import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Box extends GameObject {

    
    private BufferedImage spriteSheet;
    private BufferedImage sprites[][];
    private int currentFrame = 0;
    private int currentDirection = 0;
    private int spriteWidth = 32; 
    private int spriteHeight = 32; 
    private int frameDelay = 5;
    private int frameCounter = 0;

    public Box(int x, int y) {
        super(x, y);
        loadSprites(); 
    }

    private void loadSprites() {
        try {
           
            spriteSheet = ImageIO.read(new File("Assets/Sprout Lands - Sprites - Basic pack/Sprout Lands - Sprites - Basic pack/Characters/Basic Charakter Spritesheet.png"));

            int rows = 4; 
            int cols = 3;  
            sprites = new BufferedImage[rows][cols];

            for (int i = 0; i < rows; i++) {  
                for (int j = 0; j < cols; j++) {  
                    sprites[i][j] = spriteSheet.getSubimage(
                        j * spriteWidth,  
                        i * spriteHeight, 
                        spriteWidth,     
                        spriteHeight      
                    );
                }
            }

            System.out.println("Sprites loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load spritesheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        x += velx;
        y += vely;

        if (velx > 0) {
            currentDirection = 3; // Right D
        } else if (velx < 0) {
            currentDirection = 2; // Left A
        } else if (vely > 0) {
            currentDirection = 1; // Down S
        } else if (vely < 0) {
            currentDirection = 0; // Up W
        }

        if(velx != 0 || vely != 0) {
            frameCounter++;
            if(frameCounter >= frameDelay) {
                currentFrame = (currentFrame + 1) %3;
                frameCounter = 0;
            } else {
                currentFrame = 1;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (sprites [currentDirection][currentFrame] != null) {
            g.drawImage(sprites[currentDirection][currentFrame],x,y,null);
        } else {
            // Fallback: Draw a black rectangle if the sprite fails to load
            g.setColor(Color.black);
            g.fillRect(x, y, spriteWidth, spriteHeight);
            return;
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, spriteWidth, spriteHeight);
    }
}