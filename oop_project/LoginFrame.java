import eyeclinicsystem2.EyeClinicSystem2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/eye_clinic";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";

    public LoginFrame() {
        setTitle("Login - Eye Clinic System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Eye Clinic System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(30, 144, 255));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.setBackground(new Color(240, 248, 255));  // light blue background

        form.add(new JLabel("Username:", SwingConstants.RIGHT));
        userField = new JTextField();
        form.add(userField);

        form.add(new JLabel("Password:", SwingConstants.RIGHT));
        passField = new JPasswordField();
        form.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 144, 255));
        loginBtn.setForeground(Color.WHITE);
        form.add(new JLabel());  // Empty placeholder
        form.add(loginBtn);

        add(form, BorderLayout.CENTER);

        // Register link panel
        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(new Color(240, 248, 255));
        JLabel registerLabel = new JLabel("Don't have an account? ");
        JLabel clickToRegister = new JLabel("Register here");
        clickToRegister.setForeground(Color.BLUE.darker());
        clickToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clickToRegister.setFont(new Font("SansSerif", Font.BOLD, 12));

        clickToRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();  // Close login
                new RegisterFrame().setVisible(true);  // Open register
            }
        });

        registerPanel.add(registerLabel);
        registerPanel.add(clickToRegister);
        add(registerPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if (authenticateUser(user, pass)) {
                dispose(); // Close login window
                new EyeClinicSystem2(); // Open main app
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // returns true if user found
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}