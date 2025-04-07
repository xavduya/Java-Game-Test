import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends GameObject {
    private BufferedImage image;
    private int health = 2;
    private int width = 32;
    private int height = 32;
    private Random random;
    private int directionChangeTimer = 0;
    private int directionChangeInterval = 60;
    float moveSpeed = 2f;
    private boolean isOnGround = false;
    private final float GRAVITY = 0.3f;
    private final float MAX_FALL_SPEED = 5f;
    private Handler handler;

    public Enemy(int x, int y, Handler handler) {
        super(x, y);
        this.handler = handler;
        random = new Random();
        createPlaceholderImage();

        // Start with random direction
        velx = random.nextBoolean() ? moveSpeed : -moveSpeed;
    }

    private void createPlaceholderImage() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.fillOval(5, 5, 10, 10);
        g.fillOval(17, 5, 10, 10);
        g.drawArc(5, 10, 22, 15, 0, -180);
        g.dispose();
    }

    @Override
    public void tick() {
        // Random movement logic
        directionChangeTimer++;
        if (directionChangeTimer >= directionChangeInterval) {
            directionChangeTimer = 0;
            if (random.nextFloat() < 0.3f) {
                velx = random.nextBoolean() ? moveSpeed : -moveSpeed;
            }
            // Small chance to pause
            if (random.nextFloat() < 0.1f) {
                velx = 0;
            }
        }

        // Apply gravity if not on ground
        if (!isOnGround) {
            vely += GRAVITY;
            if (vely > MAX_FALL_SPEED) {
                vely = MAX_FALL_SPEED;
            }
        } else {
            vely = 0;
        }

        // Check for platform edges if on ground
        if (isOnGround) {
            boolean edgeAhead = false;
            Rectangle checkArea = (velx > 0) ?
                    new Rectangle(x + width, y + height, 5, 5) :
                    new Rectangle(x - 5, y + height, 5, 5);

            // Check against all platforms
            for (GameObject obj : handler.object) {
                if (obj instanceof Platform) {
                    Platform p = (Platform)obj;
                    if (checkArea.intersects(p.getBounds())) {
                        edgeAhead = true;
                        break;
                    }
                }
            }

            if (!edgeAhead) {
                velx *= -1; // Reverse direction at edges
            }
        }

        // Apply movement
        x += velx;
        y += vely;

        // Prevent going off left edge
        if (x <= 0) {
            x = 0;
            velx = moveSpeed;
        }

        // Reset ground state (will be set by collision checks)
        isOnGround = false;
    }

    public boolean takeDamage(int damage) {
        health -= damage;
        return health <= 0; // Return whether enemy died
    }

    public void setIsOnGround(boolean isOnGround) {
        this.isOnGround = isOnGround;
        if (isOnGround) {
            vely = 0;
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Rectangle getBoundsBottom() {
        return new Rectangle(x + 5, y + height - 5, width - 10, 5);
    }

    public Rectangle getBoundsTop() {
        return new Rectangle(x + 5, y, width - 10, 5);
    }

    public Rectangle getBoundsLeft() {
        return new Rectangle(x, y + 5, 5, height - 10);
    }

    public Rectangle getBoundsRight() {
        return new Rectangle(x + width - 5, y + 5, 5, height - 10);
    }
}