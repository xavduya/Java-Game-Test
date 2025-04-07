import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Background {
    private BufferedImage backgroundImage;
    private final int width;
    private final int height;
    private final boolean tileHorizontally;
    private final boolean tileVertically;

    public Background(String imagePath, boolean tileHorizontally, boolean tileVertically) {
        this.tileHorizontally = tileHorizontally;
        this.tileVertically = tileVertically;

        // Default dimensions in case loading fails
        this.width = 800;
        this.height = 600;

        loadBackgroundImage(imagePath);
    }

    private void loadBackgroundImage(String imagePath) {
        try {
            // First try direct file path (for development)
            File file = new File(imagePath);
            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
            }
            // Try resource path (for jar packaging)
            else {
                URL resourceUrl = getClass().getResource(imagePath);
                if (resourceUrl != null) {
                    backgroundImage = ImageIO.read(resourceUrl);
                } else {
                    // Try with a modified path
                    String modifiedPath = imagePath.replace(" ", "%20");
                    resourceUrl = getClass().getResource(modifiedPath);
                    if (resourceUrl != null) {
                        backgroundImage = ImageIO.read(resourceUrl);
                    }
                }
            }

            // If still null, create placeholder
            if (backgroundImage == null) {
                System.out.println("Background image not found at: " + imagePath + ". Using placeholder.");
                createPlaceholderBackground();
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            createPlaceholderBackground();
        }
    }

    private void createPlaceholderBackground() {
        // Create a simple gradient as placeholder
        backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backgroundImage.createGraphics();

        // Create a dark blue/purple gradient similar to cave image
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 12, 28),
                0, height, new Color(45, 40, 62)
        );

        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);

        // Add some "stalactite" shapes
        g.setColor(new Color(15, 10, 25));
        for (int i = 0; i < width; i += 50) {
            int triangleHeight = 20 + (int)(Math.random() * 40);
            int[] xPoints = {i, i + 25, i + 50};
            int[] yPoints = {0, triangleHeight, 0};
            g.fillPolygon(xPoints, yPoints, 3);

            // Bottom stalagmites
            triangleHeight = 20 + (int)(Math.random() * 40);
            int[] xPoints2 = {i, i + 25, i + 50};
            int[] yPoints2 = {height, height - triangleHeight, height};
            g.fillPolygon(xPoints2, yPoints2, 3);
        }

        g.dispose();
    }

    public void render(Graphics g, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        if (backgroundImage == null) return;

        // Calculate how much to scroll (parallax effect - background moves slower than foreground)
        float parallaxFactor = 0.3f; // Adjust this value to change parallax effect
        int bgOffsetX = (int)(cameraX * parallaxFactor);
        int bgOffsetY = (int)(cameraY * parallaxFactor);

        // Calculate the drawing region
        int startX = (bgOffsetX / backgroundImage.getWidth()) * backgroundImage.getWidth() - bgOffsetX % backgroundImage.getWidth();
        int startY = (bgOffsetY / backgroundImage.getHeight()) * backgroundImage.getHeight() - bgOffsetY % backgroundImage.getHeight();

        // Draw tiled background if enabled, otherwise draw single image
        if (tileHorizontally && tileVertically) {
            for (int x = startX; x < screenWidth + cameraX; x += backgroundImage.getWidth()) {
                for (int y = startY; y < screenHeight + cameraY; y += backgroundImage.getHeight()) {
                    g.drawImage(backgroundImage, x, y, null);
                }
            }
        } else if (tileHorizontally) {
            // Only tile horizontally
            for (int x = startX; x < screenWidth + cameraX; x += backgroundImage.getWidth()) {
                g.drawImage(backgroundImage, x, startY, null);
            }
        } else if (tileVertically) {
            // Only tile vertically
            for (int y = startY; y < screenHeight + cameraY; y += backgroundImage.getHeight()) {
                g.drawImage(backgroundImage, startX, y, null);
            }
        } else {
            // No tiling, just draw once
            g.drawImage(backgroundImage, startX, startY, null);
        }
    }

    public int getWidth() {
        return backgroundImage != null ? backgroundImage.getWidth() : width;
    }

    public int getHeight() {
        return backgroundImage != null ? backgroundImage.getHeight() : height;
    }
}