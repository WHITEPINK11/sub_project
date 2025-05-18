package sub_project;

import java.time.LocalDate;

public class Customer {
    private final int id;
    private String name;
    private String email;
    private SubscriptionType subscriptionType; // Используем enum SubscriptionType вместо строки
    private LocalDate renewalDate;
    private boolean isCanceled;
    private PaymentMethod paymentMethod; // Replace String with PaymentMethod

    public Customer(int id, String name, String email, SubscriptionType subscriptionType, LocalDate renewalDate, boolean isCanceled, PaymentMethod paymentMethod) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subscriptionType = subscriptionType;
        this.renewalDate = renewalDate;
        this.isCanceled = isCanceled;
        this.paymentMethod = paymentMethod;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public boolean isCanceled() { return isCanceled; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public void setCanceled(boolean canceled) { this.isCanceled = canceled; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String toString() {
        String paymentInfo = subscriptionType == SubscriptionType.FREE ? "N/A" : paymentMethod.toString();
        return "ID: " + id + "\nName: " + name + "\nEmail: " + email + "\nType: " + subscriptionType + 
               "\nRenewal: " + renewalDate + "\nCanceled: " + (isCanceled ? "Yes" : "No") + "\nPayment Method: " + paymentInfo;
    }

    public long getDaysUntilRenewal() {
        return LocalDate.now().until(renewalDate).getDays();
    }

    public void markAsPaid() {
        if (subscriptionType == SubscriptionType.FREE) {
            System.out.println("Free subscriptions do not require payment.");
            return;
        }
        System.out.println("Subscription for customer ID " + id + " has been paid.");
    }

    public void markPaymentAsCompleted() {
        System.out.println("Payment completed for customer ID: " + id);
    }

    public void cancelCashPayment() {
        if (paymentMethod == PaymentMethod.CASH) {
            System.out.println("Cash payment canceled for customer ID: " + id);
        } else {
            System.out.println("Payment method is not cash. Cannot cancel.");
        }
    }

    public void renewSubscription() {
        if (subscriptionType != SubscriptionType.FREE) {
            renewalDate = renewalDate.plusMonths(subscriptionType.getDurationInMonths());
            System.out.println("Subscription renewed for customer ID: " + id);
        } else {
            System.out.println("Free subscriptions do not require renewal.");
        }
    }

    public String getSubscriptionPeriod() {
        LocalDate startDate = renewalDate.minusMonths(subscriptionType.getDurationInMonths());
        return "From: " + startDate + " To: " + renewalDate;
    }
}