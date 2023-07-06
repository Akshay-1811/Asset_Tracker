import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationTableGUI extends JFrame {
    private JTextField txtLocationId, txtLocationName, txtAddress, txtCity, txtState, txtCountry;
    private JTable tblLocations;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public LocationTableGUI() {
        initializeUI();
        connectToDatabase();
        displayLocations();
    }

    private void initializeUI() {
        txtLocationId = new JTextField();
        txtLocationName = new JTextField();
        txtAddress = new JTextField();
        txtCity = new JTextField();
        txtState = new JTextField();
        txtCountry = new JTextField();

        tblLocations = new JTable();
        tblLocations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLocations.getSelectionModel().addListSelectionListener(e -> selectLocation());

        JScrollPane scrollPane = new JScrollPane(tblLocations);

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

        panel.add(new JLabel("Location ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Location Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("State:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Country:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtLocationId, gbc);
        gbc.gridy++;
        panel.add(txtLocationName, gbc);
        gbc.gridy++;
        panel.add(txtAddress, gbc);
        gbc.gridy++;
        panel.add(txtCity, gbc);
        gbc.gridy++;
        panel.add(txtState, gbc);
        gbc.gridy++;
        panel.add(txtCountry, gbc);

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

        btnAdd.addActionListener(e -> insertLocation());

        btnModify.addActionListener(e -> modifyLocation());

        btnDelete.addActionListener(e -> deleteLocation());

        btnDisplay.addActionListener(e -> displayLocations());

        setTitle("Location App");
        
        
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

    private void insertLocation() {
        String locationId = txtLocationId.getText();
        String locationName = txtLocationName.getText();
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String state = txtState.getText();
        String country = txtCountry.getText();

        try {
            String query = "INSERT INTO location (location_id, location_name, address, city, state, country) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, locationId);
            statement.setString(2, locationName);
            statement.setString(3, address);
            statement.setString(4, city);
            statement.setString(5, state);
            statement.setString(6, country);
            statement.executeUpdate();

            clearFields();
            displayLocations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = txtLocationId.getText();
            String locationName = txtLocationName.getText();
            String address = txtAddress.getText();
            String city = txtCity.getText();
            String state = txtState.getText();
            String country = txtCountry.getText();

            try {
                String query = "UPDATE location SET location_name=?, address=?, city=?, state=?, country=? WHERE location_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, locationName);
                statement.setString(2, address);
                statement.setString(3, city);
                statement.setString(4, state);
                statement.setString(5, country);
                statement.setString(6, locationId);
                statement.executeUpdate();

                clearFields();
                displayLocations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a location to modify.");
        }
    }

    private void deleteLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = tblLocations.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this location?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM location WHERE location_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, locationId);
                    statement.executeUpdate();

                    clearFields();
                    displayLocations();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a location to delete.");
        }
    }

    private void displayLocations() {
        try {
            String query = "SELECT * FROM location";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Location> locations = new ArrayList<>();
            while (resultSet.next()) {
                String locationId = resultSet.getString("location_id");
                String locationName = resultSet.getString("location_name");
                String address = resultSet.getString("address");
                String city = resultSet.getString("city");
                String state = resultSet.getString("state");
                String country = resultSet.getString("country");
                locations.add(new Location(locationId, locationName, address, city, state, country));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Location ID", "Location Name", "Address", "City", "State", "Country"});

            for (Location location : locations) {
                model.addRow(new String[]{location.getLocationId(), location.getLocationName(), location.getAddress(), location.getCity(), location.getState(), location.getCountry()});
            }

            tblLocations.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = tblLocations.getValueAt(selectedRow, 0).toString();
            String locationName = tblLocations.getValueAt(selectedRow, 1).toString();
            String address = tblLocations.getValueAt(selectedRow, 2).toString();
            String city = tblLocations.getValueAt(selectedRow, 3).toString();
            String state = tblLocations.getValueAt(selectedRow, 4).toString();
            String country = tblLocations.getValueAt(selectedRow, 5).toString();

            txtLocationId.setText(locationId);
            txtLocationName.setText(locationName);
            txtAddress.setText(address);
            txtCity.setText(city);
            txtState.setText(state);
            txtCountry.setText(country);
        }
    }

    private void clearFields() {
        txtLocationId.setText("");
        txtLocationName.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtCountry.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LocationTableGUI::new);
    }

    private class Location {
        private String locationId;
        private String locationName;
        private String address;
        private String city;
        private String state;
        private String country;

        public Location(String locationId, String locationName, String address, String city, String state, String country) {
            this.locationId = locationId;
            this.locationName = locationName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.country = country;
        }

        public String getLocationId() {
            return locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }
    }
}
