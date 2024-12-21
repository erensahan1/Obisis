package universitymanagmentsystem;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Student extends JFrame {

    private Connection connection;
    private int selectedLectureId = -1; 
    private int lectureid;

    public int getLectureid() {
        return lectureid;
    }

    public void setLectureid(int lectureid) {
        this.lectureid = lectureid;
    }

    public Student() {
        try {
            connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Student Dashboard");
        setLayout(null);
        setBounds(450, 100, 900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(60, 63, 65));

        JLabel titleLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        titleLabel.setBounds(20, 10, 760, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel);

        JButton studentHomepageButton = createButton("STUDENT HOMEPAGE", 50, 70);
        studentHomepageButton.setSize(190,50);
        studentHomepageButton.addActionListener(e -> new Student());
        add(studentHomepageButton);

        JButton choosingLectureButton = createButton("CHOOSING LECTURE", 250, 70);
        choosingLectureButton.setSize(185, 50);
        choosingLectureButton.addActionListener(e -> {
            choosingLecture();
            setVisible(false);
        });
        add(choosingLectureButton);

        JButton gradesButton = createButton("GRADES", 450, 70);
        gradesButton.addActionListener(e -> {
            try {
                showGrades();
            } catch (SQLException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVisible(false);
        });
        add(gradesButton);

        JButton syllabusButton = createButton("SYLLABUS", 650, 70);
        syllabusButton.addActionListener(e -> {
            showSyllabus();
            setVisible(false);
        });
        add(syllabusButton);
        
      String query = "SELECT Name FROM Students WHERE Id = ?";
try (PreparedStatement stmt = connection.prepareStatement(query)) {
    stmt.setInt(1, User.Session.getStudentId()); 
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        String name = rs.getString("Name");
        
        
        
        JLabel studentInfoLabel = createLabel(name, 50, 150, 200, 50);
        add(studentInfoLabel);
    }
} catch (SQLException e) {
    e.printStackTrace();
}

        


        JButton backButton = createButton("BACK", 50, 300);
        backButton.setBackground(new Color(0, 122, 204));
        backButton.addActionListener(e -> {
            User user = new User();
            user.setVisible(true);
            setVisible(false);
        });
        add(backButton);

        
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return label;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 180, 50);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(43, 43, 43));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    public void choosingLecture() {
        JFrame frame = new JFrame("Choosing Lecture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(550, 100, 600, 400);
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(60, 63, 65));
        frame.setVisible(true);

        JLabel labelTitle = new JLabel("Available Lectures", SwingConstants.CENTER);
        labelTitle.setBounds(30, 20, 540, 30);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 18));
        labelTitle.setForeground(Color.WHITE);
        frame.add(labelTitle);

        String[] columnNames = {"Lecture Name", "Date", "Lecture Id"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        JTable lectureTable = new JTable(tableModel);
        lectureTable.setBackground(Color.BLACK);
        lectureTable.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(lectureTable);
        scrollPane.setBounds(30, 70, 540, 200);
        frame.add(scrollPane);

    
        loadLectures(tableModel);

        JButton selectButton = new JButton("Select");
        selectButton.setBounds(150, 300, 100, 30);
        selectButton.setBackground(new Color(0, 122, 204));
        selectButton.setForeground(Color.WHITE);
        selectButton.addActionListener(e -> {
            int selectedRow = lectureTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedLectureId = (int) tableModel.getValueAt(selectedRow, 2);
                JOptionPane.showMessageDialog(frame, "Lecture selected! ID: " + selectedLectureId, "Selection Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a lecture!", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(selectButton);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(250, 300, 100, 30);
        saveButton.setBackground(new Color(0, 204, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveEnrollment(frame));
        frame.add(saveButton);
        
        

        JButton backButton2 = new JButton("BACK");
        backButton2.setBounds(350, 300, 100, 30);
        backButton2.setBackground(new Color(0, 122, 204));
        backButton2.setForeground(Color.WHITE);
        backButton2.addActionListener(e -> {
            setVisible(true);
            frame.setVisible(false);
        });
        frame.add(backButton2);
    }

    private void loadLectures(DefaultTableModel tableModel) {
        String query = "SELECT Id, Name, Date FROM Lectures";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                String date = rs.getString("Date");
                int id = rs.getInt("Id");
                tableModel.addRow(new Object[]{name, date, id});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveEnrollment(JFrame frame) {
        if (selectedLectureId != -1) {
            int studentId = User.Session.getStudentId(); 
            String insertEnrollment = "INSERT INTO Enrollment (Lecture_id, Student_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertEnrollment)) {
                insertStmt.setInt(1, selectedLectureId);
                insertStmt.setInt(2, studentId);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Enrollment saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving enrollment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a lecture first!", "Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showSyllabus() {
    JFrame frame = new JFrame("Syllabus");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(550, 100, 600, 400);
    frame.setLayout(null);
    frame.getContentPane().setBackground(new Color(60, 63, 65));
    frame.setVisible(true);

    JLabel labelTitle = new JLabel("Your Selected Lectures", SwingConstants.CENTER);
    labelTitle.setBounds(30, 20, 540, 30);
    labelTitle.setFont(new Font("Arial", Font.BOLD, 18));
    labelTitle.setForeground(Color.WHITE);
    frame.add(labelTitle);

    String[] columnNames = {"Lecture Name", "Date"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

    JTable syllabusTable = new JTable(tableModel);
    syllabusTable.setBackground(Color.BLACK);
    syllabusTable.setForeground(Color.WHITE);

    JScrollPane scrollPane = new JScrollPane(syllabusTable);
    scrollPane.setBounds(30, 70, 540, 200);
    frame.add(scrollPane);

    
    loadSyllabus(tableModel);

    JButton backButton = new JButton("BACK");
    backButton.setBounds(250, 300, 100, 30);
    backButton.setBackground(new Color(0, 122, 204));
    backButton.setForeground(Color.WHITE);
    backButton.addActionListener(e -> {
        setVisible(true);
        frame.setVisible(false);
    });
    frame.add(backButton);
}

private void loadSyllabus(DefaultTableModel tableModel) {
    String query = "SELECT L.Name, L.Date " +
                   "FROM Enrollment E " +
                   "JOIN Lectures L ON E.Lecture_id = L.Id " +
                   "WHERE E.Student_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        int studentId = User.Session.getStudentId(); 
        stmt.setInt(1, studentId);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String name = rs.getString("Name");
            String date = rs.getString("Date");
            tableModel.addRow(new Object[]{name, date});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading syllabus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


   public void showGrades() throws SQLException {
    JFrame frame = new JFrame("Student Grades");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setBounds(550, 100, 800, 500);
    frame.getContentPane().setBackground(new Color(60, 63, 65));

    JLabel gradesTitleLabel = new JLabel("Your Grades", SwingConstants.CENTER);
    gradesTitleLabel.setBounds(150, 30, 500, 30);
    gradesTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    gradesTitleLabel.setForeground(Color.WHITE);
    gradesTitleLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    frame.add(gradesTitleLabel);

    String[] columnNames = {"Lecture Name", "Personnel Name", "Grade"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);
    tableScrollPane.setBounds(50, 70, 700, 300);
    table.setBackground(Color.BLACK);
    table.setForeground(Color.WHITE);
    frame.add(tableScrollPane);

    
    loadGradesForStudent(tableModel);

 JButton backButton = new JButton("BACK");
    backButton.setBounds(250, 400, 100, 30);
    backButton.setBackground(new Color(0, 122, 204));
    backButton.setForeground(Color.WHITE);
    backButton.addActionListener(e -> {
        setVisible(true);
        frame.setVisible(false);
    });
    frame.add(backButton);

    frame.setVisible(true);
}

private void loadGradesForStudent(DefaultTableModel tableModel) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    int studentId = User.Session.getStudentId(); 
    String query = "SELECT L.Name AS LectureName, G.Grade, P.Name AS PersonnelName " +
                   "FROM Grades G " +
                   "JOIN Lectures L ON G.Lecture_id = L.Id " +
                   "JOIN Personnels P ON L.Personnel_id = P.Id " +
                   "WHERE G.Student_id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String lectureName = rs.getString("LectureName");
            String grade = rs.getString("Grade");
            String personnelName = rs.getString("PersonnelName");

          
            tableModel.addRow(new Object[]{lectureName, personnelName, grade});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

}
