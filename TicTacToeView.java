public class TicTacToeView {
    
    public void displayBoard(char[][] board) {
        System.out.println("\n");
        System.out.println("     0   1   2");
        System.out.println("   +---+---+---+");
        
        for (int i = 0; i < 3; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + board[i][j] + " |");
            }
            System.out.println();
            System.out.println("   +---+---+---+");
        }
        System.out.println();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayGameStart() {
        System.out.println("========================================");
        System.out.println("    Welcome to Java Tic-Tac-Toe!");
        System.out.println("========================================");
        System.out.println("\nPlayer X goes first.");
        System.out.println("Enter moves as: row column (0-2 for each)");
        System.out.println("Example: 0 0 (top-left corner)\n");
    }

    public void displayCurrentPlayer(char player) {
        System.out.println("Player " + player + "'s turn");
    }

    public void displayInvalidMove() {
        System.out.println("Invalid move! Either position is out of bounds or already occupied. Try again.");
    }

    public void displayWinner(char winner) {
        System.out.println("========================================");
        System.out.println("Player " + winner + " wins!");
        System.out.println("========================================");
    }

    public void displayDraw() {
        System.out.println("========================================");
        System.out.println("It's a draw!");
        System.out.println("========================================");
    }

    public void displayPlayAgain() {
        System.out.println("\nDo you want to play again? (yes/no): ");
    }

    public void displayGameEnd() {
        System.out.println("\nThanks for playing!");
    }
}
