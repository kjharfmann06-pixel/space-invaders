import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * GameController class - Main entry point and controller.
 * This class:
 * - Contains the main method to start the application
 * - Instantiates GameModel and GameView
 * - Creates the JFrame to host the GameView
 * - Manages the game loop and updates
 * - Coordinates interactions between Model and View
 */
public class GameController {
    
    private JFrame frame;
    private GameModel gameModel;
    private GameView gameView;
    private Timer gameTimer;
    private boolean[] keysPressed;
    
    /**
     * Constructor for GameController.
     * Initializes all components and sets up the GUI.
     */
    public GameController() {
        // Create the game model
        gameModel = new GameModel();
        
        // Create the game view
        gameView = new GameView(gameModel);
        
        // Create and configure the JFrame
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gameView);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Initialize key tracking array (for left, right, space)
        keysPressed = new boolean[256];
        
        // Set up keyboard input handling
        setupKeyboardInput();
        
        // Start the game loop (60 FPS = 16.67ms per frame)
        startGameLoop();
    }
    
    /**
     * Sets up keyboard event listeners for the game view.
     */
    private void setupKeyboardInput() {
        gameView.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed[e.getKeyCode()] = true;
                
                // Handle immediate input
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameModel.firePlayerBullet();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed[e.getKeyCode()] = false;
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                // Not used
            }
        });
        
        gameView.requestFocusInWindow();
    }
    
    /**
     * Starts the game loop using a Swing Timer.
     * Updates at a dynamic rate based on alien speed.
     */
    private void startGameLoop() {
        int initialInterval = gameModel.getRecommendedTimerInterval();
        gameTimer = new Timer(initialInterval, e -> {
            // Check if game is over
            if (gameModel.getLives() <= 0) {
                gameTimer.stop();
                return;
            }
            
            // Check if we need to adjust timer speed
            int currentInterval = gameModel.getRecommendedTimerInterval();
            if (currentInterval != gameTimer.getDelay()) {
                gameTimer.setDelay(currentInterval);
            }
            
            // Handle continuous key input (move left/right)
            if (keysPressed[KeyEvent.VK_LEFT]) {
                gameModel.movePlayerLeft();
            }
            if (keysPressed[KeyEvent.VK_RIGHT]) {
                gameModel.movePlayerRight();
            }
            
            // Update game logic
            gameModel.update();
            
            // Redraw the view
            gameView.repaint();
        });
        
        gameTimer.start();
    }
    
    /**
     * Main method - Entry point for the application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameController();
            }
        });
    }
}
