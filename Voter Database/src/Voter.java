import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.List;


public class Voter extends JFrame {

    private JTextField voterIdField;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private HashMap<String, String> selectedCandidates = new HashMap<String, String>(); // <Contest, Candidate>
    private JList<String> contestList;
    private JList<String> candidateList;

    private static final String DB_URL = "jdbc:sqlite:C:/Users/markp/Desktop/IdeaProjects/CS Final Project/src/univ.db";

    public Voter() {
        setTitle("Voting Application");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = createLoginPanel();
        JPanel votingPanel = createVotingPanel();

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(loginPanel, "login");
        cardPanel.add(votingPanel, "voting");

        add(cardPanel);

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel voterIdLabel = new JLabel("Enter Voter ID:");
        voterIdField = new JTextField(5); // Set the size of the text field to 5 characters
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String voterId = voterIdField.getText();
                if (isValidVoterId(voterId)) {
                    cardLayout.show(cardPanel, "voting");
                } else {
                    JOptionPane.showMessageDialog(Voter.this, "Invalid Voter ID");
                }
            }
        });

        panel.add(voterIdLabel, BorderLayout.WEST);
        panel.add(voterIdField, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create list models
        DefaultListModel<String> contestListModel = new DefaultListModel<>();
        DefaultListModel<String> candidateListModel = new DefaultListModel<>();
        DefaultListModel<String> selectedCandidateListModel = new DefaultListModel<>(); // New model for selected candidates



        // Create labels
        JLabel contestLabel = new JLabel("Contests");
        JLabel candidateLabel = new JLabel("Candidates");
        JLabel selectedCandidateLabel = new JLabel("Selected Candidates");

        // Set font size for labels
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        contestLabel.setFont(labelFont);
        candidateLabel.setFont(labelFont);
        selectedCandidateLabel.setFont(labelFont);




        // Create list boxes
        contestList = new JList<>(contestListModel);
        candidateList = new JList<>(candidateListModel);
        JList<String> selectedCandidateList = new JList<>(selectedCandidateListModel); // New list box for selected candidates

        // Set font size for lists

        // Set font size for lists
        Font listFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        contestList.setFont(listFont);
        candidateList.setFont(listFont);


        // Populate contest list
        populateContests(contestListModel);

        // Add selection listener to contest list
        contestList.addListSelectionListener(e -> {
            String selectedContest = contestList.getSelectedValue();
            if (selectedContest != null) {
                // Populate candidate list based on selected contest
                populateCandidates(selectedContest, candidateListModel);
            }
        });

        candidateList.addListSelectionListener(e -> {
            String selectedContest = contestList.getSelectedValue();
            String selectedCandidate = candidateList.getSelectedValue();
            if (selectedContest != null && selectedCandidate != null) {
                // Update the selected candidate for the contest in the map
                selectedCandidates.put(selectedContest, selectedCandidate);
            }
        });



        // Create scroll panes
        JScrollPane contestScrollPane = new JScrollPane(contestList);
        JScrollPane candidateScrollPane = new JScrollPane(candidateList);
        JScrollPane selectedCandidateScrollPane = new JScrollPane(selectedCandidateList); // Scroll pane for selected candidates list

        // Set preferred sizes for scroll panes
        Dimension scrollPaneSize = new Dimension(300, 400);
        contestScrollPane.setPreferredSize(scrollPaneSize);
        candidateScrollPane.setPreferredSize(scrollPaneSize);
        selectedCandidateScrollPane.setPreferredSize(scrollPaneSize); // Set preferred size for selected candidates list scroll pane

        // Add components to panel
        JPanel listsPanel = new JPanel(new GridLayout(1, 3)); // Use GridLayout to arrange the list boxes horizontally
        listsPanel.add(contestScrollPane);
        listsPanel.add(candidateScrollPane);
        listsPanel.add(selectedCandidateScrollPane); // Add selected candidates list to the panel

        panel.add(listsPanel, BorderLayout.CENTER); // Add the lists panel to the center

        // Create vote button
        JButton selectCandidate = new JButton("Select Candidate");
        selectCandidate.setFont(listFont);
        selectCandidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected candidate from the candidate list
                String selectedCandidate = candidateList.getSelectedValue();
                String selectedContest = contestList.getSelectedValue();
                if (selectedCandidate != null && selectedContest != null) {
                    addSelectedCandidate(selectedCandidate, selectedCandidateListModel);
                } else {
                    // Notify the user to select both contest and candidate
                    JOptionPane.showMessageDialog(Voter.this, "Please select both a contest and a candidate.");
                }
            }
        });


        // Create submit button
        JButton submitButton = new JButton("Submit Votes");
        submitButton.setFont(listFont);
        submitButton.addActionListener(e -> {
            submitVotes(selectedCandidateListModel);
        });

        JButton clearButton = new JButton("Clear Choices");
        clearButton.setFont(listFont);
        clearButton.setPreferredSize(selectCandidate.getPreferredSize());
        clearButton.addActionListener(e -> clearSelectedCandidates(selectedCandidateListModel));

        // Add the button to a panel if needed

        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.focus", new Color(0, 128, 255)); // Focus color
        UIManager.put("Button.opaque", true);

        // Create panel for labels
        JPanel labelPanel = new JPanel(new GridLayout(1, 3));
        labelPanel.add(contestLabel);
        labelPanel.add(candidateLabel);
        labelPanel.add(selectedCandidateLabel);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(selectCandidate);
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);


        // Add button panel to main panel
        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    private void addSelectedCandidate(String selectedCandidate, DefaultListModel<String> selectedCandidateListModel) {
        // Check if the number of selected candidates is less than three
        if (selectedCandidateListModel.size() < 3) {
            selectedCandidateListModel.addElement(selectedCandidate);
        } else {
            // Notify the user that they can only select three candidates
            JOptionPane.showMessageDialog(Voter.this, "You can only select up to three candidates.");
        }
    }


    private void populateContests(DefaultListModel<String> contestListModel) {
        String sql = "SELECT * FROM Contest";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String contestInfo = rs.getString("Level") + " - " + rs.getString("Office");
                contestListModel.addElement(contestInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateCandidates(String selectedContest, DefaultListModel<String> candidateListModel) {

        candidateListModel.clear();

        // Get the previously selected candidate for this contest
        String previouslySelectedCandidate = selectedCandidates.get(selectedContest);

        String[] parts = selectedContest.split(" - ");
        String level = parts[0];
        String office = parts[1];

        String sql = "SELECT * FROM Candidate WHERE level = ? AND running_for = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, level);
            stmt.setString(2, office);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String candidateInfo = rs.getString("name");
                    candidateListModel.addElement(candidateInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private boolean isValidVoterId(String voterId) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM Voter WHERE voter_id = ?")) {
            stmt.setString(1, voterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    // Display welcome popup
                    JOptionPane.showMessageDialog(null, "Welcome, " + name + "!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Voter ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Voter ID not found in the database
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred, treat as invalid voter ID
        }
    }

    // Method to clear selected candidates from the list box
    private void clearSelectedCandidates(DefaultListModel<String> selectedCandidateListModel) {
        selectedCandidateListModel.clear();
    }

    private void submitVotes(DefaultListModel<String> selectedCandidateListModel) {
        Set<String> submittedCandidates = new HashSet<>();
        boolean duplicateCandidate = false;

        if ( selectedCandidateListModel.getSize() == 0){
            JOptionPane.showMessageDialog(Voter.this, "Please select at least one candidate.");
            return;
        }


        // Iterate through the selected candidates
        for (int i = 0; i < selectedCandidateListModel.getSize(); i++) {
            String candidate = selectedCandidateListModel.getElementAt(i);

            // Check if the candidate has already been submitted
            if (submittedCandidates.contains(candidate)) {
                duplicateCandidate = true;
                break;
            } else {
                submittedCandidates.add(candidate);
            }
        }

        // If there's a duplicate candidate, show a message
        if (duplicateCandidate) {
            JOptionPane.showMessageDialog(Voter.this, "You cannot submit the same candidate multiple times.");
        } else {
            // Submit votes if there are no duplicate candidates
            // Example: submitVotes(selectedCandidates);
            JOptionPane.showMessageDialog(Voter.this, "Votes submitted successfully.");

            // Iterate over the selected candidates
            for (int i = 0; i < selectedCandidateListModel.size(); i++) {
                String selectedCandidate = selectedCandidateListModel.get(i);
                // Update the database to reflect the vote for the selected candidate
                updateCandidateVotes(selectedCandidate);
            }

        }



    }

    private void updateCandidateVotes(String selectedCandidate) {
        String sql = "UPDATE Candidate SET num_votes = num_votes + 1 WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selectedCandidate);
            // Execute the update statement
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vote for " + selectedCandidate + " successfully recorded.");

                Timer timer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Switch back to the login panel
                        cardLayout.show(cardPanel, "login");
                    }
                });
                timer.setRepeats(false); // Ensure the timer only runs once
                timer.start();

            } else {
                System.out.println("Failed to record vote for " + selectedCandidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Voter::new);
        //printContests();
        //printCandidates();

    }
}
