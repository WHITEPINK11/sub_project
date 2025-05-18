package sub_project;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static SubscriptionManager manager = new SubscriptionManager();
    private static Scanner scanner = new Scanner(System.in);
    private static String role;

    public static void main(String[] args) {
        System.out.print("Enter role (admin/user): ");
        role = scanner.nextLine().toLowerCase();

        if (!role.equals("admin") && !role.equals("user")) {
            System.out.println("Invalid role. Exiting.");
            return;
        }

        while (true) {
            System.out.println("\n1. Add Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. View Customer");
            if (role.equals("admin")) {
                System.out.println("4. Update Customer");
                System.out.println("5. Delete Customer");
                System.out.println("6. Reports");
                System.out.println("7. Import from Excel");
                System.out.println("8. Export to Excel");
            }
            System.out.println("9. Cancel Subscription");
            System.out.println("10. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                addCustomer();
            } else if (choice == 2) {
                viewAllCustomers();
            } else if (choice == 3) {
                viewCustomer();
            } else if (choice == 4 && role.equals("admin")) {
                updateCustomer();
            } else if (choice == 5 && role.equals("admin")) {
                deleteCustomer();
            } else if (choice == 6 && role.equals("admin")) {
                manager.generateReports();
            } else if (choice == 7 && role.equals("admin")) {
                importFromExcel();
            } else if (choice == 8 && role.equals("admin")) {
                exportToExcel();
            } else if (choice == 9) {
                cancelSubscription();
            } else if (choice == 10) {
                System.out.println("Возврат в предыдущее меню...");
            } else {
                System.out.println("Wrong choice or insufficient privileges.");
            }
        }
    }

    private static void addCustomer() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Bad email, try again.");
            return;
        }
        System.out.println("Select Subscription Type:");
        for (SubscriptionType type : SubscriptionType.values()) {
            System.out.println(type.ordinal() + 1 + ". " + type + " ($" + type.getPrice() + ")");
        }
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        SubscriptionType subscriptionType = SubscriptionType.values()[typeChoice - 1];

        System.out.print("Renewal Date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        LocalDate date;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(dateStr, formatter);
            if (date.isBefore(LocalDate.now())) {
                System.out.println("Date must be in future.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Wrong date format.");
            return;
        }

        PaymentMethod paymentMethod = null;
        if (subscriptionType != SubscriptionType.FREE) {
            System.out.println("Select Payment Method:");
            for (PaymentMethod method : PaymentMethod.values()) {
                System.out.println(method.ordinal() + 1 + ". " + method);
            }
            int methodChoice = scanner.nextInt();
            scanner.nextLine();
            paymentMethod = PaymentMethod.values()[methodChoice - 1];
        }

        manager.addCustomer(name, email, subscriptionType, date, paymentMethod);
        System.out.println("Customer added.");
    }

    private static void viewAllCustomers() {
        List<Customer> customers = manager.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers.");
        } else {
            for (Customer c : customers) {
                System.out.println(c);
                System.out.println();
            }
        }
    }

    private static void viewCustomer() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        Customer c = manager.getCustomerById(id);
        if (c == null) {
            System.out.println("Not found.");
        } else {
            System.out.println(c);
            System.out.println("Days until renewal: " + c.getDaysUntilRenewal());
        }
    }

    private static void updateCustomer() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Customer c = manager.getCustomerById(id);
        if (c == null) {
            System.out.println("Not found.");
            return;
        }
        System.out.println("1. Name\n2. Email\n3. Type\n4. Renewal\n5. Cancel");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 1) {
            System.out.print("New Name: ");
            c.setName(scanner.nextLine());
        } else if (choice == 2) {
            System.out.print("New Email: ");
            String email = scanner.nextLine();
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Bad email.");
                return;
            }
            c.setEmail(email);
        } else if (choice == 3) {
            System.out.println("Select New Subscription Type:");
            for (SubscriptionType type : SubscriptionType.values()) {
                System.out.println(type.ordinal() + 1 + ". " + type + " ($" + type.getPrice() + ")");
            }
            int typeChoice = scanner.nextInt();
            scanner.nextLine();
            SubscriptionType subscriptionType = SubscriptionType.values()[typeChoice - 1];
            c.setSubscriptionType(subscriptionType);
        } else if (choice == 4) {
            System.out.print("New Renewal (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateStr, formatter);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Date must be in future.");
                    return;
                }
                c.setRenewalDate(date);
            } catch (Exception e) {
                System.out.println("Wrong date format.");
                return;
            }
        } else if (choice == 5) {
            System.out.print("Cancel? (yes/no): ");
            String answer = scanner.nextLine();
            c.setCanceled(answer.equalsIgnoreCase("yes"));
        } else {
            System.out.println("Wrong choice.");
            return;
        }
        manager.saveCustomers();
        System.out.println("Updated.");
    }

    private static void deleteCustomer() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        manager.deleteCustomer(id);
        System.out.println("Deleted.");
    }

    private static void importFromExcel() {
        System.out.print("Enter Excel file name to import: ");
        String fileName = scanner.nextLine();
        manager.importFromExcel(fileName);
    }

    private static void exportToExcel() {
        System.out.print("Enter Excel file name to export: ");
        String fileName = scanner.nextLine();
        manager.exportToExcel(fileName);
    }

    private static void cancelSubscription() {
        System.out.print("Enter ID to cancel subscription: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Customer customer = manager.getCustomerById(id);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        customer.setCanceled(true);
        manager.saveCustomers();
        System.out.println("Subscription canceled for customer ID: " + id);
    }
}
