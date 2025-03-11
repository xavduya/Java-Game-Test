import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Cave Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("Assets/cave background.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        
        JLabel titleLabel = new JLabel("Cave Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(null); 
        buttonPanel.setPreferredSize(new Dimension(200, 200)); 
        buttonPanel.setBounds(10,350, 250,200);

        ImageIcon rectangleIcon = new ImageIcon("Assets/Rectangle.png");
        JButton startButton = new JButton("Start Game", rectangleIcon);
        startButton.setHorizontalTextPosition(JButton.CENTER);
        startButton.setVerticalTextPosition(JButton.CENTER);
        startButton.setBounds(10, 10, 200, 50); 
        startButton.setBorderPainted(false); 
        startButton.setContentAreaFilled(false); 
        startButton.setFocusPainted(false); 

        JButton optionsButton = new JButton("Options", rectangleIcon);
        optionsButton.setHorizontalTextPosition(JButton.CENTER);
        optionsButton.setVerticalTextPosition(JButton.CENTER);
        optionsButton.setBounds(10, 70, 200, 50); 
        optionsButton.setBorderPainted(false); 
        optionsButton.setContentAreaFilled(false); 
        optionsButton.setFocusPainted(false); 

        JButton exitButton = new JButton("Exit", rectangleIcon);
        exitButton.setHorizontalTextPosition(JButton.CENTER);
        exitButton.setVerticalTextPosition(JButton.CENTER);
        exitButton.setBounds(10, 130, 200, 50); 
        exitButton.setBorderPainted(false); 
        exitButton.setContentAreaFilled(false); 
        exitButton.setFocusPainted(false); 

        // Style the buttons
        startButton.setFont(new Font("Serif", Font.PLAIN, 24));
        optionsButton.setFont(new Font("Serif", Font.PLAIN, 24));
        exitButton.setFont(new Font("Serif", Font.PLAIN, 24));

        startButton.setBackground(Color.DARK_GRAY);
        optionsButton.setBackground(Color.DARK_GRAY);
        exitButton.setBackground(Color.DARK_GRAY);

        startButton.setForeground(Color.WHITE);
        optionsButton.setForeground(Color.WHITE);
        exitButton.setForeground(Color.WHITE);


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                new Game();
    
            }
        });

        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainMenu.this, "Opening options...");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(exitButton);

        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.EAST); // Change to EAST for bottom right alignment

        add(buttonPanel);
        add(backgroundPanel);
        setVisible(true);
    }

    
}