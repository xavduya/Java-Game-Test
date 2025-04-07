import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Map {
    private BufferedImage tileSet;
    private BufferedImage[][] tiles;
    private int tileSize;

    public Map(String path, int tileSize) {
        this.tileSize = tileSize;

        try {
            tileSet = ImageIO.read(new File("Asset/Sprout Lands - Sprites - Basic pack/Sprout Lands - Sprites - Basic pack/Tilesets/Tilled_Dirt_Wide_v2.png"));
            int rows = tileSet.getHeight() / tileSize;
            int col = tileSet.getWidth() / tileSize;
            tiles = new BufferedImage[rows][col];

            for (int i = 0; i < rows; i++)
                for (int j = 0; j < col; j++)
                    tiles[i][j] = tileSet.getSubimage(j * tileSize, i * tileSize, tileSize, tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getTile(int row, int col) {
        return tiles[row][col];
    }
}
