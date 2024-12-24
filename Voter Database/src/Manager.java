import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Manager extends JFrame {

    private JList<String> listBox1; // JList to display contests
    private JList<String> listBox2; // JList to display candidates and votes
    private DefaultListModel<String> listModel1; // Model for listBox1
    private DefaultListModel<String> listModel2; // Model for listBox2

    public Manager() {

        // Set up the main frame
        setTitle("Manager Application");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize list models
        listModel1 = new DefaultListModel<>();
        listModel2 = new DefaultListModel<>();

        // Populate list models with data from the database
        populateContests();

        // Create list boxes
        listBox1 = new JList<>(listModel1);
        listBox2 = new JList<>(listModel2);

        // Add list selection listener to listBox1
        listBox1.addListSelectionListener(e -> {
            populateCandidates(); // Populate candidates when a contest is selected
        });

        // Set font size for list boxes
        Font listBoxFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
        listBox1.setFont(listBoxFont);
        listBox2.setFont(listBoxFont);

        // Create panels
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Increased border for spacing
        panel1.add(new JLabel("Contests"), BorderLayout.NORTH);
        panel1.add(new JScrollPane(listBox1), BorderLayout.CENTER);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Increased border for spacing
        panel2.add(new JLabel("Candidates and Votes"), BorderLayout.NORTH);
        panel2.add(new JScrollPane(listBox2), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(panel1);
        mainPanel.add(panel2);

        add(mainPanel);

        setVisible(true);
    }

    // Method to populate listBox1 with contests from the database
    private void populateContests() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/markp/Desktop/IdeaProjects/univ.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Contest")) {
            while (rs.next()) {
                // Construct contest information and add it to the list model
                String contestInfo = rs.getString("Level") + " - " + rs.getString("Office");
                listModel1.addElement(contestInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database connection or query errors
        }
    }

    // Method to populate listBox2 with candidates for the selected contest
    private void populateCandidates() {
        listModel2.clear(); // Clear existing candidates

        // Get the selected contest from listBox1
        String selectedContest = listBox1.getSelectedValue();

        if (selectedContest != null) {
            // Extract the office from the selected contest
            String office = selectedContest.split(" - ")[1];

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/markp/Desktop/IdeaProjects/univ.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Candidate WHERE running_for='" + office + "'")) {
                while (rs.next()) {
                    // Construct candidate information and add it to the list model
                    String candidateInfo = rs.getString("name") + " - Votes: " + rs.getInt("num_votes");
                    listModel2.addElement(candidateInfo);
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle database connection or query errors
            }
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        // Start the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(Manager::new);
    }
}
