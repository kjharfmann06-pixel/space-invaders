public class TicTacToeModel {
    private char[][] board;
    private char currentPlayer;
    private boolean gameOver;
    private char winner;

    public TicTacToeModel() {
        board = new char[3][3];
        currentPlayer = 'X';
        gameOver = false;
        winner = ' ';
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public char getWinner() {
        return winner;
    }

    public boolean makeMove(int row, int col) {
        // Validate position
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }

        // Check if position is already occupied
        if (board[row][col] != ' ') {
            return false;
        }

        // Place the move
        board[row][col] = currentPlayer;

        // Check for win
        if (checkWin()) {
            gameOver = true;
            winner = currentPlayer;
            return true;
        }

        // Check for draw
        if (checkDraw()) {
            gameOver = true;
            winner = ' '; // Space indicates a draw
            return true;
        }

        // Switch player
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        return true;
    }

    private boolean checkWin() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && 
                board[i][1] == currentPlayer && 
                board[i][2] == currentPlayer) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == currentPlayer && 
                board[1][j] == currentPlayer && 
                board[2][j] == currentPlayer) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][2] == currentPlayer) {
            return true;
        }

        if (board[0][2] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][0] == currentPlayer) {
            return true;
        }

        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public void resetGame() {
        initializeBoard();
        currentPlayer = 'X';
        gameOver = false;
        winner = ' ';
    }
}
