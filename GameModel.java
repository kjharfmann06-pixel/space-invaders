import java.util.ArrayList;
import java.util.List;

/**
 * GameModel class - Contains all game logic and state.
 * This class handles:
 * - Game state management (running, paused, game over)
 * - Entity management (player, enemies, bullets)
 * - Collision detection
 * - Game updates and logic
 * 
 * This class has no Swing dependencies and focuses purely on game mechanics.
 */
public class GameModel {
    
    // Board dimensions
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;
    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 30;
    private static final int ALIEN_WIDTH = 30;
    private static final int ALIEN_HEIGHT = 20;
    
    // Alien formation
    private static final int ALIEN_ROWS = 5;
    private static final int ALIENS_PER_ROW = 11;
    private static final int ALIEN_SPACING_X = 50;
    private static final int ALIEN_SPACING_Y = 50;
    
    // Player state
    private float playerX;
    private int lives;
    private int score;
    
    // Bullets
    private PlayerBullet playerBullet;
    private List<AlienBullet> alienBullets;
    
    // Shields
    private List<Shield> shields;
    
    // Aliens
    private Alien[][] aliens;
    private float alienFormationX;
    private float alienFormationY;
    private int alienDirection; // 1 for right, -1 for left
    private int alienFireCounter;
    private float alienSpeed; // Current alien movement speed
    private int aliensDestroyed; // Number of aliens destroyed (affects speed)
    
