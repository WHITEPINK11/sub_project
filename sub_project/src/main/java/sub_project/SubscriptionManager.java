package sub_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SubscriptionManager {
    private List<Customer> customers = new ArrayList<>();
    private String fileName = "customers.csv";
    private int nextId;

    private Map<SubscriptionType, Integer> subscriptionLimits = new HashMap<>();
    private Map<SubscriptionType, Integer> subscriptionUsage = new HashMap<>();

    private LocalDate lastResetDate;

    public SubscriptionManager() {
        customers = loadCustomers();
        if (customers.isEmpty()) {
            nextId = 1;
        } else {
            nextId = customers.get(customers.size() - 1).getId() + 1;
        }

        // Initialize subscription limits
        subscriptionLimits.put(SubscriptionType.FREE, 100);
        subscriptionLimits.put(SubscriptionType.PREMIUM, 500);
        subscriptionLimits.put(SubscriptionType.GOLD, 1000);

        // Initialize subscription usage
        for (SubscriptionType type : SubscriptionType.values()) {
            subscriptionUsage.put(type, 0);
        }

        // Initialize last reset date
        lastResetDate = LocalDate.now();
        resetMonthlyUsageIfNeeded();
    }

    private void resetMonthlyUsageIfNeeded() {
        LocalDate currentDate = LocalDate.now();
        if (ChronoUnit.MONTHS.between(lastResetDate, currentDate) >= 1) {
            for (SubscriptionType type : subscriptionUsage.keySet()) {
                subscriptionUsage.put(type, 0);
            }
            lastResetDate = currentDate;
            System.out.println("Monthly usage limits have been reset.");
        }
    }

    public void addCustomer(String name, String email, SubscriptionType subscriptionType, LocalDate renewalDate, PaymentMethod paymentMethod) {
        // Reset monthly usage if needed
        resetMonthlyUsageIfNeeded();

        // Check subscription limits
        int currentUsage = subscriptionUsage.getOrDefault(subscriptionType, 0);
        int limit = subscriptionLimits.getOrDefault(subscriptionType, Integer.MAX_VALUE);

        if (currentUsage >= limit) {
            System.out.println("Превышен месячный лимит для типа подписки: " + subscriptionType);
            return;
        }

        // Increment usage
        subscriptionUsage.put(subscriptionType, currentUsage + 1);

        // Skip payment method for FREE subscriptions
        if (subscriptionType == SubscriptionType.FREE) {
            paymentMethod = null;
        }

        // Add customer
        Customer customer = new Customer(nextId++, name, email, subscriptionType, renewalDate, false, paymentMethod);
        customers.add(customer);
        saveCustomers();

        // Save subscriptions and usernames after adding a customer
        saveSubscriptionsAndUsernames();
    }

    public Customer getCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public void deleteCustomer(int id) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                customers.remove(i);
                saveCustomers();
                break;
            }
        }
    }

    public void generateReports() {
        int total = customers.size();
        int active = 0;
        int canceled = 0;
        for (Customer c : customers) {
            if (c.isCanceled()) {
                canceled++;
            } else {
                active++;
            }
        }
        System.out.println("Total Customers: " + total);
        System.out.println("Active Subscriptions: " + active);
        System.out.println("Canceled Subscriptions: " + canceled);
    }

    private List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine(); // Пропустить заголовок
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                SubscriptionType type = SubscriptionType.valueOf(parts[3]); // Use enum
                LocalDate date = LocalDate.parse(parts[4]);
                boolean canceled = Boolean.parseBoolean(parts[5]);
                PaymentMethod paymentMethod;
                if (type == SubscriptionType.FREE) {
                    paymentMethod = null; // Не устанавливаем метод оплаты для FREE подписки
                    System.out.println("Free subscriptions do not require a payment method.");
                } else {
                    paymentMethod = PaymentMethod.valueOf(parts[6]); // Use enum
                }
                list.add(new Customer(id, name, email, type, date, canceled, paymentMethod));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("File not found, starting fresh.");
        }
        return list;
    }

    public void saveCustomers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (Customer c : customers) {
                String line = c.getId() + "," + c.getName() + "," + c.getEmail() + "," +
                              c.getSubscriptionType() + "," + c.getRenewalDate() + "," + c.isCanceled() + "," + c.getPaymentMethod();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    public void exportToExcel(String excelFileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Customers");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Subscription Type");
            headerRow.createCell(4).setCellValue("Renewal Date");
            headerRow.createCell(5).setCellValue("Canceled");

            int rowNum = 1;
            for (Customer customer : customers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getSubscriptionType().name());
                row.createCell(4).setCellValue(customer.getRenewalDate().toString());
                row.createCell(5).setCellValue(customer.isCanceled());
            }

            try (FileOutputStream fileOut = new FileOutputStream(excelFileName)) {
                workbook.write(fileOut);
            }
            System.out.println("Data exported to Excel file: " + excelFileName);
        } catch (Exception e) {
            System.out.println("Error exporting to Excel: " + e.getMessage());
        }
    }

    public void importFromExcel(String excelFileName) {
        try (FileInputStream fileIn = new FileInputStream(excelFileName);
             Workbook workbook = new XSSFWorkbook(fileIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            customers.clear();
            nextId = 1;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                int id = (int) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                String email = row.getCell(2).getStringCellValue();
                SubscriptionType subscriptionType = SubscriptionType.valueOf(row.getCell(3).getStringCellValue());
                LocalDate renewalDate = LocalDate.parse(row.getCell(4).getStringCellValue());
                boolean canceled = row.getCell(5).getBooleanCellValue();
                PaymentMethod paymentMethod;
                if (subscriptionType == SubscriptionType.FREE) {
                    paymentMethod = PaymentMethod.CARD; // Default to CARD or set to N/A equivalent
                    System.out.println("Free subscriptions do not require a payment method.");
                } else {
                    paymentMethod = PaymentMethod.valueOf(row.getCell(6).getStringCellValue());
                }

                customers.add(new Customer(id, name, email, subscriptionType, renewalDate, canceled, paymentMethod));
                nextId = Math.max(nextId, id + 1);
            }
            saveCustomers();
            System.out.println("Data imported from Excel file: " + excelFileName);
        } catch (Exception e) {
            System.out.println("Error importing from Excel: " + e.getMessage());
        }
    }

    public void saveSubscriptionsAndUsernames() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("subscriptions_and_usernames.csv"))) {
            writer.write("Username,SubscriptionType\n"); // Заголовок
            for (Customer customer : customers) {
                writer.write(customer.getName() + "," + customer.getSubscriptionType() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving subscriptions and usernames: " + e.getMessage());
        }
    }
}
