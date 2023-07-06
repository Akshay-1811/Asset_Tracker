import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTableGUI extends JFrame {
    private JTextField txtUserId, txtUsername, txtPassword, txtEmail, txtPhone;
    private JTable tblUsers;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public UserTableGUI() {
        initializeUI();
        connectToDatabase();
        displayUsers();
    }

    private void initializeUI() {
        txtUserId = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        tblUsers = new JTable();
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.getSelectionModel().addListSelectionListener(e -> selectUser());

        JScrollPane scrollPane = new JScrollPane(tblUsers);

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

        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtUserId, gbc);
        gbc.gridy++;
        panel.add(txtUsername, gbc);
        gbc.gridy++;
        panel.add(txtPassword, gbc);
        gbc.gridy++;
        panel.add(txtEmail, gbc);
        gbc.gridy++;
        panel.add(txtPhone, gbc);

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

        btnAdd.addActionListener(e -> insertUser());

        btnModify.addActionListener(e -> modifyUser());

        btnDelete.addActionListener(e -> deleteUser());

        btnDisplay.addActionListener(e -> displayUsers());

        setTitle("User Table");
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

    private void insertUser() {
        String userId = txtUserId.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        try {
            String query = "INSERT INTO users (user_id, username, password, email, phone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.executeUpdate();

            clearFields();
            displayUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = txtUserId.getText();
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            try {
                String query = "UPDATE users SET username=?, password=?, email=?, phone=? WHERE user_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email);
                statement.setString(4, phone);
                statement.setString(5, userId);
                statement.executeUpdate();

                clearFields();
                displayUsers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to modify.");
        }
    }

    private void deleteUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = tblUsers.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM users WHERE user_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, userId);
                    statement.executeUpdate();

                    clearFields();
                    displayUsers();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void displayUsers() {
        try {
            String query = "SELECT * FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                users.add(new User(userId, username, password, email, phone));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"User ID", "Username", "Password", "Email", "Phone"});

            for (User user : users) {
                model.addRow(new Object[]{user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone()});
            }

            tblUsers.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectUser() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) tblUsers.getValueAt(selectedRow, 0);
            String username = tblUsers.getValueAt(selectedRow, 1).toString();
            String password = tblUsers.getValueAt(selectedRow, 2).toString();
            String email = tblUsers.getValueAt(selectedRow, 3).toString();
            String phone = tblUsers.getValueAt(selectedRow, 4).toString();

            txtUserId.setText(String.valueOf(userId));
            txtUsername.setText(username);
            txtPassword.setText(password);
            txtEmail.setText(email);
            txtPhone.setText(phone);
        }
    }

    private void clearFields() {
        txtUserId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserTableGUI::new);
    }

    private class User {
        private int userId;
        private String username;
        private String password;
        private String email;
        private String phone;

        public User(int userId, String username, String password, String email, String phone) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }
}
