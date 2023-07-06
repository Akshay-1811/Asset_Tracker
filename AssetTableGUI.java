import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetTableGUI extends JFrame {
    private JTextField txtAssetId, txtAssetName, txtAssetType, txtPurchaseDate, txtPurchaseCost;
    private JTable tblAssets;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public AssetTableGUI() {
        initializeUI();
        connectToDatabase();
        displayAssets();
    }

    private void initializeUI() {
        txtAssetId = new JTextField();
        txtAssetName = new JTextField();
        txtAssetType = new JTextField();
        txtPurchaseDate = new JTextField();
        txtPurchaseCost = new JTextField();

        tblAssets = new JTable();
        tblAssets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAssets.getSelectionModel().addListSelectionListener(e -> selectAsset());

        JScrollPane scrollPane = new JScrollPane(tblAssets);

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

        panel.add(new JLabel("Asset ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Asset Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Asset Type:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Purchase Date:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Purchase Cost:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtAssetId, gbc);
        gbc.gridy++;
        panel.add(txtAssetName, gbc);
        gbc.gridy++;
        panel.add(txtAssetType, gbc);
        gbc.gridy++;
        panel.add(txtPurchaseDate, gbc);
        gbc.gridy++;
        panel.add(txtPurchaseCost, gbc);

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

        btnAdd.addActionListener(e -> insertAsset());

        btnModify.addActionListener(e -> modifyAsset());

        btnDelete.addActionListener(e -> deleteAsset());

        btnDisplay.addActionListener(e -> displayAssets());

        setTitle("Asset Management");
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

    private void insertAsset() {
        int assetId = Integer.parseInt(txtAssetId.getText());
        String assetName = txtAssetName.getText();
        String assetType = txtAssetType.getText();
        String purchaseDate = txtPurchaseDate.getText();
        float purchaseCost = Float.parseFloat(txtPurchaseCost.getText());

        try {
            String query = "INSERT INTO asset (asset_id, asset_name, asset_type, purchase_date, purchase_cost) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, assetId);
            statement.setString(2, assetName);
            statement.setString(3, assetType);
            statement.setString(4, purchaseDate);
            statement.setFloat(5, purchaseCost);
            statement.executeUpdate();

            clearFields();
            displayAssets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyAsset() {
        int selectedRow = tblAssets.getSelectedRow();
        if (selectedRow >= 0) {
            int assetId = Integer.parseInt(txtAssetId.getText());
            String assetName = txtAssetName.getText();
            String assetType = txtAssetType.getText();
            String purchaseDate = txtPurchaseDate.getText();
            float purchaseCost = Float.parseFloat(txtPurchaseCost.getText());

            try {
                String query = "UPDATE asset SET asset_name=?, asset_type=?, purchase_date=?, purchase_cost=? WHERE asset_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, assetName);
                statement.setString(2, assetType);
                statement.setString(3, purchaseDate);
                statement.setFloat(4, purchaseCost);
                statement.setInt(5, assetId);
                statement.executeUpdate();

                clearFields();
                displayAssets();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an asset to modify.");
        }
    }

    private void deleteAsset() {
        int selectedRow = tblAssets.getSelectedRow();
        if (selectedRow >= 0) {
            int assetId = Integer.parseInt(tblAssets.getValueAt(selectedRow, 0).toString());

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this asset?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM asset WHERE asset_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, assetId);
                    statement.executeUpdate();

                    clearFields();
                    displayAssets();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an asset to delete.");
        }
    }

    private void displayAssets() {
        try {
            String query = "SELECT * FROM asset";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Asset> assets = new ArrayList<>();
            while (resultSet.next()) {
                int assetId = resultSet.getInt("asset_id");
                String assetName = resultSet.getString("asset_name");
                String assetType = resultSet.getString("asset_type");
                String purchaseDate = resultSet.getString("purchase_date");
                float purchaseCost = resultSet.getFloat("purchase_cost");
                assets.add(new Asset(assetId, assetName, assetType, purchaseDate, purchaseCost));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Asset ID", "Asset Name", "Asset Type", "Purchase Date", "Purchase Cost"});

            for (Asset asset : assets) {
                model.addRow(new Object[]{asset.getAssetId(), asset.getAssetName(), asset.getAssetType(), asset.getPurchaseDate(), asset.getPurchaseCost()});
            }

            tblAssets.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectAsset() {
        int selectedRow = tblAssets.getSelectedRow();
        if (selectedRow >= 0) {
            int assetId = Integer.parseInt(tblAssets.getValueAt(selectedRow, 0).toString());
            String assetName = tblAssets.getValueAt(selectedRow, 1).toString();
            String assetType = tblAssets.getValueAt(selectedRow, 2).toString();
            String purchaseDate = tblAssets.getValueAt(selectedRow, 3).toString();
            float purchaseCost = Float.parseFloat(tblAssets.getValueAt(selectedRow, 4).toString());

            txtAssetId.setText(Integer.toString(assetId));
            txtAssetName.setText(assetName);
            txtAssetType.setText(assetType);
            txtPurchaseDate.setText(purchaseDate);
            txtPurchaseCost.setText(Float.toString(purchaseCost));
        }
    }

    private void clearFields() {
        txtAssetId.setText("");
        txtAssetName.setText("");
        txtAssetType.setText("");
        txtPurchaseDate.setText("");
        txtPurchaseCost.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AssetTableGUI::new);
    }

    private class Asset {
        private int assetId;
        private String assetName;
        private String assetType;
        private String purchaseDate;
        private float purchaseCost;

        public Asset(int assetId, String assetName, String assetType, String purchaseDate, float purchaseCost) {
            this.assetId = assetId;
            this.assetName = assetName;
            this.assetType = assetType;
            this.purchaseDate = purchaseDate;
            this.purchaseCost = purchaseCost;
        }

        public int getAssetId() {
            return assetId;
        }

        public String getAssetName() {
            return assetName;
        }

        public String getAssetType() {
            return assetType;
        }

        public String getPurchaseDate() {
            return purchaseDate;
        }

        public float getPurchaseCost() {
            return purchaseCost;
        }
    }
}
