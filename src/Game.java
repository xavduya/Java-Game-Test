import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    private boolean running = false;
    private Thread thread;
    private Handler handler;
    private Box box;
    private BufferedImage image;

    public Game() {
        handler = new Handler();
        box = new Box(100, 100); 
        handler.addObject(box);   

        new Window(1920, 1080, "Untitled Game", this);
        start();

        this.addKeyListener(new KeyInput(box)); 
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Load the image asset
        try {
            image = ImageIO.read(getClass().getResource("/assets/image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() { 
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() { 
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        this.createBufferStrategy(3);
        BufferStrategy bs = this.getBufferStrategy();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render(bs);
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    public void tick() {
        handler.tick();
    }

    public void render(BufferStrategy bs) {
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.cyan);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the loaded image
        if (image != null) {
            g.drawImage(image, 50, 50, null);
        }

        handler.render(g);

        g.dispose();
        bs.show();
    }

    public static void main(String args[]) {
        new Game();
    }
}