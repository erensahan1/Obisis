package universitymanagmentsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends JFrame {

    private int loggedInPersonnelId;
    private JPanel loginPanel;
    private JPanel createAccountPanel;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JComboBox<String> loginRoleField;

    private JTextField createNameField;
    private JTextField createUsernameField;
    private JPasswordField createPasswordField;
    private JComboBox<String> createRoleField;

    private Connection connection;

    public User() {
        setTitle("User Management");
        setBounds(450, 100, 600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(60, 63, 65));

        try {
            connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        initializeLoginPanel();
        initializeCreateAccountPanel();

        loginPanel.setVisible(true);
        createAccountPanel.setVisible(false);
    }

    private void initializeLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(0, 0, 600, 400);
        loginPanel.setBackground(new Color(60, 63, 65));
        add(loginPanel);

        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setBounds(250, 20, 100, 30);
        loginPanel.add(loginTitle);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(150, 80, 100, 25);
        loginPanel.add(usernameLabel);

        loginUsernameField = new JTextField();
        loginUsernameField.setBounds(250, 80, 150, 30);
        loginPanel.add(loginUsernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(150, 130, 100, 25);
        loginPanel.add(passwordLabel);

        loginPasswordField = new JPasswordField();
        loginPasswordField.setBounds(250, 130, 150, 30);
        loginPanel.add(loginPasswordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBounds(150, 180, 100, 25);
        loginPanel.add(roleLabel);

        loginRoleField = new JComboBox<>(new String[]{"STUDENT", "PERSONNEL", "ADMIN"});
        loginRoleField.setBounds(250, 180, 150, 30);
        loginPanel.add(loginRoleField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(250, 230, 100, 30);
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new LoginButtonListener());
        loginPanel.add(loginButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(250, 280, 150, 30);
        createAccountButton.setBackground(new Color(0, 122, 204));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.addActionListener(e -> switchToCreateAccountPanel());
        loginPanel.add(createAccountButton);
    }

    private void initializeCreateAccountPanel() {
        createAccountPanel = new JPanel();
        createAccountPanel.setLayout(null);
        createAccountPanel.setBounds(0, 0, 600, 400);
        createAccountPanel.setBackground(new Color(60, 63, 65));
        add(createAccountPanel);

        JLabel createTitle = new JLabel("Create Account");
        createTitle.setFont(new Font("Arial", Font.BOLD, 20));
        createTitle.setForeground(Color.WHITE);
        createTitle.setBounds(200, 20, 200, 30);
        createAccountPanel.add(createTitle);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(150, 50, 100, 25);
        createAccountPanel.add(nameLabel);

        createNameField = new JTextField();
        createNameField.setBounds(250, 50, 150, 30);
        createAccountPanel.add(createNameField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(150, 100, 100, 25);
        createAccountPanel.add(usernameLabel);

        createUsernameField = new JTextField();
        createUsernameField.setBounds(250, 100, 150, 30);
        createAccountPanel.add(createUsernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(150, 150, 100, 25);
        createAccountPanel.add(passwordLabel);

        createPasswordField = new JPasswordField();
        createPasswordField.setBounds(250, 150, 150, 30);
        createAccountPanel.add(createPasswordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBounds(150, 200, 100, 25);
        createAccountPanel.add(roleLabel);

        createRoleField = new JComboBox<>(new String[]{"STUDENT", "PERSONNEL", "ADMIN"});
        createRoleField.setBounds(250, 200, 150, 30);
        createAccountPanel.add(createRoleField);

        JButton createButton = new JButton("Create");
        createButton.setBounds(200, 250, 100, 30);
        createButton.setBackground(new Color(0, 122, 204));
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(new CreateButtonListener());
        createAccountPanel.add(createButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(320, 250, 100, 30);
        backButton.setBackground(new Color(204, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> switchToLoginPanel());
        createAccountPanel.add(backButton);
    }
private class LoginButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();
        String role = (String) loginRoleField.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String table = getTableNameForRole(role);
        if (table == null) {
            JOptionPane.showMessageDialog(null, "Invalid role selected!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT Id, Username FROM " + table + " WHERE Username = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int loggedInId = rs.getInt("Id"); 
                String loggedInUsername = rs.getString("Username");

             
                switch (role.toUpperCase()) {
                    case "STUDENT":
                        Session.setStudentId(loggedInId); 
                        JOptionPane.showMessageDialog(null, "Welcome, " + loggedInUsername + "! You are logged in as a Student.");
                      Student student=  new Student(); 
                        student.setVisible(true);
                        break;
                    case "PERSONNEL":
                        Session.setPersonnelId(loggedInId); 
                        JOptionPane.showMessageDialog(null, "Welcome, " + loggedInUsername + "! You are logged in as Personnel.");
                        new Personnel(); 
                        break;
                    case "ADMIN":
                        JOptionPane.showMessageDialog(null, "Welcome, " + loggedInUsername + "! You are logged in as Admin.");
                        new Admin();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Unknown role!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error logging in: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


   

    private class CreateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = createNameField.getText().trim();
            String username = createUsernameField.getText().trim();
            String password = new String(createPasswordField.getPassword()).trim();
            String role = (String) createRoleField.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String table = getTableNameForRole(role);
            if (table == null) {
                JOptionPane.showMessageDialog(null, "Invalid role selected!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO " + table + " (Name, Username, Password) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                switchToLoginPanel();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error creating account: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getTableNameForRole(String role) {
        switch (role.toUpperCase()) {
            case "STUDENT":
                return "Students";
            case "PERSONNEL":
                return "Personnels";
            case "ADMIN":
                return "Admins";
            default:
                return null;
        }
    }

    private void switchToLoginPanel() {
        loginPanel.setVisible(true);
        createAccountPanel.setVisible(false);
    }

    private void switchToCreateAccountPanel() {
        loginPanel.setVisible(false);
        createAccountPanel.setVisible(true);
    }
    public class Session {
    private static int personnelId = -1;  
    private  static  int studentId=-1;

        public static int getStudentId() {
            return studentId;
        }

        public static void setStudentId(int studentId) {
            Session.studentId = studentId;
        }


   
    public static void setPersonnelId(int id) {
        personnelId = id;
    }

   
    public static int getPersonnelId() {
        return personnelId;
    }

   
    public static boolean isLoggedIn() {
        return personnelId != -1;
    }
    }
    


    
}
