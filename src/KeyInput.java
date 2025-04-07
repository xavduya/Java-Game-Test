import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {
    private final Box box;
    private final Game game;
    private final KeyBindings bindings;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean jumpReleased = true;

    public class KeyBindings {
        public int LEFT = KeyEvent.VK_A;
        public int RIGHT = KeyEvent.VK_D;
        public int JUMP = KeyEvent.VK_SPACE;
        public int ATTACK = KeyEvent.VK_J;
    }

    public KeyInput(Box box, Game game) {
        this.box = box;
        this.game = game;
        this.bindings = new KeyBindings();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (game.gameOver) return;

        int key = e.getKeyCode();
        if (key == bindings.LEFT) {
            leftPressed = true;  // Fixed: set to true when pressed
            updateMovement();
        }
        if (key == bindings.RIGHT) {
            rightPressed = true;  // Fixed: set to true when pressed
            updateMovement();
        }
        if (key == bindings.JUMP && jumpReleased) {
            jumpPressed = true;
            jumpReleased = false;
            processJump();  // Process jump immediately for better responsiveness
        }
        if (key == bindings.ATTACK) {
            box.attack();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == bindings.LEFT) {
            leftPressed = false;
            updateMovement();
        }
        if (key == bindings.RIGHT) {
            rightPressed = false;
            updateMovement();
        }
        if (key == bindings.JUMP) {
            jumpPressed = false;
            jumpReleased = true;
        }
    }

    public void processJump() {
        if (game.gameOver) return;
        if (jumpPressed) {
            box.jump();
            jumpPressed = false;
        }
    }

    private void updateMovement() {
        if (leftPressed && !rightPressed) {
            box.setVelx(-Box.MOVE_SPEED);
        } else if (rightPressed && !leftPressed) {
            box.setVelx(Box.MOVE_SPEED);
        } else {
            box.setVelx(0);
        }
    }
}