    /**
     * Constructor for GameModel.
     * Initializes the game state and entities.
     */
    public GameModel() {
        // Initialize player at bottom center
        playerX = BOARD_WIDTH / 2.0f - PLAYER_WIDTH / 2.0f;
        lives = 3;
        score = 0;
        
        // Initialize bullets
        playerBullet = null;
        alienBullets = new ArrayList<>();
        
        // Initialize alien formation
        aliens = new Alien[ALIEN_ROWS][ALIENS_PER_ROW];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIENS_PER_ROW; col++) {
                aliens[row][col] = new Alien(col * ALIEN_SPACING_X + 50, row * ALIEN_SPACING_Y + 50);
            }
        }
        
        alienFormationX = 0;
        alienFormationY = 50;
        alienDirection = 1;
        alienFireCounter = 0;
        alienSpeed = 1.0f; // Start with base speed
        aliensDestroyed = 0;
        
        // Initialize shields (4 shields positioned between player and aliens)
        shields = new ArrayList<>();
        int shieldY = 450; // Position shields between player (bottom) and aliens (top)
        int shieldSpacing = BOARD_WIDTH / 5; // Space shields evenly across screen
        for (int i = 0; i < 4; i++) {
            int shieldX = shieldSpacing * (i + 1) - 25; // Center each shield
            shields.add(new Shield(shieldX, shieldY));
        }
    }
    
    /**
     * Move the player left (decreases playerX).
     */
    public void movePlayerLeft() {
        playerX = Math.max(0, playerX - 5);
    }
    
    /**
     * Move the player right (increases playerX).
     */
    public void movePlayerRight() {
        playerX = Math.min(BOARD_WIDTH - PLAYER_WIDTH, playerX + 5);
    }
    
    /**
     * Fire a player bullet if one isn't already in flight.
     */
    public void firePlayerBullet() {
        if (playerBullet == null) {
            playerBullet = new PlayerBullet(playerX + PLAYER_WIDTH / 2.0f, BOARD_HEIGHT - 50);
        }
    }
    
    /**
     * Updates the game logic for one frame.
     * Called repeatedly to update entity positions, handle collisions, etc.
     */
    public void update() {
        // Update player bullet position and remove if off-screen
        if (playerBullet != null) {
            playerBullet.update();
            if (playerBullet.getY() < 0) {
                playerBullet = null;
            }
        }
        
        // Update alien bullets
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            alienBullets.get(i).update();
            if (alienBullets.get(i).getY() > BOARD_HEIGHT) {
                alienBullets.remove(i);
            }
        }
        
        // Move alien formation
        updateAlienFormation();
        
        // Fire alien bullets at random intervals
        fireAlienBullets();
        
        // Check collisions
        checkCollisions();
    }
    
    /**
     * Update alien formation position and direction.
     * Moves right/left, then down and reverses when hitting the edge.
     */
    private void updateAlienFormation() {
        alienFormationX += alienDirection * alienSpeed;
        
        // Check if we need to move down and reverse direction
        boolean shouldReverse = false;
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIENS_PER_ROW; col++) {
                if (aliens[row][col] != null) {
                    float alienX = alienFormationX + col * ALIEN_SPACING_X;
                    if (alienX < 0 || alienX + ALIEN_WIDTH > BOARD_WIDTH) {
                        shouldReverse = true;
                        break;
                    }
                }
            }
            if (shouldReverse) break;
        }
        
        if (shouldReverse) {
            alienDirection *= -1;
            alienFormationY += 30;
        }
    }
    
    /**
     * Fire alien bullets at random intervals from random active aliens.
     */
    private void fireAlienBullets() {
        alienFireCounter++;
        // Fire roughly every 30 frames on average
        if (alienFireCounter > 30 && Math.random() < 0.3) {
            // Pick a random column
            int col = (int) (Math.random() * ALIENS_PER_ROW);
            
            // Find the lowest alive alien in that column
            for (int row = ALIEN_ROWS - 1; row >= 0; row--) {
                if (aliens[row][col] != null) {
                    float alienX = alienFormationX + col * ALIEN_SPACING_X + ALIEN_WIDTH / 2.0f;
                    float alienY = alienFormationY + row * ALIEN_SPACING_Y + ALIEN_HEIGHT;
                    alienBullets.add(new AlienBullet(alienX, alienY));
                    alienFireCounter = 0;
                    break;
                }
            }
        }
    }
    
    /**
     * Check and handle all collisions between bullets and entities.
     */
    private void checkCollisions() {
        // Check player bullet vs aliens
        if (playerBullet != null) {
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIENS_PER_ROW; col++) {
                    if (aliens[row][col] != null) {
                        float alienX = alienFormationX + col * ALIEN_SPACING_X;
                        float alienY = alienFormationY + row * ALIEN_SPACING_Y;
                        
                        if (playerBullet.collidesWith(alienX, alienY, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                            aliens[row][col] = null;
                            playerBullet = null;
                            score += 10;
                            aliensDestroyed++;
                            alienSpeed += 0.1f; // Increase speed slightly each time an alien is destroyed
                            return; // Exit early since bullet is destroyed
                        }
                    }
                }
            }
        }
        
        // Check alien bullets vs player
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            float playerY = BOARD_HEIGHT - PLAYER_HEIGHT - 10;
            if (alienBullets.get(i).collidesWith(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                alienBullets.remove(i);
                lives--;
            }
        }
        
        // Check player bullet vs shields
        if (playerBullet != null) {
            for (int i = shields.size() - 1; i >= 0; i--) {
                Shield shield = shields.get(i);
                if (playerBullet.collidesWith(shield.getX(), shield.getY(), shield.getWidth(), shield.getHeight())) {
                    shield.takeDamage();
                    playerBullet = null;
                    if (shield.getHealth() <= 0) {
                        shields.remove(i);
                    }
                    return; // Exit early since bullet is destroyed
                }
            }
        }
        
        // Check alien bullets vs shields
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            AlienBullet bullet = alienBullets.get(i);
            for (int j = shields.size() - 1; j >= 0; j--) {
                Shield shield = shields.get(j);
                if (bullet.collidesWith(shield.getX(), shield.getY(), shield.getWidth(), shield.getHeight())) {
                    shield.takeDamage();
                    alienBullets.remove(i);
                    if (shield.getHealth() <= 0) {
                        shields.remove(j);
                    }
                    break; // Bullet is destroyed, move to next bullet
                }
            }
        }
    }
    
    // Getter methods for the view
    
    public float getPlayerX() {
        return playerX;
    }
    
    public int getPlayerWidth() {
        return PLAYER_WIDTH;
    }
    
    public int getPlayerHeight() {
        return PLAYER_HEIGHT;
    }
    
    public int getBoardWidth() {
        return BOARD_WIDTH;
    }
    
    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }
    
    public int getLives() {
        return lives;
    }
    
    public int getScore() {
        return score;
    }
    
    public PlayerBullet getPlayerBullet() {
        return playerBullet;
    }
    
    public List<AlienBullet> getAlienBullets() {
        return alienBullets;
    }
    
    public Alien[][] getAliens() {
        return aliens;
    }
    
    public float getAlienFormationX() {
        return alienFormationX;
    }
    
    public float getAlienFormationY() {
        return alienFormationY;
    }
    
    public int getAlienWidth() {
        return ALIEN_WIDTH;
    }
    
    public int getAlienHeight() {
        return ALIEN_HEIGHT;
    }
    
    public int getAlienSpacingX() {
        return ALIEN_SPACING_X;
    }
    
    public int getAlienSpacingY() {
        return ALIEN_SPACING_Y;
    }
    
    public List<Shield> getShields() {
        return shields;
    }
    
    /**
     * Gets the recommended timer interval in milliseconds based on current alien speed.
     * As aliens get faster, the game should update more frequently for smoother gameplay.
     * 
     * @return recommended timer interval in milliseconds
     */
    public int getRecommendedTimerInterval() {
        // Base interval is 16ms (~60 FPS)
        // As speed increases, decrease interval but don't go below 8ms
        int baseInterval = 16;
        int speedReduction = (int) (alienSpeed * 2); // Reduce interval by 2ms per speed unit
        return Math.max(8, baseInterval - speedReduction);
    }
}

