package com.chaturth.jdbc;
import java.sql.*;
import java.util.Scanner;

public class BankingManagementSystem {

    static final String url = "jdbc:mysql://localhost:3306/BankingManagementSystem";
    static final String user = "root";
    static final String password = "chattu@2004";
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        createTables();

        while (true) {
            System.out.println("\n========== BANKING MANAGEMENT SYSTEM ==========");
            System.out.println("1.Create Customer Account");
            System.out.println("2.View Customers Accounts");
            System.out.println("3.Update Customer Account");
            System.out.println("4.Delete Customer Account");
            System.out.println("5.Deposit Money");
            System.out.println("6.WithDraw Money");
            System.out.println("7.Transfer Money");
            System.out.println("8.Transaction History");
            System.out.println("9.Exit");
            System.out.println("==============================");
            System.out.print("Enter Your Choice : ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    viewCustomers();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    deposit();
                    break;
                case 6:
                    withdraw();
                    break;
                case 7:
                    fundTransfer();
                    break;
                case 8:
                    transactionHistory();
                    break;
                case 9:
                    System.out.println("Thank You");
                    System.exit(0);
                default:
                    System.out.println("Invaild Choice");
            }
        }
    }

    static void createTables() {
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {

            st.executeUpdate("create table if not exists customers(" +
                    "customer_id int primary key auto_increment," +
                    "customer_name varchar(50)," +
                    "email varchar(100)," +
                    "phone varchar(10)," +
                    "address varchar(100)," +
                    "city varchar(50)," +
                    "state varchar(50)," +
                    "pincode varchar(6))");

            st.executeUpdate("create table if not exists accounts(" +
                    "account_no int primary key auto_increment," +
                    "customer_id int," +
                    "balance decimal(10,2)," +
                    "foreign key(customer_id) references customers(customer_id))");

            st.executeUpdate("create table if not exists transactions(" +
                    "transaction_id int primary key auto_increment," +
                    "account_no int," +
                    "transaction_type varchar(20)," +
                    "amount decimal(10,2)," +
                    "transaction_date timestamp default current_timestamp," +
                    "foreign key(account_no) references accounts(account_no))");

            System.out.println("=================================");
            System.out.println("Tables Created Successfully.");
            System.out.println("=================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void createAccount() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {

            sc.nextLine();
            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();

            String email;
            while (true) {
                System.out.print("Enter Email ID: ");
                email = sc.nextLine();

                if (email.endsWith("@gmail.com")) {
                    break;
                } else {
                    System.out.println("Invalid Email");
                    System.out.print("Email must contain @gmail.com");
                }
            }

            String phone;
            while (true) {
                System.out.print("Enter Phone Number: ");
                phone = sc.nextLine();

                if (phone.matches("[6789][0-9]{9}")) {
                    break;
                } else {
                    System.out.println("Invalid Phone Number");
                }
            }

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            System.out.print("Enter City: ");
            String city = sc.nextLine();

            System.out.print("Enter State: ");
            String state = sc.nextLine();

            String pincode;
            while (true) {
                System.out.print("Enter Pincode: ");
                pincode = sc.nextLine();

                if (pincode.matches("\\d{6}")) {
                    break;
                } else {
                    System.out.println("Pincode must contains exactly 6 digits");
                }
            }

            double balance;
            while (true) {
                System.out.print("Enter Initial Deposit(Minimum Rs.5000): ");
                balance = sc.nextDouble();

                if (balance >= 5000) {
                    break;
                } else {
                    System.out.println("Initial Deposit should be at least Rs.5000");
                }
            }

            PreparedStatement ps1 = con.prepareStatement("insert into customers(customer_name, email, phone, address, city, state, pincode) values (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, name);
            ps1.setString(2, email);
            ps1.setString(3, phone);
            ps1.setString(4, address);
            ps1.setString(5, city);
            ps1.setString(6, state);
            ps1.setString(7, pincode);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();

            if (rs.next()) {
                int customerId = rs.getInt(1);
                PreparedStatement ps2 = con.prepareStatement("insert into accounts(customer_id, balance) values(?,?) ");
                ps2.setInt(1, customerId);
                ps2.setDouble(2, balance);
                ps2.executeUpdate();

                System.out.println("\n=================================");
                System.out.println("Account Created Successfully!");
                System.out.println("Customer ID : " + customerId);
                System.out.println("Opening Balance : Rs." + balance);
                System.out.println("Minimum Balance : Rs.1000");
                System.out.println("=================================");

                ps2.close();
            }
            rs.close();
            ps1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewCustomers() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "select * from customers";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n================ CUSTOMER DETAILS ================");

            while (rs.next()) {
                System.out.println("Customer ID   : " + rs.getInt("customer_id"));
                System.out.println("Name          : " + rs.getString("customer_name"));
                System.out.println("Email         : " + rs.getString("email"));
                System.out.println("Phone         : " + rs.getString("phone"));
                System.out.println("Address       : " + rs.getString("address"));
                System.out.println("City          : " + rs.getString("city"));
                System.out.println("State         : " + rs.getString("state"));
                System.out.println("Pincode       : " + rs.getString("pincode"));
                System.out.println("-----------------------------------------------");
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateCustomer() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.print("Enter Customer ID: ");
            int customerId = sc.nextInt();
            sc.nextLine();

            String name;
            while (true) {
                System.out.print("Enter New Customer Name: ");
                name = sc.nextLine();

                if (name.matches("[A-Za-z ]+")) {
                    break;
                } else {
                    System.out.println("Invalid Name");
                }
            }

            String email;
            while (true) {
                System.out.print("Enter New Email: ");
                email = sc.nextLine();

                if (email.endsWith("@gmail.com")) {
                    break;
                } else {
                    System.out.println("Email must ends with @gmail.com");
                }
            }

            String phone;
            while (true) {
                System.out.print("Enter New Phone Number: ");
                phone = sc.nextLine();

                if (phone.matches("[6789][0-9]{9}")) {
                    break;
                } else {
                    System.out.println("Phone number must be 10 digits and starts with 6,7,8 or 9.");
                }
            }

            System.out.print("Enter New Address: ");
            String address = sc.nextLine();

            System.out.print("Enter New City: ");
            String city = sc.nextLine();

            System.out.print("Enter New state: ");
            String state = sc.nextLine();

            String pincode;
            while (true) {
                System.out.print("Enter New Pincode: ");
                pincode = sc.nextLine();

                if (pincode.matches("\\d{6}")) {
                    break;
                } else {
                    System.out.println("Pincode must be exactly 6 digits.");
                }
            }

            String sql = "update customers set customer_name=?, email=?, phone=?, address=?, city=?, state=?, pincode=? where customer_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, city);
            ps.setString(6, state);
            ps.setString(7, pincode);
            ps.setInt(8, customerId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Customer Details Update Successfully.");
            } else {
                System.out.println("Customer ID Not Found.");
            }
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deleteCustomer() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.println("Enter Customer ID to Delete: ");
            int customerId = sc.nextInt();

            String sql1 = "delete from transactions where account_no in (select account_no from accounts where customer_id=?)";
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setInt(1, customerId);
            ps1.executeUpdate();

            String sql2 = "delete from accounts where customer_id=?";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            ps2.setInt(1, customerId);
            ps2.executeUpdate();

            String sql3 = "delete from customers where customer_id=?";
            PreparedStatement ps3 = con.prepareStatement(sql3);
            ps3.setInt(1, customerId);
            int customerDeleted = ps3.executeUpdate();

            if (customerDeleted > 0) {
                System.out.println("Customer Deleted Successfully.");
            } else {
                System.out.println("Customer ID Not Found.");
            }
            ps1.close();
            ps2.close();
            ps3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deposit() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.print("Enter Account Number: ");
            int accountNo = sc.nextInt();

            System.out.print("Enter Deposit Amount: ");
            double amount = sc.nextDouble();

            if (amount <= 0) {
                System.out.println("Invalid Deposit Amount.");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement("select balance from accounts where account_no=?");
            ps1.setInt(1, accountNo);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balance = balance + amount;

                PreparedStatement ps2 = con.prepareStatement("update accounts set balance=? where account_no=?");
                ps2.setDouble(1, balance);
                ps2.setInt(2, accountNo);
                ps2.executeUpdate();

                PreparedStatement ps3 = con.prepareStatement("insert into transactions(account_no, transaction_type, amount) values (?,?,?)");
                ps3.setInt(1, accountNo);
                ps3.setString(2, "Deposit");
                ps3.setDouble(3, amount);
                ps3.executeUpdate();

                System.out.println("--------------------------------");
                System.out.println("Deposit Successful.");
                System.out.println("Deposited Amount : Rs." + amount);
                System.out.println("Current Balance  : Rs." + balance);
                System.out.println("--------------------------------");

                ps2.close();
                ps3.close();
            } else {
                System.out.println("Account Number Not Found.");
            }
            rs.close();
            ps1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void withdraw() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.print("Enter Account Number : ");
            int accountNo = sc.nextInt();

            System.out.print("Enter Withdrawal Amount: ");
            double amount = sc.nextDouble();

            if (amount <= 0) {
                System.out.println("Invalid Withdrawal Amount.");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement("select balance from accounts where account_no=?");
            ps1.setInt(1, accountNo);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");

                if ((balance - amount) < 1000) {
                    System.out.println("Withdrawal Failed");
                    System.out.println("Minimum Balance of Rs.1000 must be maintained.");
                } else {
                    double newBalance = balance - amount;

                    PreparedStatement ps2 = con.prepareStatement("update accounts set balance=? where account_no=?");
                    ps2.setDouble(1, newBalance);
                    ps2.setInt(2, accountNo);
                    ps2.executeUpdate();

                    PreparedStatement ps3 = con.prepareStatement("insert into transactions(account_no, transaction_type, amount) values (?,?,?)");
                    ps3.setInt(1, accountNo);
                    ps3.setString(2, "Withdraw");
                    ps3.setDouble(3, amount);
                    ps3.executeUpdate();

                    System.out.println("--------------------------------");
                    System.out.println("Withdrawal Successful.");
                    System.out.println("Withdrawn Amount : Rs." + amount);
                    System.out.println("Remaining Balance : Rs." + newBalance);
                    System.out.println("--------------------------------");

                    ps2.close();
                    ps3.close();
                }
            } else {
                System.out.println("Account Number Not Found.");
            }
            rs.close();
            ps1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fundTransfer() {

        Connection con = null;

        try {

            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);

            System.out.print("Enter Sender Account Number : ");
            int sender = sc.nextInt();

            System.out.print("Enter Receiver Account Number : ");
            int receiver = sc.nextInt();

            System.out.print("Enter Transfer Amount : ");
            double amount = sc.nextDouble();

            if (amount <= 0) {
                System.out.println("Invalid Amount.");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement(
                    "select balance from accounts where account_no=?");
            ps1.setInt(1, sender);

            ResultSet rs1 = ps1.executeQuery();

            if (!rs1.next()) {
                System.out.println("Sender Account Not Found.");
                return;
            }

            double senderBalance = rs1.getDouble("balance");

            if ((senderBalance - amount) < 1000) {
                System.out.println("Transfer Failed! Minimum Balance of Rs.1000 must be maintained.");
                return;
            }

            // Check Receiver Account
            PreparedStatement ps2 = con.prepareStatement(
                    "select balance from accounts where account_no=?");
            ps2.setInt(1, receiver);

            ResultSet rs2 = ps2.executeQuery();

            if (!rs2.next()) {
                System.out.println("Receiver Account Not Found.");
                return;
            }

            double receiverBalance = rs2.getDouble("balance");

            // Deduct from Sender
            PreparedStatement ps3 = con.prepareStatement("update accounts set balance=? where account_no=?");
            ps3.setDouble(1, senderBalance - amount);
            ps3.setInt(2, sender);
            ps3.executeUpdate();

            // Add to Receiver
            PreparedStatement ps4 = con.prepareStatement(
                    "update accounts set balance=? where account_no=?");
            ps4.setDouble(1, receiverBalance + amount);
            ps4.setInt(2, receiver);
            ps4.executeUpdate();

            // Sender Transaction
            PreparedStatement ps5 = con.prepareStatement(
                    "insert into transactions(account_no,transaction_type,amount) values(?,?,?)");
            ps5.setInt(1, sender);
            ps5.setString(2, "Transfer Sent");
            ps5.setDouble(3, amount);
            ps5.executeUpdate();

            // Receiver Transaction
            PreparedStatement ps6 = con.prepareStatement(
                    "insert into transactions(account_no,transaction_type,amount) values(?,?,?)");
            ps6.setInt(1, receiver);
            ps6.setString(2, "Transfer Received");
            ps6.setDouble(3, amount);
            ps6.executeUpdate();

            con.commit();

            System.out.println("--------------------------------");
            System.out.println("Fund Transfer Successful.");
            System.out.println("Transferred Amount : Rs." + amount);
            System.out.println("--------------------------------");

            rs1.close();
            rs2.close();
            ps1.close();
            ps2.close();
            ps3.close();
            ps4.close();
            ps5.close();
            ps6.close();

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                    System.out.println("Transfer Failed! Transaction Rolled Back.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void transactionHistory() {

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            String sql = "select * from transactions order by transaction_date desc";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n============== TRANSACTION HISTORY ==============");

            if (!rs.isBeforeFirst()) {
                System.out.println("No Transactions Found.");
            } else {

                while (rs.next()) {

                    System.out.println("Transaction ID   : " + rs.getInt("transaction_id"));
                    System.out.println("Account Number   : " + rs.getInt("account_no"));
                    System.out.println("Transaction Type : " + rs.getString("transaction_type"));
                    System.out.println("Amount           : Rs." + rs.getDouble("amount"));
                    System.out.println("Date & Time      : " + rs.getTimestamp("transaction_date"));
                    System.out.println("-----------------------------------------------");
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
