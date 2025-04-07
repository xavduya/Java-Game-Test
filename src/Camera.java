import java.awt.Rectangle;

public class Camera {
    private float x, y;
    private int worldWidth = 2000; // Match the size of your largest platform
    private int worldHeight = 800; // Adjust based on your level height
    private Rectangle bounds;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(Box player) {
        float targetX = player.getX() - 800/2 + player.spriteWidth/2;
        float targetY = player.getY() - 600/2 + player.spriteHeight/2;


        x += (targetX - x) * 0.05f;
        y += (targetY - y) * 0.05f;

        if (x < 0) {
            x = 0;
        }

        if (x > worldWidth - 800) {
            x = worldWidth - 800;
        }

        if (y < 0) {
            y = 0;
        }

        if (y > worldHeight - 600) {
            y = worldHeight - 600;
        }

        bounds = new Rectangle((int)x, (int)y, 800, 600);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Allow setting the world bounds if needed
    public void setWorldBounds(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
    }
}