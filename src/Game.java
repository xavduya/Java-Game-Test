import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JFrame implements Runnable {
    private boolean running = false;
    private Thread thread;
    private Handler handler;
    private Box box;
    private Camera camera;
    private LinkedList<Platform> platforms = new LinkedList<>();
    private Background background;
    private KeyInput keyInput;
    private LinkedList<Enemy> enemies = new LinkedList<>();
    private boolean resetButtonHovered = false;
    private final Rectangle resetButtonRect = new Rectangle(10, 10, 100, 30);
    boolean gameOver = false;
    private Font uiFont = new Font("Serif", Font.BOLD, 24);
    private int score = 0;
    private int restartTimer = 0;

    // Wave system variables
    private int currentWave = 1;
    private int enemiesDefeated = 0;
    private int enemiesRequiredForNextWave = 3; // Initial requirement
    private boolean waveInProgress = false;
    private int waveDelay = 180; // 3 seconds at 60fps
    private int waveDelayTimer = 0;
    private boolean newWaveStarting = false;
    private int newWaveMessageTimer = 120; // 2 seconds at 60fps

    // Game constants
    private static final int INITIAL_PLAYER_X = 100;
    private static final int INITIAL_PLAYER_Y = 100;
    private static final int PLATFORM_HEIGHT = 500;
    private static final int SCORE_PER_KILL = 100;
    private static final int SCORE_FOR_WAVE_BONUS = 500;
    private static final float ENEMY_DIFFICULTY_MULTIPLIER = 0.2f; // Increases per wave
    private final int RESTART_DELAY = 180; // 3 seconds at 60fps

    public Game() {
        setTitle("Cave Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        handler = new Handler();

        box = new Box(INITIAL_PLAYER_X, INITIAL_PLAYER_Y);
        handler.addObject(box);

        camera = new Camera(0, 0);

        // Load background
        try {
            background = new Background("Assets/cave_background.png", true, false);
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
            background = new Background("", true, false); // Use placeholder
        }

        createPlatforms();
        startWave(currentWave); // Start the first wave

        keyInput = new KeyInput(box, this);
        this.addKeyListener(keyInput);

        // Mouse listeners for UI interaction
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) {
                    resetGame();
                } else if (resetButtonRect.contains(e.getPoint())) {
                    resetGame();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                resetButtonHovered = resetButtonRect.contains(e.getPoint());
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter); // Added to track hover state

        this.setFocusable(true);
        this.requestFocusInWindow();
        setVisible(true);
        start();
    }

    private void createPlatforms() {
        platforms.clear();

        // Main ground platform
        platforms.add(new Platform(0, PLATFORM_HEIGHT, 2000, 50));

        // Floating platforms
        platforms.add(new Platform(300, 400, 200, 20));
        platforms.add(new Platform(600, 350, 200, 20));
        platforms.add(new Platform(900, 300, 200, 20));
        platforms.add(new Platform(1200, 400, 200, 20));

        for (Platform p : platforms) {
            handler.addObject(p);
        }
    }

    private void startWave(int waveNumber) {
        enemies.clear();

        // Calculate number of enemies for this wave
        int enemyCount = 2 + waveNumber; // Increases with each wave

        // Calculate enemy speed for this wave
        float enemySpeedMultiplier = 1.0f + ((waveNumber - 1) * ENEMY_DIFFICULTY_MULTIPLIER);

        Random random = new Random();
        // Spawn enemies on platforms
        for (int i = 0; i < enemyCount; i++) {
            // Select a random platform (excluding main ground)
            Platform p = null;
            do {
                p = platforms.get(random.nextInt(platforms.size()));
            } while (p.getY() >= PLATFORM_HEIGHT);

            Enemy enemy = new Enemy(p.getX() + random.nextInt(p.getWidth() - 32),
                    p.getY() - 32, handler);

            // Scale enemy speed based on wave number
            enemy.moveSpeed *= enemySpeedMultiplier;

            enemies.add(enemy);
            handler.addObject(enemy);
        }

        enemiesRequiredForNextWave = enemies.size();
        waveInProgress = true;
        newWaveStarting = true;
        newWaveMessageTimer = 120; // 2 seconds
    }

    private void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0; // 60 FPS
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        this.createBufferStrategy(3);
        BufferStrategy bs = this.getBufferStrategy();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            boolean shouldRender = false;

            while (delta >= 1) {
                processInput();
                tick();
                delta--;
                shouldRender = true;
            }

            // Only render when needed
            if (shouldRender) {
                render(bs);
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }

            // Limit frame rate by sleeping
            try {
                // Calculate how long to sleep to maintain 60 FPS
                long sleepTime = (long)((lastTime + nsPerFrame - System.nanoTime()) / 1000000);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                } else {
                    // Yield CPU if we're behind schedule
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    private void processInput() {
        keyInput.processJump();
    }

    public void render(BufferStrategy bs) {
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        // Clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Translate for camera
        g2d.translate(-camera.getX(), -camera.getY());

        // Draw world
        background.render(g, camera.getX(), camera.getY(), getWidth(), getHeight());
        handler.render(g);

        // Reset translation for UI elements
        g2d.translate(camera.getX(), camera.getY());
        drawUI(g);

        g.dispose();
        bs.show();
    }

    private void drawUI(Graphics g) {
        // Reset button
        g.setColor(resetButtonHovered ? new Color(200, 50, 50) : new Color(150, 50, 50));
        g.fillRect(resetButtonRect.x, resetButtonRect.y, resetButtonRect.width, resetButtonRect.height);
        g.setColor(Color.WHITE);
        g.drawString("Reset", resetButtonRect.x + 10, resetButtonRect.y + 20);

        // Health display
        g.setColor(Color.WHITE);
        g.setFont(uiFont);
        g.drawString("HP: " + box.getHealth(), 20, 60);

        // Score display
        g.drawString("Score: " + score, getWidth() - 150, 40);

        // Wave information
        g.drawString("Wave: " + currentWave, getWidth() - 150, 70);

        if (newWaveStarting) {
            // Show wave announcement
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.YELLOW);
            String waveText = "WAVE " + currentWave;
            int textWidth = g.getFontMetrics().stringWidth(waveText);
            g.drawString(waveText, getWidth()/2 - textWidth/2, getHeight()/2 - 50);

            // Show enemies remaining if wave in progress
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String enemiesText = "Enemies: " + enemies.size();
            textWidth = g.getFontMetrics().stringWidth(enemiesText);
            g.drawString(enemiesText, getWidth()/2 - textWidth/2, getHeight()/2);
        } else if (!waveInProgress && !gameOver) {
            // Show next wave countdown
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.YELLOW);
            String nextWaveText = "Next Wave in: " + (waveDelay - waveDelayTimer) / 60 + "s";
            int textWidth = g.getFontMetrics().stringWidth(nextWaveText);
            g.drawString(nextWaveText, getWidth()/2 - textWidth/2, 100);
        }

        // Game over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, getWidth()/2 - textWidth/2, getHeight()/2 - 80);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restartText = "Click to restart";
            textWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, getWidth()/2 - textWidth/2, getHeight()/2 + 20);

            // Display final score and wave reached
            String scoreText = "Final Score: " + score;
            textWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, getWidth()/2 - textWidth/2, getHeight()/2 - 20);

            String waveText = "Waves Completed: " + (currentWave - 1);
            textWidth = g.getFontMetrics().stringWidth(waveText);
            g.drawString(waveText, getWidth()/2 - textWidth/2, getHeight()/2 + 60);
        }
    }

    public void tick() {
        if (gameOver) {
            restartTimer++;
            if (restartTimer >= RESTART_DELAY) {
                resetGame();
            }
            return;
        }

        handler.tick();
        camera.tick(box);

        // Prevent player from going out of bounds
        keepPlayerInBounds();

        handlePlayerPlatformCollisions();
        handleEnemyLogic();
        handleWaveSystem();

        // Decrease the new wave message timer
        if (newWaveStarting) {
            newWaveMessageTimer--;
            if (newWaveMessageTimer <= 0) {
                newWaveStarting = false;
            }
        }
    }

    private void handleWaveSystem() {
        // Check if wave is complete
        if (waveInProgress && enemies.isEmpty()) {
            waveInProgress = false;
            waveDelayTimer = 0;
            score += SCORE_FOR_WAVE_BONUS * currentWave; // Bonus for completing wave
            currentWave++; // Increment wave
        }

        // Start new wave after delay
        if (!waveInProgress && !gameOver) {
            waveDelayTimer++;
            if (waveDelayTimer >= waveDelay) {
                startWave(currentWave);
            }
        }
    }

    private void keepPlayerInBounds() {
        if (box.getX() < camera.getX()) {
            box.setX(camera.getX());
            box.setVelx(0);
        }

        if (box.getX() + box.spriteWidth > camera.getX() + 800) {
            box.setX(camera.getX() + 800 - box.spriteWidth);
            box.setVelx(0);
        }

        if (box.getY() > camera.getY() + 600 + 100) {
            box.takeDamage();
            if (box.getHealth() <= 0) {
                gameOver = true;
            } else {
                for (Platform p : platforms) {
                    if (p.getX() > camera.getX() && p.getX() < camera.getX() + 800) {
                        box.reset((int)p.getX(), (int)p.getY() - box.spriteHeight);
                        break;
                    }
                }
            }
        }
    }

    private void handlePlayerPlatformCollisions() {
        box.setIsOnGround(false);

        for (Platform p : platforms) {
            if (box.getBoundsBottom().intersects(p.getBounds())) {
                box.setIsOnGround(true);
                box.setVely(0);
                box.setY(p.getY() - box.spriteHeight);
            }

            if (box.getBoundsTop().intersects(p.getBounds())) {
                box.setVely(0);
                box.setY(p.getY() + p.getHeight());
            }

            if (box.getBoundsLeft().intersects(p.getBounds())) {
                box.setVelx(0);
                box.setX(p.getX() + p.getWidth());
            }

            if (box.getBoundsRight().intersects(p.getBounds())) {
                box.setVelx(0);
                box.setX(p.getX() - box.spriteWidth);
            }
        }
    }

    private void handleEnemyLogic() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();

            boolean onAnyPlatform = false;
            for (Platform p : platforms) {
                if (e.getBoundsBottom().intersects(p.getBounds())) {
                    e.setIsOnGround(true);
                    e.setY(p.getY() - e.getBounds().height);
                    onAnyPlatform = true;
                }

                if (e.getBoundsLeft().intersects(p.getBounds())) {
                    e.setVelx(e.moveSpeed);
                    e.setX(p.getX() + p.getWidth());
                }

                if (e.getBoundsRight().intersects(p.getBounds())) {
                    e.setVelx(-e.moveSpeed);
                    e.setX(p.getX() - e.getBounds().width);
                }
            }

            if (!onAnyPlatform) {
                e.setIsOnGround(false);
            }

            if (e.getY() > getHeight() + 100) {
                handler.removeObject(e);
                enemyIterator.remove();
                continue;
            }

            if (e.getBounds().intersects(box.getBounds()) && !box.isInvulnerable) {
                box.takeDamage();
                if (box.getHealth() <= 0) {
                    gameOver = true;
                }
            }

            if (box.isAttacking() && box.getAttackHitbox() != null &&
                    box.getAttackHitbox().intersects(e.getBounds())) {
                if (e.takeDamage(1)) {
                    handler.removeObject(e);
                    enemyIterator.remove();
                    score += SCORE_PER_KILL * currentWave; // Scale score with wave
                }
            }
        }
    }

    private void resetGame() {
        restartTimer = 0;
        handler.object.clear();
        platforms.clear();
        enemies.clear();

        box = new Box(INITIAL_PLAYER_X, INITIAL_PLAYER_Y);
        handler.addObject(box);
        camera = new Camera(0, 0);

        createPlatforms();
        currentWave = 1;
        startWave(currentWave);
        gameOver = false;
        score = 0;
        waveInProgress = true;
        newWaveStarting = true;
        newWaveMessageTimer = 120;

        // Re-register key events with the new box instance
        keyInput = new KeyInput(box, this);
        this.removeKeyListener(this.getKeyListeners()[0]);
        this.addKeyListener(keyInput);
        this.requestFocusInWindow();
    }
}