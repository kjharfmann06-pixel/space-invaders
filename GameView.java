import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;

/**
 * GameView class - Handles all rendering and display logic.
 * This class:
 * - Extends JPanel to provide a canvas for drawing
 * - Renders the game board, entities, and UI elements
 * - Handles display updates based on the game model state
 * 
 * GameView is housed in a JFrame created by GameController.
 */
public class GameView extends JPanel {
    
    private GameModel gameModel;
    
    /**
     * Constructor for GameView.
     * Sets up the panel with appropriate size and properties.
     * 
     * @param gameModel The game model to render
     */
    public GameView(GameModel gameModel) {
        this.gameModel = gameModel;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        // TODO: Add keyboard and mouse listeners for input handling
    }
    
    /**
     * Renders the game onto the panel.
     * Called automatically when the panel needs to be redrawn.
     * 
     * @param g The Graphics context for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw player
        drawPlayer(g2d);
        
        // Draw aliens
        drawAliens(g2d);
        
        // Draw player bullet
        drawPlayerBullet(g2d);
        
        // Draw alien bullets
        drawAlienBullets(g2d);
        
        // Draw shields with health-based color gradient
        for (Shield shield : gameModel.getShields()) {
            int health = shield.getHealth();
            int maxHealth = shield.getMaxHealth();
            
            // Create color gradient from full green (health=max) to dim red (health=0)
            int red = (maxHealth - health) * 255 / maxHealth;
            int green = health * 255 / maxHealth;
            int blue = 0;
            
            g2d.setColor(new Color(red, green, blue));
            g2d.fillRect((int) shield.getX(), (int) shield.getY(), shield.getWidth(), shield.getHeight());
        }
        
        // Draw UI (score and lives)
        drawUI(g2d);
        
        // Draw game over message if game has ended
        if (gameModel.getLives() <= 0) {
            drawGameOver(g2d);
        }
    }
    
    /**
     * Draws the player ship at the bottom of the screen.
     */
    private void drawPlayer(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        float playerX = gameModel.getPlayerX();
        int playerY = gameModel.getBoardHeight() - gameModel.getPlayerHeight() - 10;
        int playerWidth = gameModel.getPlayerWidth();
        int playerHeight = gameModel.getPlayerHeight();
        
        // Draw player as a simple triangle/ship shape
        g2d.fillRect((int) playerX, playerY, playerWidth, playerHeight);
        
        // Draw a pointer at the top
        int[] xPoints = {(int) playerX + playerWidth / 2, (int) playerX, (int) playerX + playerWidth};
        int[] yPoints = {playerY - 10, playerY, playerY};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draws all alien invaders in their formation.
     */
    private void drawAliens(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        Alien[][] aliens = gameModel.getAliens();
        float formationX = gameModel.getAlienFormationX();
        float formationY = gameModel.getAlienFormationY();
        int alienWidth = gameModel.getAlienWidth();
        int alienHeight = gameModel.getAlienHeight();
        int spacingX = gameModel.getAlienSpacingX();
        int spacingY = gameModel.getAlienSpacingY();
        
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (aliens[row][col] != null) {
                    float alienX = formationX + col * spacingX;
                    float alienY = formationY + row * spacingY;
                    g2d.fillRect((int) alienX, (int) alienY, alienWidth, alienHeight);
                    
                    // Draw some simple details on the alien
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval((int) alienX + 5, (int) alienY + 4, 5, 5);
                    g2d.fillOval((int) alienX + alienWidth - 10, (int) alienY + 4, 5, 5);
                    g2d.setColor(Color.CYAN);
                }
            }
        }
    }
    
    /**
     * Draws the player's bullet if one is in flight.
     */
    private void drawPlayerBullet(Graphics2D g2d) {
        PlayerBullet bullet = gameModel.getPlayerBullet();
        if (bullet != null) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int) bullet.getX() - 2, (int) bullet.getY(), 4, 10);
        }
    }
    
    /**
     * Draws all alien bullets in flight.
     */
    private void drawAlienBullets(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        for (AlienBullet bullet : gameModel.getAlienBullets()) {
            g2d.fillRect((int) bullet.getX() - 2, (int) bullet.getY(), 4, 10);
        }
    }
    
    /**
     * Draws the UI elements: score and lives.
     */
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Draw score on the left
        g2d.drawString("Score: " + gameModel.getScore(), 10, 20);
        
        // Draw lives on the right
        g2d.drawString("Lives: " + gameModel.getLives(), gameModel.getBoardWidth() - 120, 20);
    }
    
    /**
     * Draws the centered game-over message.
     */
    private void drawGameOver(Graphics2D g2d) {
        // Draw a semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(0, 0, gameModel.getBoardWidth(), gameModel.getBoardHeight());
        
        // Draw "GAME OVER" text centered
        String gameOverText = "GAME OVER";
        String scoreText = "Final Score: " + gameModel.getScore();
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics metrics = g2d.getFontMetrics();
        int gameOverX = (gameModel.getBoardWidth() - metrics.stringWidth(gameOverText)) / 2;
        int gameOverY = gameModel.getBoardHeight() / 2 - 30;
        g2d.drawString(gameOverText, gameOverX, gameOverY);
        
        // Draw final score below
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        metrics = g2d.getFontMetrics();
        int scoreX = (gameModel.getBoardWidth() - metrics.stringWidth(scoreText)) / 2;
        int scoreY = gameModel.getBoardHeight() / 2 + 20;
        g2d.drawString(scoreText, scoreX, scoreY);
    }
}
