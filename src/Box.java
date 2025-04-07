import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Box extends GameObject {
    private BufferedImage spriteSheet;
    private BufferedImage[][] sprites;
    private int currentFrame = 0;
    private int currentDirection = 0; // 0=down, 1=left, 2=right, 3=up
    public int spriteWidth = 32;
    public int spriteHeight = 32;
    private int frameDelay = 5;
    private int frameCounter = 0;
    private int health = 10;
    boolean isInvulnerable = false;
    private int invulnerabilityTimer = 0;
    private final int INVULNERABILITY_DURATION = 60; // 1 second
    private boolean isAttacking = false;
    private int attackCooldown = 0;
    private final int ATTACK_COOLDOWN_MAX = 20; // frames
    private Rectangle attackHitbox;
    private final int ATTACK_RANGE = 50;

    // Platformer physics
    private boolean isJumping = false;
    private boolean isOnGround = false;
    public final float GRAVITY = 0.5f;
    public final float JUMP_FORCE = -12f;
    public final float MAX_FALL_SPEED = 10f;
    public static final float MOVE_SPEED = 5f;

    // Jump buffer system
    private boolean jumpBuffered = false;
    private long jumpBufferTimer = 0;
    private final long JUMP_BUFFER_DURATION = 150;

    // Coyote time - allows jumping slightly after leaving a platform
    private long coyoteTimer = 0;
    private final long COYOTE_TIME_DURATION = 100;
    private boolean canCoyoteJump = false;

    public Box(int x, int y) {
        super(x, y);
        loadSprites();
    }

    private void loadSprites() {
        try {
            // First try direct file path (for development)
            File file = new File("Assets/8-Directional_Gameboy_Character_Template/loose_sprites.png");
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
            }
            // Try resource path (for jar packaging)
            else {
                URL resourceUrl = getClass().getResource("/Assets/8-Directional_Gameboy_Character_Template/loose_sprites.png");
                if (resourceUrl != null) {
                    spriteSheet = ImageIO.read(resourceUrl);
                } else {
                    resourceUrl = getClass().getResource("/Assets/8-Directional Gameboy Character Template/loose sprites.png");
                    if (resourceUrl != null) {
                        spriteSheet = ImageIO.read(resourceUrl);
                    }
                }
            }

            if (spriteSheet == null) {
                System.out.println("Sprite sheet not found. Using placeholder.");
                createPlaceholderSprites();
                return;
            }

            int numRows = 4;
            int numCols = 3;
            sprites = new BufferedImage[numRows][numCols];

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    sprites[row][col] = spriteSheet.getSubimage(
                            col * spriteWidth,
                            row * spriteHeight,
                            spriteWidth,
                            spriteHeight
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading sprites: " + e.getMessage());
            spriteSheet = new BufferedImage(spriteWidth * 3, spriteHeight * 4, BufferedImage.TYPE_INT_ARGB);
            Graphics g = spriteSheet.getGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, spriteSheet.getWidth(), spriteSheet.getHeight());
            g.dispose();
        }
    }

    public void bufferJump() {
        jumpBuffered = true;
        jumpBufferTimer = System.currentTimeMillis();
    }

    public void jump() {
        if (isOnGround) {
            vely = JUMP_FORCE;
            isOnGround = false;
            isJumping = true;
            jumpBuffered = false;
            return;
        }

        if (canCoyoteJump) {
            vely = JUMP_FORCE;
            canCoyoteJump = false;
            isJumping = true;
            jumpBuffered = false;
            return;
        }

        bufferJump();
    }

    public void setIsOnGround(boolean isOnGround) {
        if (isOnGround && !this.isOnGround && jumpBuffered) {
            this.isOnGround = true;
            jumpBuffered = false;
            jump(); // Execute jump
            return;
        }

        if (!isOnGround && this.isOnGround) {
            canCoyoteJump = true;
            coyoteTimer = System.currentTimeMillis();
        }

        this.isOnGround = isOnGround;
        if (isOnGround) {
            isJumping = false;
        }
    }

    @Override
    public void tick() {
        vely += GRAVITY;
        if (vely > MAX_FALL_SPEED) {
            vely = MAX_FALL_SPEED;
        }

        if (jumpBuffered && System.currentTimeMillis() - jumpBufferTimer > JUMP_BUFFER_DURATION) {
            jumpBuffered = false;
        }

        if (canCoyoteJump && System.currentTimeMillis() - coyoteTimer > COYOTE_TIME_DURATION) {
            canCoyoteJump = false;
        }

        // Update position
        x += velx;
        y += vely;

        // Handle direction
        if (velx > 0) {
            currentDirection = 2; // Right
        } else if (velx < 0) {
            currentDirection = 1; // Left
        }

        if (velx != 0 && isOnGround) {
            frameCounter++;
            if (frameCounter >= frameDelay) {
                currentFrame = (currentFrame + 1) % 3;
                frameCounter = 0;
            }
        } else {
            currentFrame = 1; // Default standing frame
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown <= ATTACK_COOLDOWN_MAX / 2) {
                isAttacking = false;
            }
        }

        if (isInvulnerable) {
            invulnerabilityTimer--;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (isInvulnerable && invulnerabilityTimer % 10 < 5) {
            return;
        }

        if (isAttacking) {
            // Draw attacking frame
            int attackFrame = 0;
            g.drawImage(sprites[currentDirection][attackFrame], x, y, null);

            // Draw weapon/sword
            g.setColor(Color.YELLOW);
            if (currentDirection == 1) { // Left
                g.fillRect(x - 20, y + 10, 25, 5);
            } else { // Right
                g.fillRect(x + spriteWidth - 5, y + 10, 25, 5);
            }
        } else {
            // Normal rendering
            if (sprites != null && sprites.length > currentDirection &&
                    sprites[currentDirection].length > currentFrame) {
                g.drawImage(sprites[currentDirection][currentFrame], x, y, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, spriteWidth, spriteHeight);
            }
        }

        if (isAttacking) {
            g.setColor(new Color(255, 255, 0, 100));
            if (currentDirection == 1) { // Left
                g.fillRect(x - ATTACK_RANGE, y, ATTACK_RANGE, spriteHeight);
            } else {
                g.fillRect(x + spriteWidth, y, ATTACK_RANGE, spriteHeight);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, spriteWidth, spriteHeight);
    }

    public Rectangle getBoundsBottom() {
        return new Rectangle(x + 5, y + spriteHeight - 5, spriteWidth - 10, 5);
    }

    public Rectangle getBoundsTop() {
        return new Rectangle(x + 5, y, spriteWidth - 10, 5);
    }

    public Rectangle getBoundsLeft() {
        return new Rectangle(x, y + 5, 5, spriteHeight - 10);
    }

    public Rectangle getBoundsRight() {
        return new Rectangle(x + spriteWidth - 5, y + 5, 5, spriteHeight - 10);
    }

    private void createPlaceholderSprites() {
        int numRows = 4;
        int numCols = 3;
        sprites = new BufferedImage[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                sprites[row][col] = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics g = sprites[row][col].getGraphics();
                g.setColor(Color.RED);
                g.fillRect(0, 0, spriteWidth, spriteHeight);
                g.dispose();
            }
        }
    }

    public void attack() {
        if (attackCooldown <= 0) {
            isAttacking = true;
            attackCooldown = ATTACK_COOLDOWN_MAX;

            // Create attack hitbox based on facing direction
            if (currentDirection == 1) { // Facing left
                attackHitbox = new Rectangle(x - ATTACK_RANGE, y, ATTACK_RANGE, spriteHeight);
            } else { // Facing right (or default)
                attackHitbox = new Rectangle(x + spriteWidth, y, ATTACK_RANGE, spriteHeight);
            }
        }
    }

    public void takeDamage() {
        if (!isInvulnerable) {
            health--;
            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;

            // Knockback effect
            vely = -5;
            velx = (currentDirection == 1) ? 3 : -3;
        }
    }

    public int getHealth() {
        return health;
    }

    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.velx = 0;
        this.vely = 0;
        this.isJumping = false;
        this.isOnGround = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        isInvulnerable = invulnerable;
    }

    public int getInvulnerableTimer() {
        return invulnerabilityTimer;
    }

    public void setInvulnerableTimer(int invulnerableTimer) {
        this.invulnerabilityTimer = invulnerableTimer;
    }

    public void decrementInvulnerableTimer() {
        this.invulnerabilityTimer--;
    }
}