/**
 * Represents a player-fired bullet.
 */
class PlayerBullet {
    private float x;
    private float y;
    private static final float SPEED = 5; // Pixels per frame, moving up (negative Y direction)
    
    public PlayerBullet(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        y -= SPEED; // Move up
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public boolean collidesWith(float otherX, float otherY, float otherWidth, float otherHeight) {
        // Simple AABB collision detection
        return x > otherX && x < otherX + otherWidth && 
               y > otherY && y < otherY + otherHeight;
    }
}

/**
 * Represents an alien-fired bullet.
 */
class AlienBullet {
    private float x;
    private float y;
    private static final float SPEED = 3; // Pixels per frame, moving down (positive Y direction)
    
    public AlienBullet(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        y += SPEED; // Move down
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public boolean collidesWith(float otherX, float otherY, float otherWidth, float otherHeight) {
        // Simple AABB collision detection
        return x > otherX && x < otherX + otherWidth && 
               y > otherY && y < otherY + otherHeight;
    }
}

/**
 * Represents an alien enemy.
 */
class Alien {
    private float baseX;
    private float baseY;
    private boolean alive;
    
    public Alien(float x, float y) {
        this.baseX = x;
        this.baseY = y;
        this.alive = true;
    }
    
    public float getBaseX() {
        return baseX;
    }
    
    public float getBaseY() {
        return baseY;
    }
    
    public boolean isAlive() {
        return alive;
    }
}

/**
 * Represents a protective shield.
 */
class Shield {
    private static final int SHIELD_WIDTH = 50;
    private static final int SHIELD_HEIGHT = 20;
    private static final int MAX_HEALTH = 3;
    
    private float x;
    private float y;
    private int health;
    
    public Shield(float x, float y) {
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
    }
    
    public void takeDamage() {
        health--;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public int getWidth() {
        return SHIELD_WIDTH;
    }
    
    public int getHeight() {
        return SHIELD_HEIGHT;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}
