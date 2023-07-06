import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceTableGUI extends JFrame {
    private JTextField txtMaintenanceId, txtAssetId, txtMaintenanceDate, txtMaintenanceDescription;
    private JTable tblMaintenance;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public MaintenanceTableGUI() {
        initializeUI();
        connectToDatabase();
        displayMaintenance();
    }

    private void initializeUI() {
        txtMaintenanceId = new JTextField();
        txtAssetId = new JTextField();
        txtMaintenanceDate = new JTextField();
        txtMaintenanceDescription = new JTextField();

        tblMaintenance = new JTable();
        tblMaintenance.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMaintenance.getSelectionModel().addListSelectionListener(e -> selectMaintenance());

        JScrollPane scrollPane = new JScrollPane(tblMaintenance);

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

        panel.add(new JLabel("Maintenance ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Asset ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Maintenance Date:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Maintenance Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtMaintenanceId, gbc);
        gbc.gridy++;
        panel.add(txtAssetId, gbc);
        gbc.gridy++;
        panel.add(txtMaintenanceDate, gbc);
        gbc.gridy++;
        panel.add(txtMaintenanceDescription, gbc);

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

        btnAdd.addActionListener(e -> insertMaintenance());

        btnModify.addActionListener(e -> modifyMaintenance());

        btnDelete.addActionListener(e -> deleteMaintenance());

        btnDisplay.addActionListener(e -> displayMaintenance());

        setTitle("Maintenance App");
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

    private void insertMaintenance() {
        String maintenanceId = txtMaintenanceId.getText();
        String assetId = txtAssetId.getText();
        String maintenanceDate = txtMaintenanceDate.getText();
        String maintenanceDescription = txtMaintenanceDescription.getText();

        try {
            String query = "INSERT INTO Maintenance (maintenance_id, asset_id, maintenance_date, maintenance_description) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, maintenanceId);
            statement.setString(2, assetId);
            statement.setString(3, maintenanceDate);
            statement.setString(4, maintenanceDescription);
            statement.executeUpdate();

            clearFields();
            displayMaintenance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyMaintenance() {
        int selectedRow = tblMaintenance.getSelectedRow();
        if (selectedRow >= 0) {
            String maintenanceId = txtMaintenanceId.getText();
            String assetId = txtAssetId.getText();
            String maintenanceDate = txtMaintenanceDate.getText();
            String maintenanceDescription = txtMaintenanceDescription.getText();

            try {
                String query = "UPDATE Maintenance SET asset_id=?, maintenance_date=?, maintenance_description=? WHERE maintenance_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, assetId);
                statement.setString(2, maintenanceDate);
                statement.setString(3, maintenanceDescription);
                statement.setString(4, maintenanceId);
                statement.executeUpdate();

                clearFields();
                displayMaintenance();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a maintenance entry to modify.");
        }
    }

    private void deleteMaintenance() {
        int selectedRow = tblMaintenance.getSelectedRow();
        if (selectedRow >= 0) {
            String maintenanceId = tblMaintenance.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this maintenance entry?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Maintenance WHERE maintenance_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, maintenanceId);
                    statement.executeUpdate();

                    clearFields();
                    displayMaintenance();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a maintenance entry to delete.");
        }
    }

    private void displayMaintenance() {
        try {
            String query = "SELECT * FROM Maintenance";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<MaintenanceEntry> maintenanceEntries = new ArrayList<>();
            while (resultSet.next()) {
                String maintenanceId = resultSet.getString("maintenance_id");
                String assetId = resultSet.getString("asset_id");
                String maintenanceDate = resultSet.getString("maintenance_date");
                String maintenanceDescription = resultSet.getString("maintenance_description");
                maintenanceEntries.add(new MaintenanceEntry(maintenanceId, assetId, maintenanceDate, maintenanceDescription));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Maintenance ID", "Asset ID", "Maintenance Date", "Maintenance Description"});

            for (MaintenanceEntry maintenanceEntry : maintenanceEntries) {
                model.addRow(new String[]{maintenanceEntry.getMaintenanceId(), maintenanceEntry.getAssetId(), maintenanceEntry.getMaintenanceDate(), maintenanceEntry.getMaintenanceDescription()});
            }

            tblMaintenance.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectMaintenance() {
        int selectedRow = tblMaintenance.getSelectedRow();
        if (selectedRow >= 0) {
            String maintenanceId = tblMaintenance.getValueAt(selectedRow, 0).toString();
            String assetId = tblMaintenance.getValueAt(selectedRow, 1).toString();
            String maintenanceDate = tblMaintenance.getValueAt(selectedRow, 2).toString();
            String maintenanceDescription = tblMaintenance.getValueAt(selectedRow, 3).toString();

            txtMaintenanceId.setText(maintenanceId);
            txtAssetId.setText(assetId);
            txtMaintenanceDate.setText(maintenanceDate);
            txtMaintenanceDescription.setText(maintenanceDescription);
        }
    }

    private void clearFields() {
        txtMaintenanceId.setText("");
        txtAssetId.setText("");
        txtMaintenanceDate.setText("");
        txtMaintenanceDescription.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MaintenanceTableGUI::new);
    }

    private class MaintenanceEntry {
        private String maintenanceId;
        private String assetId;
        private String maintenanceDate;
        private String maintenanceDescription;

        public MaintenanceEntry(String maintenanceId, String assetId, String maintenanceDate, String maintenanceDescription) {
            this.maintenanceId = maintenanceId;
            this.assetId = assetId;
            this.maintenanceDate = maintenanceDate;
            this.maintenanceDescription = maintenanceDescription;
        }

        public String getMaintenanceId() {
            return maintenanceId;
        }

        public String getAssetId() {
            return assetId;
        }

        public String getMaintenanceDate() {
            return maintenanceDate;
        }

        public String getMaintenanceDescription() {
            return maintenanceDescription;
        }
    }
}
