import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BankingGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentAccount;

    public BankingGUI() {
        setTitle("Banking System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels to CardLayout
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createAccountPanel(), "CreateAccount");
        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createDashboardPanel(), "Dashboard");

        add(mainPanel);
        
        // Show Home panel initially
        cardLayout.show(mainPanel, "Home");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnCreate = new JButton("Create Account");
        JButton btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");

        btnCreate.addActionListener(e -> cardLayout.show(mainPanel, "CreateAccount"));
        btnLogin.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        btnExit.addActionListener(e -> System.exit(0));

        gbc.gridy = 0; panel.add(btnCreate, gbc);
        gbc.gridy = 1; panel.add(btnLogin, gbc);
        gbc.gridy = 2; panel.add(btnExit, gbc);

        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtName = new JTextField(15);
        JTextField txtAcc = new JTextField(15);
        JPasswordField txtPin = new JPasswordField(15);
        JButton btnSubmit = new JButton("Create");
        JButton btnBack = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Account No:"), gbc);
        gbc.gridx = 1; panel.add(txtAcc, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1; panel.add(txtPin, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSubmit);
        btnPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        btnSubmit.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users(name,account_no,pin,balance) VALUES(?,?,?,?)");
                ps.setString(1, txtName.getText());
                ps.setString(2, txtAcc.getText());
                ps.setString(3, new String(txtPin.getPassword()));
                ps.setDouble(4, 0);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account Created Successfully");
                txtName.setText(""); txtAcc.setText(""); txtPin.setText("");
                cardLayout.show(mainPanel, "Home");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtAcc = new JTextField(15);
        JPasswordField txtPin = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnBack = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Account No:"), gbc);
        gbc.gridx = 1; panel.add(txtAcc, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("PIN:"), gbc);
        gbc.gridx = 1; panel.add(txtPin, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnLogin);
        btnPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        btnLogin.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE account_no=? AND pin=?");
                ps.setString(1, txtAcc.getText());
                ps.setString(2, new String(txtPin.getPassword()));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    currentAccount = txtAcc.getText();
                    txtAcc.setText(""); txtPin.setText("");
                    cardLayout.show(mainPanel, "Dashboard");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Details");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");
        JButton btnBalance = new JButton("Check Balance");
        JButton btnLogout = new JButton("Logout");

        btnDeposit.addActionListener(e -> {
            String amtStr = JOptionPane.showInputDialog(this, "Enter Amount:");
            if (amtStr != null && !amtStr.isEmpty()) {
                try (Connection con = DBConnection.getConnection()) {
                    double amt = Double.parseDouble(amtStr);
                    PreparedStatement ps = con.prepareStatement("UPDATE users SET balance=balance+? WHERE account_no=?");
                    ps.setDouble(1, amt);
                    ps.setString(2, currentAccount);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Amount Deposited");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        btnWithdraw.addActionListener(e -> {
            String amtStr = JOptionPane.showInputDialog(this, "Enter Amount:");
            if (amtStr != null && !amtStr.isEmpty()) {
                try (Connection con = DBConnection.getConnection()) {
                    double amt = Double.parseDouble(amtStr);
                    
                    // Check balance first
                    PreparedStatement psBal = con.prepareStatement("SELECT balance FROM users WHERE account_no=?");
                    psBal.setString(1, currentAccount);
                    ResultSet rsBal = psBal.executeQuery();
                    
                    if (rsBal.next()) {
                        double currentBal = rsBal.getDouble("balance");
                        if (currentBal >= amt) {
                            PreparedStatement ps = con.prepareStatement("UPDATE users SET balance=balance-? WHERE account_no=?");
                            ps.setDouble(1, amt);
                            ps.setString(2, currentAccount);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Amount Withdrawn");
                        } else {
                            JOptionPane.showMessageDialog(this, "Insufficient Balance");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        btnBalance.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT balance FROM users WHERE account_no=?");
                ps.setString(1, currentAccount);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Balance: " + rs.getDouble("balance"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnLogout.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(mainPanel, "Home");
        });

        gbc.gridy = 0; panel.add(btnDeposit, gbc);
        gbc.gridy = 1; panel.add(btnWithdraw, gbc);
        gbc.gridy = 2; panel.add(btnBalance, gbc);
        gbc.gridy = 3; panel.add(btnLogout, gbc);

        return panel;
    }

    public static void main(String[] args) {
        // Set cross-platform Java L&F (also called "Metal")
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }
        
        SwingUtilities.invokeLater(() -> {
            new BankingGUI().setVisible(true);
        });
    }
}
