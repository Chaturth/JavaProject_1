package com.chaturth.jdbc;
import java.sql.*;
import java.util.Scanner;
public class VechicleServiceCenter {
    static final String url = "jdbc:mysql://localhost:3306/VechicleServiceCenterManagement";
    static final String user = "root";
    static final String password = "chattu@2004";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);

            Statement st = con.createStatement();

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS customers (" +
                            "customer_id INT PRIMARY KEY AUTO_INCREMENT," +
                            "customer_name VARCHAR(50)," +
                            "phone VARCHAR(10),"+
                            "email VARCHAR(50),"+
                            "address VARCHAR(100))"
            );

            // Create Vehicles Table
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS vehicles (" +
                            "vehicle_id INT PRIMARY KEY AUTO_INCREMENT," +
                            "customer_id INT," +
                            "vehicle_number VARCHAR(20)," +
                            "model VARCHAR(50)," +
                            "FOREIGN KEY(customer_id) REFERENCES customers(customer_id))"
            );

            // Create Service Records Table
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS service_records (" +
                            "service_id INT PRIMARY KEY AUTO_INCREMENT," +
                            "vehicle_id INT," +
                            "service_date DATE," +
                            "service_type VARCHAR(50)," +
                            "cost DOUBLE," +
                            "FOREIGN KEY(vehicle_id) REFERENCES vehicles(vehicle_id))"
            );

            System.out.println("Tables Created Successfully...");

            while(true) {

                System.out.println("\n===== Vehicle Service Center =====");
                System.out.println("1. Customer Registration");
                System.out.println("2. Vehicle Registration");
                System.out.println("3. Service Booking");
                System.out.println("4. Service History");
                System.out.println("5. Search Customer Vehicle");
                System.out.println("6. View Customers");
                System.out.println("7. View Vehicle Registration");
                System.out.println("8. Update Customer");
                System.out.println("9. Delete Customer");
                System.out.println("10. Exit");
                System.out.println("\n===================");
                System.out.print("Enter Choice: ");

                int ch = sc.nextInt();

                switch(ch) {

                    case 1:
                        customerRegistration(con, sc);
                        break;

                    case 2:
                        vehicleRegistration(con, sc);
                        break;

                    case 3:
                        serviceBooking(con, sc);
                        break;

                    case 4:
                        serviceHistory(con);
                        break;

                    case 5:
                        searchVehicle(con, sc);
                        break;

                    case 6:
                        viewCustomers(con);
                        break;

                    case 7:
                        viewVehicleRegistration(con);
                        break;

                    case 8:
                        updateCustomer(con, sc);
                        break;

                    case 9:
                        deleteCustomer(con, sc);
                        break;

                    case 10:
                        sc.close();
                        con.close();
                        System.out.println("Thank You");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Choice");
                        break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void customerRegistration(Connection con, Scanner sc) throws Exception{
        sc.nextLine();

        System.out.println("Customer Name: ");
        String name = sc.nextLine();

        String phone;

        while (true) {
            System.out.print("Phone Number: ");
            phone = sc.nextLine();

            if (phone.matches("[6789]\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid Phone Number! It must be 10 digits and start with 6, 7, 8, or 9.");
            }
        }

        String email;

        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                break;
            } else {
                System.out.println("Invalid Email!");
            }
        }

        System.out.print("Address: ");
        String address = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO customers(customer_name, phone, email, address) VALUES(?,?,?,?)");

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, address);

        ps.executeUpdate();

        System.out.println("Customer Registered Successfully.");
    }

    public static void vehicleRegistration(Connection con, Scanner sc) throws Exception {

        System.out.print("Customer ID: ");
        int cid = sc.nextInt();
        sc.nextLine();

        PreparedStatement check = con.prepareStatement(
                "SELECT * FROM customers WHERE customer_id=?");
        check.setInt(1, cid);

        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            System.out.println("Customer ID does not exist.");
            return;
        }

        String number;

        while (true) {
            System.out.print("Vehicle Number (Example: KA01AB1234): ");
            number = sc.nextLine().toUpperCase();

            if (number.matches("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$")) {
                break;
            } else {
                System.out.println("Invalid Vehicle Number!");
            }
        }

        System.out.print("Vehicle Model: ");
        String model = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO vehicles(customer_id, vehicle_number, model) VALUES(?,?,?)");

        ps.setInt(1, cid);
        ps.setString(2, number);
        ps.setString(3, model);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Vehicle Registered Successfully.");
        } else {
            System.out.println("Registration Failed.");
        }
    }

    public static void serviceBooking(Connection con, Scanner sc) throws Exception {

        System.out.print("Vehicle ID: ");
        int vid = sc.nextInt();
        sc.nextLine();

        PreparedStatement check = con.prepareStatement(
                "SELECT * FROM vehicles WHERE vehicle_id=?");
        check.setInt(1, vid);

        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            System.out.println("Vehicle ID does not exist.");
            return;
        }

        System.out.print("Service Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Service Type: ");
        String type = sc.nextLine();

        System.out.print("Cost: ");
        double cost = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO service_records(vehicle_id,service_date,service_type,cost) VALUES(?,?,?,?)");

        ps.setInt(1, vid);
        ps.setDate(2, Date.valueOf(date));
        ps.setString(3, type);
        ps.setDouble(4, cost);

        ps.executeUpdate();

        System.out.println("Service Booked Successfully");
    }

    public static void serviceHistory(Connection con) throws Exception {

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(
                "SELECT c.customer_name,v.vehicle_number,s.service_date,s.service_type,s.cost " +
                        "FROM customers c " +
                        "JOIN vehicles v ON c.customer_id=v.customer_id " +
                        "JOIN service_records s ON v.vehicle_id=s.vehicle_id");

        boolean found = false;

        System.out.println("\nService History");

        while(rs.next()) {

            found = true;

            System.out.println("-------------------------");
            System.out.println("Customer : " + rs.getString(1));
            System.out.println("Vehicle  : " + rs.getString(2));
            System.out.println("Date     : " + rs.getDate(3));
            System.out.println("Service  : " + rs.getString(4));
            System.out.println("Cost     : " + rs.getDouble(5));
        }

        if (!found) {
            System.out.println("No Service Records Found.");
        }
    }

    public static void searchVehicle(Connection con, Scanner sc) throws Exception {

        System.out.print("Enter Vehicle Number: ");
        String search = sc.nextLine().toUpperCase();

        PreparedStatement ps = con.prepareStatement(
                "SELECT c.customer_name,v.vehicle_number,v.model " +
                        "FROM customers c JOIN vehicles v " +
                        "ON c.customer_id=v.customer_id " +
                        "WHERE v.vehicle_number=?");

        ps.setString(1, search);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            System.out.println("Customer Name : " + rs.getString(1));
            System.out.println("Vehicle Number: " + rs.getString(2));
            System.out.println("Model         : " + rs.getString(3));
        }
        else {
            System.out.println("Vehicle Not Found");
        }
    }

    public static void viewCustomers(Connection con) throws Exception {

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM customers");

        boolean found = false;

        System.out.println("\n----- Customers -----");

        while (rs.next()) {
            found = true;
            System.out.println("Customer ID   : " + rs.getInt("customer_id"));
            System.out.println("Name          : " + rs.getString("customer_name"));
            System.out.println("Phone         : " + rs.getString("phone"));
            System.out.println("Email         : " + rs.getString("email"));
            System.out.println("Address       : " + rs.getString("address"));
            System.out.println("-----------------------------------");
        }

        if (!found) {
            System.out.println("No Customers Found.");
        }
    }

    public static void viewVehicleRegistration(Connection con) throws Exception {

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(
                "SELECT c.customer_name, v.vehicle_id, v.vehicle_number, v.model " +
                        "FROM customers c JOIN vehicles v " +
                        "ON c.customer_id = v.customer_id");

        boolean found = false;

        System.out.println("\n----- Vehicle Registration -----");

        while (rs.next()) {
            found = true;
            System.out.println("Vehicle ID     : " + rs.getInt("vehicle_id"));
            System.out.println("Customer Name  : " + rs.getString("customer_name"));
            System.out.println("Vehicle Number : " + rs.getString("vehicle_number"));
            System.out.println("Vehicle Model  : " + rs.getString("model"));
            System.out.println("-----------------------------------");
        }

        if (!found) {
            System.out.println("No Vehicle Registration Found.");
        }
    }

    public static void updateCustomer(Connection con, Scanner sc) throws Exception {

        System.out.print("Enter Customer ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("New Customer Name: ");
        String name = sc.nextLine();

        String phone;

        while (true) {
            System.out.print("New Phone Number: ");
            phone = sc.nextLine();

            if (phone.matches("[6789]\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid Phone Number!");
            }
        }

        String email;

        while (true) {
            System.out.print("New Email: ");
            email = sc.nextLine();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                break;
            } else {
                System.out.println("Invalid Email!");
            }
        }

        System.out.print("New Address: ");
        String address = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE customers SET customer_name=?, phone=?, email=?, address=? WHERE customer_id=?");

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, address);
        ps.setInt(5, id);

        int rows = ps.executeUpdate();

        if(rows > 0)
            System.out.println("Customer Updated Successfully.");
        else
            System.out.println("Customer ID Not Found.");
    }

    public static void deleteCustomer(Connection con, Scanner sc) throws Exception {

        System.out.print("Enter Customer ID: ");
        int id = sc.nextInt();

        PreparedStatement ps1 = con.prepareStatement(
                "DELETE FROM service_records WHERE vehicle_id IN " +
                        "(SELECT vehicle_id FROM vehicles WHERE customer_id=?)");
        ps1.setInt(1, id);
        ps1.executeUpdate();

        PreparedStatement ps2 = con.prepareStatement(
                "DELETE FROM vehicles WHERE customer_id=?");
        ps2.setInt(1, id);
        ps2.executeUpdate();

        PreparedStatement ps3 = con.prepareStatement(
                "DELETE FROM customers WHERE customer_id=?");
        ps3.setInt(1, id);
        int rows = ps3.executeUpdate();

        if(rows > 0)
            System.out.println("Customer Deleted Successfully.");
        else
            System.out.println("Customer ID Not Found.");
    }
}
