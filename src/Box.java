import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Box extends GameObject {

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
            // Use an absolute file path (temporary workaround)
            File file = new File("C:/Users/User/Desktop/Java Game Test/Assets/MainCharacter(FreePack)/MainChar - idle.png");
            System.out.println("Loading sprite sheet from: " + file.getAbsolutePath());
    
            // Check if the file exists
            if (!file.exists()) {
                System.out.println("File does not exist: " + file.getAbsolutePath());
                return;
            }
    
            // Check if the file is readable
            if (!file.canRead()) {
                System.out.println("File is not readable: " + file.getAbsolutePath());
                return;
            }
    
            // Load the sprite sheet
            BufferedImage spriteSheet = ImageIO.read(file);
            if (spriteSheet == null) {
                System.out.println("Failed to load sprite sheet: ImageIO.read() returned null.");
                return;
            }
    
            // Print sprite sheet dimensions
            System.out.println("Sprite sheet dimensions: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
    
            // Extract frames from the sprite sheet
            sprites = new BufferedImage[4];
            sprites[0] = spriteSheet.getSubimage(0, 0, spriteWidth, spriteHeight); // W
            sprites[1] = spriteSheet.getSubimage(spriteWidth, 0, spriteWidth, spriteHeight); // S
            sprites[2] = spriteSheet.getSubimage(0, spriteHeight, spriteWidth, spriteHeight); // A
            sprites[3] = spriteSheet.getSubimage(spriteWidth, spriteHeight, spriteWidth, spriteHeight); // D
    
            System.out.println("Sprite sheet loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load sprite sheet: " + e.getMessage());
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

    // Draw the current sprite frame
    g.drawImage(sprites[currentSpriteIndex], x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, spriteWidth, spriteHeight);
    }
}