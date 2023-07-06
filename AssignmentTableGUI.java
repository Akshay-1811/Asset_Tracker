import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentTableGUI extends JFrame {
    private JTextField txtAssignmentId, txtAssetId, txtUserId, txtAssignedDate, txtReturnDate, txtLocationId;
    private JTable tblAssignments;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public AssignmentTableGUI() {
        initializeUI();
        connectToDatabase();
        displayAssignments();
    }

    private void initializeUI() {
        txtAssignmentId = new JTextField();
        txtAssetId = new JTextField();
        txtUserId = new JTextField();
        txtAssignedDate = new JTextField();
        txtReturnDate = new JTextField();
        txtLocationId = new JTextField();

        tblAssignments = new JTable();
        tblAssignments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAssignments.getSelectionModel().addListSelectionListener(e -> selectAssignment());

        JScrollPane scrollPane = new JScrollPane(tblAssignments);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Assignment ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Asset ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Assigned Date:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Return Date:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Location ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtAssignmentId, gbc);
        gbc.gridy++;
        panel.add(txtAssetId, gbc);
        gbc.gridy++;
        panel.add(txtUserId, gbc);
        gbc.gridy++;
        panel.add(txtAssignedDate, gbc);
        gbc.gridy++;
        panel.add(txtReturnDate, gbc);
        gbc.gridy++;
        panel.add(txtLocationId, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertAssignment());

        btnModify.addActionListener(e -> modifyAssignment());

        btnDelete.addActionListener(e -> deleteAssignment());

        btnDisplay.addActionListener(e -> displayAssignments());

        setTitle("Assignments App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "akshay";
        String password = "akshay2003";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertAssignment() {
        String assignmentId = txtAssignmentId.getText();
        String assetId = txtAssetId.getText();
        String userId = txtUserId.getText();
        String assignedDate = txtAssignedDate.getText();
        String returnDate = txtReturnDate.getText();
        String locationId = txtLocationId.getText();

        try {
            String query = "INSERT INTO assignment (assignment_id, asset_id, user_id, assigned_date, return_date, location_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, assignmentId);
            statement.setString(2, assetId);
            statement.setString(3, userId);
            statement.setString(4, assignedDate);
            statement.setString(5, returnDate);
            statement.setString(6, locationId);
            statement.executeUpdate();

            clearFields();
            displayAssignments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyAssignment() {
        int selectedRow = tblAssignments.getSelectedRow();
        if (selectedRow >= 0) {
            String assignmentId = txtAssignmentId.getText();
            String assetId = txtAssetId.getText();
            String userId = txtUserId.getText();
            String assignedDate = txtAssignedDate.getText();
            String returnDate = txtReturnDate.getText();
            String locationId = txtLocationId.getText();

            try {
                String query = "UPDATE assignment SET asset_id=?, user_id=?, assigned_date=?, return_date=?, location_id=? WHERE assignment_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, assetId);
                statement.setString(2, userId);
                statement.setString(3, assignedDate);
                statement.setString(4, returnDate);
                statement.setString(5, locationId);
                statement.setString(6, assignmentId);
                statement.executeUpdate();

                clearFields();
                displayAssignments();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an assignment to modify.");
        }
    }

    private void deleteAssignment() {
        int selectedRow = tblAssignments.getSelectedRow();
        if (selectedRow >= 0) {
            String assignmentId = tblAssignments.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this assignment?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM assignment WHERE assignment_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, assignmentId);
                    statement.executeUpdate();

                    clearFields();
                    displayAssignments();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an assignment to delete.");
        }
    }

    private void displayAssignments() {
        try {
            String query = "SELECT * FROM assignment";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Assignment> assignments = new ArrayList<>();
            while (resultSet.next()) {
                String assignmentId = resultSet.getString("assignment_id");
                String assetId = resultSet.getString("asset_id");
                String userId = resultSet.getString("user_id");
                String assignedDate = resultSet.getString("assigned_date");
                String returnDate = resultSet.getString("return_date");
                String locationId = resultSet.getString("location_id");
                assignments.add(new Assignment(assignmentId, assetId, userId, assignedDate, returnDate, locationId));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Assignment ID", "Asset ID", "User ID", "Assigned Date", "Return Date", "Location ID"});

            for (Assignment assignment : assignments) {
                model.addRow(new String[]{assignment.getAssignmentId(), assignment.getAssetId(), assignment.getUserId(), assignment.getAssignedDate(), assignment.getReturnDate(), assignment.getLocationId()});
            }

            tblAssignments.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectAssignment() {
        int selectedRow = tblAssignments.getSelectedRow();
        if (selectedRow >= 0) {
            String assignmentId = tblAssignments.getValueAt(selectedRow, 0).toString();
            String assetId = tblAssignments.getValueAt(selectedRow, 1).toString();
            String userId = tblAssignments.getValueAt(selectedRow, 2).toString();
            String assignedDate = tblAssignments.getValueAt(selectedRow, 3).toString();
            String returnDate = tblAssignments.getValueAt(selectedRow, 4).toString();
            String locationId = tblAssignments.getValueAt(selectedRow, 5).toString();

            txtAssignmentId.setText(assignmentId);
            txtAssetId.setText(assetId);
            txtUserId.setText(userId);
            txtAssignedDate.setText(assignedDate);
            txtReturnDate.setText(returnDate);
            txtLocationId.setText(locationId);
        }
    }

    private void clearFields() {
        txtAssignmentId.setText("");
        txtAssetId.setText("");
        txtUserId.setText("");
        txtAssignedDate.setText("");
        txtReturnDate.setText("");
        txtLocationId.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AssignmentTableGUI::new);
    }

    private class Assignment {
        private String assignmentId;
        private String assetId;
        private String userId;
        private String assignedDate;
        private String returnDate;
        private String locationId;

        public Assignment(String assignmentId, String assetId, String userId, String assignedDate, String returnDate, String locationId) {
            this.assignmentId = assignmentId;
            this.assetId = assetId;
            this.userId = userId;
            this.assignedDate = assignedDate;
            this.returnDate = returnDate;
            this.locationId = locationId;
        }

        public String getAssignmentId() {
            return assignmentId;
        }

        public String getAssetId() {
            return assetId;
        }

        public String getUserId() {
            return userId;
        }

        public String getAssignedDate() {
            return assignedDate;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public String getLocationId() {
            return locationId;
        }
    }
}

