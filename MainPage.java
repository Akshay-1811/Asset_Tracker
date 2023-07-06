import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    /*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private JButton retrieveMarksButton;

    public MainPage() {
        // Set frame properties
        setTitle("Asset Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("Asset Tracker");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create panel for the button
        /*JPanel buttonPanel = new JPanel();
        retrieveMarksButton = new JButton("Retrieve Marks");
        buttonPanel.add(retrieveMarksButton);
*/
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu UserMenu = new JMenu("User Details");
        JMenu AssetMenu = new JMenu(" Asset Details");
        JMenu AssignmentMenu = new JMenu("Assignment Details");
        JMenu LocationMenu = new JMenu("Location Details");
        JMenu MaintenanceMenu = new JMenu("Maintenance Details");

        // Create menu item for student menu
        JMenuItem viewUserDetails = new JMenuItem("View User Details");
        viewUserDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserTableGUI();
            }
        });

        // Create menu item for course menu
        JMenuItem viewAssetDetails = new JMenuItem("View Asset Details");
        viewAssetDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AssetTableGUI();
            }
        });

        // Create menu item for enrollment menu
        JMenuItem viewAssignmentDetails = new JMenuItem("View Assignment Details");
        viewAssignmentDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AssignmentTableGUI();
            }
        });

        // Create menu item for semester menu
        JMenuItem viewLocationDetails = new JMenuItem("View Location Details");
        viewLocationDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LocationTableGUI();
            }
        });

        // Create menu item for grade menu
        JMenuItem viewMaintenanceDetails = new JMenuItem("View Maintenance Details");
        viewMaintenanceDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MaintenanceTableGUI();
            }
        });

        // Add menu items to respective menus
        UserMenu.add(viewUserDetails);
        AssetMenu.add(viewAssetDetails);
        AssignmentMenu.add(viewAssignmentDetails);
        LocationMenu.add(viewLocationDetails);
        MaintenanceMenu.add(viewMaintenanceDetails);

        // Add menus to the menu bar
        menuBar.add(UserMenu);
        menuBar.add(AssetMenu);
        menuBar.add(AssignmentMenu);
        menuBar.add(LocationMenu);
        menuBar.add(MaintenanceMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add the button panel to the frame
       // add(buttonPanel, BorderLayout.CENTER);

        // Set button action for "Retrieve Marks"
       
        /*retrieveMarksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Retreive();
            }
        });*/

        // Add window listener to handle maximizing the window
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    System.out.println("Window maximized");
                } else {
                    System.out.println("Window not maximized");
                }
            }
        });

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainPage();
    }
}