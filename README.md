# sub_project
# Subscription Management System

## Overview
This project is a Subscription Management System designed to manage customer subscriptions. It allows users to add, view, update, and delete customer subscriptions, as well as generate reports and handle subscription data import/export.

## Features
- Add new customers with subscription details.
- View all customers or a specific customer by ID.
- Update customer details (name, email, subscription type, renewal date, etc.).
- Delete customers.
- Generate reports on active and canceled subscriptions.
- Import and export subscription data to/from Excel files.
- Handle different subscription types (FREE, PREMIUM, GOLD) with monthly usage limits.
- Automatically reset monthly usage limits at the start of a new month.

## Subscription Types
1. **FREE**: No payment method required, limited to 100 operations per month.
2. **PREMIUM**: Requires a payment method, limited to 500 operations per month.
3. **GOLD**: Requires a payment method, limited to 1000 operations per month.

## Payment Methods
- CARD
- PAYPAL
- CASH

## How to Run
1. Ensure you have Java installed on your system.
2. Compile the project using Maven:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   java -cp target/classes sub_project.Main
   ```

## File Structure
- `src/main/java/sub_project/`: Contains the main Java source files.
- `src/test/java/`: Contains test files.
- `customers.csv`: Stores customer data.
- `subscriptions_and_usernames.csv`: (Optional) Stores usernames and their subscription types.

## Notes
- FREE subscriptions do not require a payment method.
- Monthly usage limits are reset automatically at the start of a new month.
- Ensure the `customers.csv` file exists in the project directory for data persistence.

## Author
Developed by WHITEPINK.
