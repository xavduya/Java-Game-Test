import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Platform extends GameObject {
    private int width, height;
    private BufferedImage platformImage;

    public Platform(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;

        try {
            // Load the image from file
            platformImage = ImageIO.read(getClass().getResourceAsStream("Assets/Cave platform.png"));
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to the original color if image fails to load
        }
    }

    @Override
    public void tick() {
        // Platforms don't move
    }

    @Override
    public void render(Graphics g) {
        if (platformImage != null) {
            // Draw the image, scaled to the platform's dimensions
            g.drawImage(platformImage, x, y, width, height, null);
        } else {
            // Fallback to original colored rectangle
            g.setColor(new Color(100, 70, 40));
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}