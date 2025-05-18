package sub_project;

public enum SubscriptionType {
    FREE(0,0 ),
    PREMIUM(9.99, 1),
    GOLD(19.99, 1); // Renamed from Family to Gold

    private final double price;
    private final int durationInMonths;

    SubscriptionType(double price, int durationInMonths) {
        this.price = price;
        this.durationInMonths = durationInMonths;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }
}
