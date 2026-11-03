package com.chaturth.jdbc;
import java.sql.*;
import java.util.Scanner;
public class EmployeeLeaveManagement {
    static final String url = "jdbc:mysql://localhost:3306/EmployeeLeaveManagement";
    static final String user = "root";
    static final String password = "chattu@2004";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

            createTables();

            while (true) {
                System.out.println("\n---------- Employee Leave Management ----------");
                System.out.println("1.Register Employee");
                System.out.println("2.Apply Leave");
                System.out.println("3.Approve leave");
                System.out.println("4.Reject Leave");
                System.out.println("5.Leave Balance");
                System.out.println("6.View Employees");
                System.out.println("7.View Leave Requests");
                System.out.println("8.Update Employee");
                System.out.println("9.Delete Employee");
                System.out.println("10.Exit");
                System.out.print("Enter Choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1:
                        registerEmployee();
                        break;
                    case 2:
                        applyLeave();
                        break;
                    case 3:
                        approveLeave();
                        break;
                    case 4:
                        rejectLeave();
                        break;
                    case 5:
                        viewBalance();
                        break;
                    case 6:
                        viewEmployees();
                        break;
                    case 7:
                        viewLeaveRequests();
                        break;
                    case 8:
                        updateEmployee();
                        break;
                    case 9:
                        deleteEmployee();
                        break;
                    case 10:
                        con.close();
                        System.out.println("Thank You");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Choice");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void createTables() throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("create table if not exists employees(" +
                "emp_id int primary key auto_increment," +
                "emp_name varchar(50)," +
                "department varchar(50))");

        st.executeUpdate("create table if not exists leave_balance(" +
                "emp_id int primary key," +
                "total_leave int," +
                "used_leave int," +
                "foreign key(emp_id) references employees(emp_id))");

        st.executeUpdate("create table if not exists leave_requests(" +
                "leave_id int primary key auto_increment," +
                "emp_id int," +
                "from_date date," +
                "to_date date," +
                "days int," +
                "reason varchar(100)," +
                "status varchar(50)," +
                "foreign key(emp_id) references employees(emp_id))");
        System.out.println("Tables Created Successfully (if not already present).");
    }

    static void registerEmployee() throws Exception {
        System.out.println("Employee Name: ");
        sc.nextLine();
        String name = sc.nextLine();

        System.out.println("Department: ");
        String dept = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("insert into employees(emp_name, department) values(?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, name);
        ps.setString(2, dept);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            int id = rs.getInt(1);
            PreparedStatement ps2 = con.prepareStatement("insert into leave_balance values (?,?,?)");
            ps2.setInt(1, id);
            ps2.setInt(2, 20);
            ps2.setInt(3, 0);
            ps2.executeUpdate();

            System.out.println("Employee Registered ID = " + id);
        }
    }

    static void applyLeave() throws Exception {
        System.out.println("Employee ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("From Date(yyyy-MM-dd): ");
        String from = sc.nextLine();

        System.out.println("TO Date(yyyy-MM-dd): ");
        String to = sc.nextLine();

        System.out.println("Number of Days: ");
        int days = sc.nextInt();
        sc.nextLine();

        System.out.println("Reason: ");
        String reason = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("insert into leave_requests(emp_id, from_date, to_date, days, reason, status) values(?,?,?,?,?,?)");
        ps.setInt(1, id);
        ps.setDate(2, Date.valueOf(from));
        ps.setDate(3, Date.valueOf(to));
        ps.setInt(4, days);
        ps.setString(5, reason);
        ps.setString(6, "Pending");
        ps.executeUpdate();
        System.out.println("Leave Request Submitted.");
    }

    static void approveLeave() throws Exception {
        System.out.println("Leave ID: ");
        int leaveID = sc.nextInt();

        con.setAutoCommit(false);

        try {
            PreparedStatement ps = con.prepareStatement("select emp_id, days from leave_requests where leave_id=?");
            ps.setInt(1, leaveID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int emp = rs.getInt("emp_id");
                int days = rs.getInt("days");

                PreparedStatement bal = con.prepareStatement("select total_leave, used_leave from leave_balance where emp_id=?");
                bal.setInt(1, emp);
                ResultSet rb = bal.executeQuery();
                if (rb.next()) {
                    int total = rb.getInt("total_leave");
                    int used = rb.getInt("used_leave");
                    if ((total - used) >= days) {
                        PreparedStatement updateReq = con.prepareStatement("update leave_requests set status ='Approved' where leave_id = ?");
                        updateReq.setInt(1, leaveID);
                        updateReq.executeUpdate();

                        PreparedStatement updateBal = con.prepareStatement("update leave_balance set used_leave = used_leave + ? where emp_id=?");
                        updateBal.setInt(1, days);
                        updateBal.setInt(2, emp);
                        updateBal.executeUpdate();
                        con.commit();
                        System.out.println("Leave Approved");
                    } else {
                        System.out.println("Insufficient Leave Balance");
                        con.rollback();
                    }
                }
            } else {
                System.out.println("Leave ID Not Found.");
            }
            con.setAutoCommit(true);
        } catch (Exception e) {
            con.rollback();
            con.setAutoCommit(true);
            e.printStackTrace();
        }
    }

    static void rejectLeave() throws Exception {
        System.out.println("Leave ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement("update leave_requests set status = 'Rejected' where leave_id=?");
        ps.setInt(1, id);

        int x = ps.executeUpdate();
        if (x > 0) {
            System.out.println("Leave Rejected.");
        } else
            System.out.println("Leave ID Not Found.");
    }

    static void viewBalance() throws Exception {
        System.out.println("Employee ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement("select total_leave, used_leave from leave_balance where emp_id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int total = rs.getInt(1);
            int used = rs.getInt(2);

            System.out.println("+--------+-------------+------------+-----------+");
            System.out.printf("| %-6s | %-11s | %-10s | %-9s |\n",
                    "Emp ID", "Total Leave", "Used", "Remaining");
            System.out.println("+--------+-------------+------------+-----------+");

            System.out.printf("| %-6d | %-11d | %-10d | %-9d |\n",
                    id, total, used, (total - used));

            System.out.println("+--------+-------------+------------+-----------+");
        } else {
            System.out.println("Employee Not Found.");
        }
    }

    static void viewEmployees() throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select emp_id, emp_name, department from employees");

        System.out.println("+--------+----------------------+----------------+");
        System.out.printf("| %-6s | %-20s | %-14s |\n",
                "ID", "Employee Name", "Department");
        System.out.println("+--------+----------------------+----------------+");

        while (rs.next()) {
            System.out.printf("| %-6d | %-20s | %-14s |\n",
                    rs.getInt("emp_id"),
                    rs.getString("emp_name"),
                    rs.getString("department"));
        }

        System.out.println("+--------+----------------------+----------------+");
    }

    static void viewLeaveRequests() throws Exception {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(
                "select * from leave_requests");

        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("| %-4s | %-5s | %-10s | %-10s | %-4s | %-15s | %-10s |\n",
                "ID", "Emp", "From", "To", "Days", "Reason", "Status");
        System.out.println("------------------------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("| %-4d | %-5d | %-10s | %-10s | %-4d | %-15s | %-10s |\n",
                    rs.getInt("leave_id"),
                    rs.getInt("emp_id"),
                    rs.getDate("from_date"),
                    rs.getDate("to_date"),
                    rs.getInt("days"),
                    rs.getString("reason"),
                    rs.getString("status"));
        }

        System.out.println("------------------------------------------------------------------------------------------------");
    }

    static void updateEmployee() throws Exception {
        System.out.println("Enter Employee ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("New Employee Name: ");
        String name = sc.nextLine();

        System.out.println("New Department: ");
        String dept = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("update employees set emp_name=?, department=? where emp_id=?");
        ps.setString(1, name);
        ps.setString(2, dept);
        ps.setInt(3, id);

        int rows = ps.executeUpdate();
        if (rows > 0)
            System.out.println("Employee Update Successfully.");
        else
            System.out.println("Employee ID Not Found.");
    }

    static void deleteEmployee() throws Exception {
        System.out.println("Enter Employee ID: ");
        int id = sc.nextInt();

        PreparedStatement ps1 = con.prepareStatement("delete from leave_requests where emp_id=?");
        ps1.setInt(1, id);
        ps1.executeUpdate();

        PreparedStatement ps2 = con.prepareStatement("delete from leave_balance where emp_id=?");
        ps2.setInt(1, id);
        ps2.executeUpdate();

        PreparedStatement ps3 = con.prepareStatement("delete from employees where emp_id=?");
        ps3.setInt(1, id);

        int rows = ps3.executeUpdate();

        if (rows > 0)
            System.out.println("Employee Deleted Successfully.");
        else
            System.out.println("Employee ID Not Found");

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM leave_requests");
        if (rs.next() && rs.getInt(1) == 0) {
            rs.close();
            st.executeUpdate("ALTER TABLE leave_requests AUTO_INCREMENT = 1");
            System.out.println("Leave ID Reset to 1.");
        } else {
            rs.close();
        }

        rs = st.executeQuery("SELECT COUNT(*) FROM employees");
        if (rs.next() && rs.getInt(1) == 0) {
            rs.close();
            st.executeUpdate("ALTER TABLE employees AUTO_INCREMENT = 1");
            System.out.println("Employee ID Reset to 1.");
        } else {
            rs.close();
        }
        st.close();
    }
}
