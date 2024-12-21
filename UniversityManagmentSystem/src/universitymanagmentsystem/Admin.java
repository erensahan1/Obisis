package universitymanagmentsystem;

import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Admin extends JFrame {

    public Admin() throws SQLException {
        setTitle("Admin Dashboard");
        setLayout(null);
        setBounds(450, 100, 900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(60, 63, 65));

       
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setBounds(20, 10, 860, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel);

      
        JButton showPendingLecturesButton = createButton("SHOW PENDING LECTURES", 50, 70);
        showPendingLecturesButton.setSize(250, 50);
        showPendingLecturesButton.addActionListener(e -> {
            try {
                showPendingLectures();
                setVisible(false);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading pending lectures: " + ex.getMessage());
            }
        });
        add(showPendingLecturesButton);

   
        JButton backButton = createButton("BACK", 50, 300);
        backButton.setBackground(new Color(0, 122, 204));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            try {
                new Personnel(); 
                setVisible(false);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        add(backButton);

        setVisible(true);
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

   
   public void showPendingLectures() throws SQLException {
    JFrame frame = new JFrame("Pending Lectures");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setBounds(550, 100, 800, 500);
    frame.getContentPane().setBackground(new Color(60, 63, 65));

    JLabel titleLabel = new JLabel("Pending Lectures", SwingConstants.CENTER);
    titleLabel.setBounds(150, 30, 500, 30);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    frame.add(titleLabel);

    String[] columnNames = {"Lecture Id", "Lecture Name", "Time", "Personnel", ""};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);
    tableScrollPane.setBounds(50, 70, 700, 300);
    table.setBackground(Color.BLACK);
    table.setForeground(Color.WHITE);
    frame.add(tableScrollPane);

    loadPendingLectures(tableModel);

    // Approve Butonu
    JButton approveButton = createButton("APPROVE", 600, 400);
    approveButton.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int pendingLectureId = (int) tableModel.getValueAt(selectedRow, 0); 
            try {
                approveLecture(pendingLectureId);
                JOptionPane.showMessageDialog(frame, "Lecture Approved!");
                loadPendingLectures(tableModel); 
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error approving lecture: " + ex.getMessage());
            }
        }
    });
    frame.add(approveButton);

    
    JButton rejectButton = createButton("REJECT", 420, 400);
    rejectButton.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int pendingLectureId = (int) tableModel.getValueAt(selectedRow, 0); 
            try {
                rejectLecture(pendingLectureId);
                JOptionPane.showMessageDialog(frame, "Lecture Rejected!");
                loadPendingLectures(tableModel); 
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error rejecting lecture: " + ex.getMessage());
            }
        }
    });
    frame.add(rejectButton);


    JButton backButton = createButton("Back", 10, 400);
    backButton.addActionListener(e -> {
        frame.setVisible(false);
        try {
            new Admin();
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    });
    frame.add(backButton);

    frame.setVisible(true);
}

public void rejectLecture(int pendingLectureId) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();

  
    String deleteQuery = "DELETE FROM PendingLectures WHERE Id = ?";
    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
        deleteStmt.setInt(1, pendingLectureId);
        deleteStmt.executeUpdate();
    }
}

public void loadPendingLectures(DefaultTableModel tableModel) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    String query = "SELECT Id, Name, Date, Personnel_id FROM PendingLectures WHERE isApproved = 0";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("Id");
            String name = rs.getString("Name");
            String date = rs.getString("Date");
            int personnelId = rs.getInt("Personnel_id");

           
            String personnelNameQuery = "SELECT Name FROM Personnels WHERE Id = ?";
            try (PreparedStatement personnelStmt = conn.prepareStatement(personnelNameQuery)) {
                personnelStmt.setInt(1, personnelId);
                ResultSet personnelRs = personnelStmt.executeQuery();
                String personnelName = personnelRs.next() ? personnelRs.getString("Name") : "Unknown";

                tableModel.addRow(new Object[]{id, name, date, personnelName});
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading pending lectures: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    public void approveLecture(int pendingLectureId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

       
        String moveQuery = "INSERT INTO Lectures (Name, Date, Personnel_id) " +
                           "SELECT Name, Date, Personnel_id FROM PendingLectures WHERE Id = ?";
        try (PreparedStatement moveStmt = conn.prepareStatement(moveQuery)) {
            moveStmt.setInt(1, pendingLectureId);
            moveStmt.executeUpdate();
        }

        
        String updateQuery = "UPDATE PendingLectures SET isApproved = 1 WHERE Id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, pendingLectureId);
            updateStmt.executeUpdate();
        }
    }

    
}
