import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;

public class MainMenu extends JFrame {
    private Image backgroundImage;
    private static final String BACKGROUND_PATH = "Assets/cave_background.png";
    private static final String BUTTON_BG_PATH = "Assets/Rectangle.png";
    private List<Integer> highScores = new ArrayList<>();

    public MainMenu() {
        initializeFrame();
        loadBackgroundImage();
        loadHighScores(); // Load high scores when the menu starts

        JPanel backgroundPanel = createBackgroundPanel();
        JLabel titleLabel = createTitleLabel();
        JPanel buttonPanel = createButtonPanel();

        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.EAST);

        add(buttonPanel);
        add(backgroundPanel);
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Cave Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadBackgroundImage() {
        try {
            File file = new File(BACKGROUND_PATH);
            if (file.exists()) {
                backgroundImage = new ImageIcon(BACKGROUND_PATH).getImage();
                return;
            }

            URL resourceUrl = getClass().getResource("/" + BACKGROUND_PATH);
            if (resourceUrl != null) {
                backgroundImage = new ImageIcon(resourceUrl).getImage();
                return;
            }

            resourceUrl = getClass().getResource("/Assets/cave background.png");
            if (resourceUrl != null) {
                backgroundImage = new ImageIcon(resourceUrl).getImage();
                return;
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }

        backgroundImage = null;
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Cave Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        return titleLabel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(null);
        buttonPanel.setPreferredSize(new Dimension(200, 200));
        buttonPanel.setBounds(10, 350, 250, 200);

        ImageIcon buttonBackground = loadButtonBackground();

        JButton startButton = createStyledButton("Start Game", buttonBackground, 10, 10);
        JButton highScoresButton = createStyledButton("High Scores", buttonBackground, 10, 70);
        JButton exitButton = createStyledButton("Exit", buttonBackground, 10, 130);

        startButton.addActionListener(e -> {
            dispose();
            new Game();
        });

        highScoresButton.addActionListener(e -> showHighScores());

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(startButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    private void showHighScores() {
        // Create a panel to display the high scores
        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBackground(new Color(0, 0, 0, 200));

        JLabel titleLabel = new JLabel("TOP 10 HIGH SCORES");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoresPanel.add(titleLabel);

        scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (highScores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No scores yet!");
            noScoresLabel.setFont(new Font("Serif", Font.PLAIN, 18));
            noScoresLabel.setForeground(Color.WHITE);
            noScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoresPanel.add(noScoresLabel);
        } else {
            for (int i = 0; i < Math.min(highScores.size(), 10); i++) {
                JLabel scoreLabel = new JLabel((i + 1) + ". " + highScores.get(i));
                scoreLabel.setFont(new Font("Serif", Font.PLAIN, 18));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                scoresPanel.add(scoreLabel);
            }
        }

        // Create a scroll pane if there are many scores
        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(this, scrollPane, "High Scores", JOptionPane.PLAIN_MESSAGE);
    }

    private void loadHighScores() {
        // In a real application, you would load these from a file or database
        // For now, we'll just create some dummy data
        highScores.add(5000);
        highScores.add(4200);
        highScores.add(3800);
        highScores.add(3500);
        highScores.add(3200);
        highScores.add(3000);
        highScores.add(2800);
        highScores.add(2500);
        highScores.add(2200);
        highScores.add(2000);

        // Sort in descending order
        Collections.sort(highScores, Collections.reverseOrder());
    }

    private ImageIcon loadButtonBackground() {
        try {
            File file = new File(BUTTON_BG_PATH);
            if (file.exists()) {
                return new ImageIcon(BUTTON_BG_PATH);
            }

            URL resourceUrl = getClass().getResource("/" + BUTTON_BG_PATH);
            if (resourceUrl != null) {
                return new ImageIcon(resourceUrl);
            }

            resourceUrl = getClass().getResource("/Assets/Rectangle.png");
            if (resourceUrl != null) {
                return new ImageIcon(resourceUrl);
            }
        } catch (Exception e) {
            System.err.println("Error loading button background: " + e.getMessage());
        }

        return null;
    }

    private JButton createStyledButton(String text, ImageIcon background, int x, int y) {
        JButton button = new JButton(text, background);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        button.setBounds(x, y, 200, 50);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Serif", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        return button;
    }
}