import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    private Box box;

    public KeyInput(Box box) {
        this.box = box;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Movement keys
        if (key == KeyEvent.VK_W) { //up
            box.setVely(-2);
        }
        if (key == KeyEvent.VK_S) { //down
            box.setVely(2);
        }
        if (key == KeyEvent.VK_A) { //left
            box.setVelx(-2);
        }
        if (key == KeyEvent.VK_D) { //right
            box.setVelx(2);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            box.setVely(0);
        }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            box.setVelx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }
}