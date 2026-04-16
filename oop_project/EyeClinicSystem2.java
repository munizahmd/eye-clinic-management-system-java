package eyeclinicsystem2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

abstract class Person {
    private int id;
    private String name;
    private float age;
    private String contact;

    public Person(int id, String name, float age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public float getAge() { return age; }
    public String getContact() { return contact; }

    public abstract String getDetails();
}

class Doctor extends Person {
    private String specialization;
    private boolean onLeave;

    public Doctor(int id, String name, float age, String contact, String specialization, boolean onLeave) {
        super(id, name, age, contact);
        this.specialization = specialization;
        this.onLeave = onLeave;
    }

    public String getSpecialization() { return specialization; }
    public boolean isOnLeave() { return onLeave; }

    @Override
    public String getDetails() {
        return "Dr. " + getName() + " - " + specialization + " (Age: " + getAge() + ", Contact: " + getContact() + ")" +
                (onLeave ? " [On Leave]" : "");
    }
}

class Patient extends Person {
    private boolean hasInfant;

    public Patient(int id, String name, float age, String contact, boolean hasInfant) {
        super(id, name, age, contact);
        this.hasInfant = hasInfant;
    }

    public boolean hasInfant() { return hasInfant; }

    @Override
    public String getDetails() {
        return getName() + " (Age: " + getAge() + ", Contact: " + getContact() + ", Has Infant: " + (hasInfant ? "Yes" : "No") + ")";
    }
}

public class EyeClinicSystem2 extends JFrame {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/eye_clinic";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234"
            + "";

    private Connection conn;

    private final File logFile = new File("EyeClinicLog.txt");

    private JButton regDocBtn, regPatBtn, bookAppBtn, cancelAppBtn, viewDocBtn, viewPatBtn, viewAppBtn;
    private JPanel buttonPanel;

    public EyeClinicSystem2() {
        setTitle("Eye Clinic Management System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }

        setupGUI();
        setVisible(true);
    }

    private void setupGUI() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(new Color(240, 248, 255)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        Font btnFont = new Font("Arial", Font.BOLD, 16);

        regDocBtn = new JButton("Register Doctor");
        regDocBtn.setFont(btnFont);
        regDocBtn.setBackground(new Color(0, 123, 255));
        regDocBtn.setForeground(Color.white);

        regPatBtn = new JButton("Register Patient");
        regPatBtn.setFont(btnFont);
        regPatBtn.setBackground(new Color(0, 123, 255));
        regPatBtn.setForeground(Color.white);

        bookAppBtn = new JButton("Book Appointment");
        bookAppBtn.setFont(btnFont);
        bookAppBtn.setBackground(new Color(0, 123, 255));
        bookAppBtn.setForeground(Color.white);

        cancelAppBtn = new JButton("Cancel Appointment");
        cancelAppBtn.setFont(btnFont);
        cancelAppBtn.setBackground(new Color(0, 123, 255));
        cancelAppBtn.setForeground(Color.white);

        viewDocBtn = new JButton("View Doctors");
        viewDocBtn.setFont(btnFont);
        viewDocBtn.setBackground(new Color(0, 123, 255));
        viewDocBtn.setForeground(Color.white);

        viewPatBtn = new JButton("View Patients");
        viewPatBtn.setFont(btnFont);
        viewPatBtn.setBackground(new Color(0, 123, 255));
        viewPatBtn.setForeground(Color.white);

        viewAppBtn = new JButton("View Appointments");
        viewAppBtn.setFont(btnFont);
        viewAppBtn.setBackground(new Color(0, 123, 255));
        viewAppBtn.setForeground(Color.white);

        gbc.gridy = 0; buttonPanel.add(regDocBtn, gbc);
        gbc.gridy = 1; buttonPanel.add(regPatBtn, gbc);
        gbc.gridy = 2; buttonPanel.add(bookAppBtn, gbc);
        gbc.gridy = 3; buttonPanel.add(cancelAppBtn, gbc);
        gbc.gridy = 4; buttonPanel.add(viewDocBtn, gbc);
        gbc.gridy = 5; buttonPanel.add(viewPatBtn, gbc);
        gbc.gridy = 6; buttonPanel.add(viewAppBtn, gbc);

        add(buttonPanel);

        regDocBtn.addActionListener(e -> registerDoctor());
        regPatBtn.addActionListener(e -> registerPatient());
        bookAppBtn.addActionListener(e -> bookAppointment());
        cancelAppBtn.addActionListener(e -> cancelAppointment());
        viewDocBtn.addActionListener(e -> showDoctors());
        viewPatBtn.addActionListener(e -> showPatients());
        viewAppBtn.addActionListener(e -> showAppointments());
    }

    private void registerDoctor() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField specField = new JTextField();
        JCheckBox leaveCheck = new JCheckBox("On Leave");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Contact:"));
        panel.add(contactField);
        panel.add(new JLabel("Specialization:"));
        panel.add(specField);
        panel.add(new JLabel("Status:"));
        panel.add(leaveCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register Doctor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                float age = Float.parseFloat(ageField.getText().trim());
                String contact = contactField.getText().trim();
                String specialization = specField.getText().trim();
                boolean onLeave = leaveCheck.isSelected();

                if (name.isEmpty() || contact.isEmpty() || specialization.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                String sql = "INSERT INTO Doctors (Name, Age, ContactNumber, Specialization, IsOnLeave) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setFloat(2, age);
                ps.setString(3, contact);
                ps.setString(4, specialization);
                ps.setBoolean(5, onLeave);
                ps.executeUpdate();
                ps.close();

                logToFile("Registered Doctor: " + name + ", Specialization: " + specialization);

                JOptionPane.showMessageDialog(this, "Doctor registered successfully.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid age entered.");
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Logging error: " + ioe.getMessage());
            }
        }
    }

