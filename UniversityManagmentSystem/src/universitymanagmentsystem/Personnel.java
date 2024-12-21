package universitymanagmentsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Personnel extends JFrame {
   

    public Personnel() throws SQLException {
    setTitle("Personnel Homepage");
    setLayout(null);
    setBounds(450, 100, 900, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setBackground(new Color(60, 63, 65));

  
    JLabel titleLabel = new JLabel("Personnel Dashboard", SwingConstants.CENTER);
    titleLabel.setBounds(20, 10, 860, 40);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
    titleLabel.setForeground(Color.WHITE);
    add(titleLabel);

  
    JButton personnelButton = createButton("PERSONNEL HOMEPAGE", 50, 70);
    personnelButton.setSize(190, 50);
    personnelButton.addActionListener((e) -> { 
        try { 
            Personnel personnel = new Personnel();
        } catch (SQLException ex) {
            Logger.getLogger(Personnel.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false); 
    });
    add(personnelButton);

    JButton crudLectureButton = createButton("CRUD LECTURE", 250, 70);
    crudLectureButton.addActionListener(e -> {
        createLecture();
        setVisible(false);
    });
    add(crudLectureButton);

    JButton syllabusButton = createButton("SYLLABUS", 450, 70);
    syllabusButton.addActionListener(e -> {
        try {
            showSyllabus();
        } catch (SQLException ex) {
            Logger.getLogger(Personnel.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
    });
    add(syllabusButton);

    JButton crudGradesButton = createButton("CRUD GRADES", 650, 70);
    crudGradesButton.addActionListener(e -> {
        try {
            createGrades();
        } catch (SQLException ex) {
            Logger.getLogger(Personnel.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
    });
    add(crudGradesButton);

  

    JButton backButton = createButton("BACK", 50, 300);
    backButton.setBackground(new Color(0, 122, 204));
    backButton.setForeground(Color.WHITE);

    backButton.addActionListener(e -> {
        new User().setVisible(true);
        
        setVisible(false);
    });
    add(backButton);

   
    String personnelName = getPersonnelNameFromDatabase(User.Session.getPersonnelId());
    JLabel personelInfoLabel = new JLabel(personnelName, SwingConstants.CENTER);
    personelInfoLabel.setBounds(50, 150, 200, 50);
    personelInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    personelInfoLabel.setForeground(Color.WHITE);
    personelInfoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    add(personelInfoLabel);

    setVisible(true);
}

private String getPersonnelNameFromDatabase(int personnelId) throws SQLException {
    String personnelName = "";
    Connection conn = DatabaseConnection.getInstance().getConnection();
    String query = "SELECT Name FROM Personnels WHERE Id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, personnelId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            personnelName = rs.getString("Name"); 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching personnel name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    return personnelName;
}


    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 180, 50);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(43, 43, 43));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return button;
    }
    
public void createLecture() {
    JFrame frame = new JFrame("CRUD Lecture");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setBounds(550, 100, 700, 400);
    frame.getContentPane().setBackground(new Color(60, 63, 65));

    JLabel addLectureLabel = new JLabel("ADD LECTURE", SwingConstants.CENTER);
    addLectureLabel.setBounds(150, 30, 300, 30);
    addLectureLabel.setFont(new Font("Arial", Font.BOLD, 16));
    addLectureLabel.setForeground(Color.WHITE);
    addLectureLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    frame.add(addLectureLabel);

    JComboBox<String> lectureComboBox = new JComboBox<>(new String[]{"MATH", "PHYSICS", "CHEMISTRY"});
    lectureComboBox.setBounds(350, 110, 150, 30);
    lectureComboBox.setBackground(new Color(43, 43, 43));
    lectureComboBox.setForeground(Color.WHITE);
    frame.add(lectureComboBox);

    JComboBox<String> timeComboBox = new JComboBox<>(new String[]{"9:00-11:00", "11:00-13:00", "13:00-15:00"});
    timeComboBox.setBounds(150, 110, 150, 30);
    timeComboBox.setBackground(new Color(43, 43, 43));
    timeComboBox.setForeground(Color.WHITE);
    frame.add(timeComboBox);

    JButton saveButton = createButton("SAVE", 250, 300);
    saveButton.addActionListener(e -> {
        String lecture = lectureComboBox.getSelectedItem().toString();
        String time = timeComboBox.getSelectedItem().toString();
        
        try {
            saveLectureToDatabase(lecture, time);
            JOptionPane.showMessageDialog(frame, "Lecture Saved!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving lecture: " + ex.getMessage());
        }
    });
    frame.add(saveButton);

    JButton backButton = createButton("BACK", 50, 300);
    backButton.addActionListener(e -> {
        try {
            new Personnel();
        } catch (SQLException ex) {
            Logger.getLogger(Personnel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        frame.dispose();
    });
    frame.add(backButton);

    frame.setVisible(true);
}

public void saveLectureToDatabase(String lecture, String time) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    String query = "INSERT INTO PendingLectures (Name, Date, Personnel_id, isApproved) VALUES (?, ?, ?, ?)";
    PreparedStatement stmt = conn.prepareStatement(query);

    stmt.setString(1, lecture); 
    stmt.setString(2, time);    
    stmt.setInt(3, User.Session.getPersonnelId()); 
    stmt.setBoolean(4, false); 

    stmt.executeUpdate();
}



   

    public void createGrades() throws SQLException {
    JFrame frame = new JFrame("Grades");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setBounds(550, 100, 800, 500);
    frame.getContentPane().setBackground(new Color(60, 63, 65));

    JLabel gradesTitleLabel = new JLabel("Assign Grades", SwingConstants.CENTER);
    gradesTitleLabel.setBounds(150, 30, 500, 30);
    gradesTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    gradesTitleLabel.setForeground(Color.WHITE);
    gradesTitleLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    frame.add(gradesTitleLabel);

    String[] columnNames = {"Lecture Name", "Student Name", "Grade"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);
    tableScrollPane.setBounds(50, 70, 700, 300);
    table.setBackground(Color.BLACK);
    table.setForeground(Color.WHITE);
    frame.add(tableScrollPane);

    loadGradesTable(tableModel);

    JButton saveButton = createButton("SAVE", 600, 400);
    saveButton.addActionListener(e -> {
        try {
            saveGradesToDatabase(tableModel);
            JOptionPane.showMessageDialog(frame, "Grades Saved to Database!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving grades: " + ex.getMessage());
        }
    });
    frame.add(saveButton);

    JButton backButton = createButton("BACK", 50, 400);
    backButton.addActionListener(e -> {
        try {
            new Personnel();
        } catch (SQLException ex) {
            Logger.getLogger(Personnel.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.dispose();
    });
    frame.add(backButton);

    frame.setVisible(true);
}
private void loadGradesTable(DefaultTableModel tableModel) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    String query = "SELECT L.Name AS LectureName, S.Name AS StudentName, G.Grade " +
                   "FROM Enrollment E " +
                   "JOIN Lectures L ON E.Lecture_id = L.Id " +
                   "JOIN Students S ON E.Student_id = S.Id " +
                   "LEFT JOIN Grades G ON E.Student_id = G.Student_id AND E.Lecture_id = G.Lecture_id " +
                   "WHERE L.Personnel_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, User.Session.getPersonnelId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String lectureName = rs.getString("LectureName");
            String studentName = rs.getString("StudentName");
            String grade = rs.getString("Grade") != null ? rs.getString("Grade") : "";
            tableModel.addRow(new Object[]{lectureName, studentName, grade});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading grades table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
public void saveGradesToDatabase(DefaultTableModel tableModel) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();

  
    String query = "UPDATE Grades SET Grade = ? " +
                   "WHERE Lecture_id = (SELECT Id FROM Lectures WHERE Name = ?) " +
                   "AND Student_id = (SELECT Id FROM Students WHERE Name = ?)";

    PreparedStatement stmt = conn.prepareStatement(query);

    for (int i = 0; i < tableModel.getRowCount(); i++) {
        String lectureName = (String) tableModel.getValueAt(i, 0); 
        String studentName = (String) tableModel.getValueAt(i, 1); 
        String grade = (String) tableModel.getValueAt(i, 2);      

        stmt.setString(1, grade);      
        stmt.setString(2, lectureName); 
        stmt.setString(3, studentName); 

        int updatedRows = stmt.executeUpdate(); 

       
        if (updatedRows == 0) {
            String insertQuery = "INSERT INTO Grades (Lecture_id, Student_id, Grade) " +
                                 "VALUES ((SELECT Id FROM Lectures WHERE Name = ?), " +
                                 "(SELECT Id FROM Students WHERE Name = ?), ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, lectureName);
                insertStmt.setString(2, studentName);
                insertStmt.setString(3, grade);
                insertStmt.executeUpdate(); 
            }
        }
    }
}

   public void showSyllabus() throws SQLException {
    JFrame frame = new JFrame("Syllabus");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setBounds(550, 100, 800, 500);
    frame.getContentPane().setBackground(new Color(60, 63, 65));

    JLabel syllabusTitleLabel = new JLabel("Your Syllabus", SwingConstants.CENTER);
    syllabusTitleLabel.setBounds(150, 30, 500, 30);
    syllabusTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    syllabusTitleLabel.setForeground(Color.WHITE);
    syllabusTitleLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    frame.add(syllabusTitleLabel);

    String[] columnNames = {"Lecture Name", "Lecture Date"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);
    tableScrollPane.setBounds(50, 70, 700, 300);
    table.setBackground(Color.BLACK);
    table.setForeground(Color.WHITE);
    frame.add(tableScrollPane);

  
    loadSyllabusForPersonnel(tableModel);

    JButton backButton = createButton("BACK", 50, 400);
    backButton.addActionListener(e -> {
        setVisible(true);
        frame.dispose();
    });
    frame.add(backButton);

    frame.setVisible(true);
}

private void loadSyllabusForPersonnel(DefaultTableModel tableModel) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    int personnelId = User.Session.getPersonnelId(); 
    String query = "SELECT Name, Date FROM Lectures WHERE Personnel_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, personnelId); 
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String lectureName = rs.getString("Name");
            String lectureDate = rs.getString("Date");

           
            tableModel.addRow(new Object[]{lectureName, lectureDate});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading syllabus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    
}
