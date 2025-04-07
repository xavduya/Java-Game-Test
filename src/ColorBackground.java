import java.awt.Color;
import java.awt.Graphics;

public class ColorBackground {
    private Color color;

    public ColorBackground(Color color) {
        this.color = color;
    }

    public void render(Graphics g, int cameraX, int cameraY, int width, int height) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
}