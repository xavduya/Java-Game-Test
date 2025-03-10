import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Box extends GameObject {

    private BufferedImage spriteup, spritedown, spriteleft, spriteright;
    private BufferedImage[] sprites;
    private int currentSpriteIndex = 0; 
    private int spriteWidth = 32; 
    private int spriteHeight = 32; 

    public Box(int x, int y) {
        super(x, y);
        loadSprites(); 
    }

    private void loadSprites() {
        try {
            // Load each sprite image
            spriteup = ImageIO.read(new File("Assets/8-Directional Gameboy Character Template/gif/up.gif"));
            spritedown = ImageIO.read(new File("Assets/8-Directional Gameboy Character Template/gif/down.gif"));
            spriteleft = ImageIO.read(new File("Assets/8-Directional Gameboy Character Template/gif/left.gif"));
            spriteright = ImageIO.read(new File("Assets/8-Directional Gameboy Character Template/gif/right.gif"));

            // Initialize the sprites array
            sprites = new BufferedImage[4];
            sprites[0] = spriteup;
            sprites[1] = spritedown;
            sprites[2] = spriteleft;
            sprites[3] = spriteright;

            System.out.println("Sprites loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load sprites: " + e.getMessage());
        }
    }

    @Override
    public void tick() {
        x += velx;
        y += vely;

        if (velx > 0) {
            currentSpriteIndex = 3; // Right D
        } else if (velx < 0) {
            currentSpriteIndex = 2; // Left A
        } else if (vely > 0) {
            currentSpriteIndex = 1; // Down S
        } else if (vely < 0) {
            currentSpriteIndex = 0; // Up W
        }
    }

    @Override
    public void render(Graphics g) {
        if (sprites == null || sprites[currentSpriteIndex] == null) {
            // Fallback: Draw a black rectangle if the sprite fails to load
            g.setColor(Color.black);
            g.fillRect(x, y, spriteWidth, spriteHeight);
            return;
        }

        g.drawImage(sprites[currentSpriteIndex], x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, spriteWidth, spriteHeight);
    }
}