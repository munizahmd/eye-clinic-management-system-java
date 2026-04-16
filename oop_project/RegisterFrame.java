import eyeclinicsystem2.EyeClinicSystem2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField userField, emailField;
    private JPasswordField passField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/eye_clinic";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";

    public RegisterFrame() {
        setTitle("Register - Eye Clinic System");
        setSize(420, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color bgColor = new Color(240, 248, 255);
        Color btnColor = new Color(30, 144, 255);
        Color textColor = new Color(33, 33, 33);

        JLabel title = new JLabel("User Registration", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(btnColor);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(bgColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        emailField = new JTextField(15);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel emailLabel = new JLabel("Email:");

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(btnColor);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(userLabel, gbc);
        gbc.gridx = 1; formPanel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(emailLabel, gbc);
        gbc.gridx = 1; formPanel.add(emailField, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(registerBtn, gbc);

        getContentPane().setBackground(bgColor);
        add(title, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "Registration successful!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}