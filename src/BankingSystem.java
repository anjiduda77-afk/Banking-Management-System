import java.sql.*;
import java.util.Scanner;

public class BankingSystem {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = sc.nextInt();

            if (choice == 1) createAccount();
            else if (choice == 2) login();
            else break;
        }
    }

    static void createAccount() throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Enter Name: ");
        String name = sc.next();

        System.out.print("Enter Account No: ");
        String acc = sc.next();

        System.out.print("Enter PIN: ");
        String pin = sc.next();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(name,account_no,pin,balance) VALUES(?,?,?,?)");

        ps.setString(1, name);
        ps.setString(2, acc);
        ps.setString(3, pin);
        ps.setDouble(4, 0);

        ps.executeUpdate();
        System.out.println("Account Created Successfully");
    }

    static void login() throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Enter Account No: ");
        String acc = sc.next();

        System.out.print("Enter PIN: ");
        String pin = sc.next();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE account_no=? AND pin=?");

        ps.setString(1, acc);
        ps.setString(2, pin);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Login Successful");
            dashboard(acc);
        } else {
            System.out.println("Invalid Details");
        }
    }

    static void dashboard(String acc) throws Exception {

        while (true) {
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Balance");
            System.out.println("4. Logout");

            int choice = sc.nextInt();

            if (choice == 1) deposit(acc);
            else if (choice == 2) withdraw(acc);
            else if (choice == 3) balance(acc);
            else break;
        }
    }

    static void deposit(String acc) throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Enter Amount: ");
        double amt = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET balance=balance+? WHERE account_no=?");

        ps.setDouble(1, amt);
        ps.setString(2, acc);

        ps.executeUpdate();
        System.out.println("Amount Deposited");
    }

    static void withdraw(String acc) throws Exception {
        Connection con = DBConnection.getConnection();

        System.out.print("Enter Amount: ");
        double amt = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET balance=balance-? WHERE account_no=?");

        ps.setDouble(1, amt);
        ps.setString(2, acc);

        ps.executeUpdate();
        System.out.println("Amount Withdrawn");
    }

    static void balance(String acc) throws Exception {
        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM users WHERE account_no=?");

        ps.setString(1, acc);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Balance: " + rs.getDouble("balance"));
        }
    }
}