    private void registerPatient() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField contactField = new JTextField();
        JCheckBox infantCheck = new JCheckBox("Has Infant");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Contact:"));
        panel.add(contactField);
        panel.add(new JLabel("Has Infant:"));
        panel.add(infantCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                float age = Float.parseFloat(ageField.getText().trim());
                String contact = contactField.getText().trim();
                boolean hasInfant = infantCheck.isSelected();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                String sql = "INSERT INTO Patients (Name, Age, ContactNumber, HasInfant) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setFloat(2, age);
                ps.setString(3, contact);
                ps.setBoolean(4, hasInfant);
                ps.executeUpdate();
                ps.close();

                logToFile("Registered Patient: " + name);

                JOptionPane.showMessageDialog(this, "Patient registered successfully.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid age entered.");
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Logging error: " + ioe.getMessage());
            }
        }
    }

    private void bookAppointment() {
        try {
            String pname = JOptionPane.showInputDialog(this, "Enter Patient Name:");
            if (pname == null || pname.trim().isEmpty()) return;

            Patient p = findPatientByName(pname.trim());
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Patient not found.");
                return;
            }

            String dname = JOptionPane.showInputDialog(this, "Enter Doctor Name:");
            if (dname == null || dname.trim().isEmpty()) return;

            Doctor d = findDoctorByName(dname.trim());
            if (d == null) {
                JOptionPane.showMessageDialog(this, "Doctor not found.");
                return;
            }

            if (d.isOnLeave()) {
                JOptionPane.showMessageDialog(this, "Doctor is currently on leave.");
                return;
            }

            JPanel datePanel = new JPanel(new GridLayout(0, 2, 10, 10));
            JTextField dateField = new JTextField("YYYY-MM-DD");
            JTextField timeField = new JTextField("HH:MM (24h)");

            datePanel.add(new JLabel("Date (YYYY-MM-DD):"));
            datePanel.add(dateField);
            datePanel.add(new JLabel("Time (HH:MM 24h):"));
            datePanel.add(timeField);

            int result = JOptionPane.showConfirmDialog(this, datePanel, "Appointment Date & Time", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String dateStr = dateField.getText().trim();
            String timeStr = timeField.getText().trim();

            String dateTimeStr = dateStr + " " + timeStr + ":00";
            Timestamp appointmentTimestamp;
            try {
                appointmentTimestamp = Timestamp.valueOf(dateTimeStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format.");
                return;
            }

            String status = "Booked";

            String sql = "INSERT INTO Appointments (DoctorID, PatientID, AppointmentDate, Status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, d.getId());
            ps.setInt(2, p.getId());
            ps.setTimestamp(3, appointmentTimestamp);
            ps.setString(4, status);
            ps.executeUpdate();
            ps.close();

            logToFile("Booked appointment: Patient '" + p.getName() + "' with Doctor '" + d.getName() + "' on " + appointmentTimestamp);

            JOptionPane.showMessageDialog(this, "Appointment booked successfully.");
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Logging error: " + ioe.getMessage());
        }
    }

    private void cancelAppointment() {
        try {
            String pname = JOptionPane.showInputDialog(this, "Enter Patient Name to cancel appointment:");
            if (pname == null || pname.trim().isEmpty()) return;

            Patient p = findPatientByName(pname.trim());
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Patient not found.");
                return;
            }

            String sql = "DELETE FROM Appointments WHERE PatientID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            int deleted = ps.executeUpdate();
            ps.close();

            if (deleted > 0) {
                logToFile("Canceled appointment(s) for patient: " + p.getName());
                JOptionPane.showMessageDialog(this, "Appointment(s) canceled.");
            } else {
                JOptionPane.showMessageDialog(this, "No appointments found for this patient.");
            }
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Logging error: " + ioe.getMessage());
        }
    }

    private void showDoctors() {
        try {
            String sql = "SELECT * FROM Doctors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getInt("DoctorID"),
                        rs.getString("Name"),
                        rs.getFloat("Age"),
                        rs.getString("ContactNumber"),
                        rs.getString("Specialization"),
                        rs.getBoolean("IsOnLeave")
                );
                sb.append(d.getDetails()).append("\n");
            }
            rs.close();
            stmt.close();

            JOptionPane.showMessageDialog(this, sb.length() == 0 ? "No doctors found." : sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching doctors: " + e.getMessage());
        }
    }

    private void showPatients() {
        try {
            String sql = "SELECT * FROM Patients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("PatientID"),
                        rs.getString("Name"),
                        rs.getFloat("Age"),
                        rs.getString("ContactNumber"),
                        rs.getBoolean("HasInfant")
                );
                sb.append(p.getDetails()).append("\n");
            }
            rs.close();
            stmt.close();

            JOptionPane.showMessageDialog(this, sb.length() == 0 ? "No patients found." : sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching patients: " + e.getMessage());
        }
    }

    private void showAppointments() {
        try {
            String sql = "SELECT * FROM Appointments";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                int appId = rs.getInt("AppointmentID");
                int patId = rs.getInt("PatientID");
                int docId = rs.getInt("DoctorID");
                Timestamp dt = rs.getTimestamp("AppointmentDate");
                String status = rs.getString("Status");

                Patient p = findPatientById(patId);
                Doctor d = findDoctorById(docId);

                sb.append("Appointment ID: ").append(appId).append("\n")
                        .append("Patient: ").append(p != null ? p.getName() : "Unknown").append("\n")
                        .append("Doctor: ").append(d != null ? d.getName() : "Unknown").append("\n")
                        .append("DateTime: ").append(dt).append("\n")
                        .append("Status: ").append(status).append("\n\n");
            }
            rs.close();
            stmt.close();

            JOptionPane.showMessageDialog(this, sb.length() == 0 ? "No appointments found." : sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }

    private Doctor findDoctorByName(String name) throws SQLException {
        String sql = "SELECT * FROM Doctors WHERE LOWER(Name) = LOWER(?) LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        Doctor d = null;
        if (rs.next()) {
            d = new Doctor(
                    rs.getInt("DoctorID"),
                    rs.getString("Name"),
                    rs.getFloat("Age"),
                    rs.getString("ContactNumber"),
                    rs.getString("Specialization"),
                    rs.getBoolean("IsOnLeave")
            );
        }
        rs.close();
        stmt.close();
        return d;
    }

    private Patient findPatientByName(String name) throws SQLException {
        String sql = "SELECT * FROM Patients WHERE LOWER(Name) = LOWER(?) LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        Patient p = null;
        if (rs.next()) {
            p = new Patient(
                    rs.getInt("PatientID"),
                    rs.getString("Name"),
                    rs.getFloat("Age"),
                    rs.getString("ContactNumber"),
                    rs.getBoolean("HasInfant")
            );
        }
        rs.close();
        stmt.close();
        return p;
    }

    private Patient findPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM Patients WHERE PatientID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Patient p = null;
        if (rs.next()) {
            p = new Patient(
                    rs.getInt("PatientID"),
                    rs.getString("Name"),
                    rs.getFloat("Age"),
                    rs.getString("ContactNumber"),
                    rs.getBoolean("HasInfant")
            );
        }
        rs.close();
        stmt.close();
        return p;
    }

    private Doctor findDoctorById(int id) throws SQLException {
        String sql = "SELECT * FROM Doctors WHERE DoctorID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Doctor d = null;
        if (rs.next()) {
            d = new Doctor(
                    rs.getInt("DoctorID"),
                    rs.getString("Name"),
                    rs.getFloat("Age"),
                    rs.getString("ContactNumber"),
                    rs.getString("Specialization"),
                    rs.getBoolean("IsOnLeave")
            );
        }
        rs.close();
        stmt.close();
        return d;
    }

    private void logToFile(String message) throws IOException {
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            out.println(timeStamp + " - " + message);
        }
    }

  
}