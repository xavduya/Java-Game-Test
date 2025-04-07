import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Platform extends GameObject {
    private int width, height;
    private Color color;

    public Platform(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = new Color(100, 70, 40); // Brown color for platforms
    }

    @Override
    public void tick() {
        // Platforms don't move
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
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