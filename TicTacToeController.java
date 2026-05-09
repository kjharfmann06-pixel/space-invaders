import java.util.Scanner;

public class TicTacToeController {
    private TicTacToeModel model;
    private TicTacToeView view;
    private Scanner scanner;

    public TicTacToeController(TicTacToeModel model, TicTacToeView view) {
        this.model = model;
        this.view = view;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        view.displayGameStart();

        boolean playAgain = true;
        while (playAgain) {
            playGame();
            playAgain = askPlayAgain();
            if (playAgain) {
                model.resetGame();
            }
        }

        view.displayGameEnd();
        scanner.close();
    }

    private void playGame() {
        while (!model.isGameOver()) {
            view.displayBoard(model.getBoard());
            view.displayCurrentPlayer(model.getCurrentPlayer());
            
            boolean validMove = false;
            while (!validMove) {
                validMove = getUserMoveAndApply();
            }
        }

        // Display final board and result
        view.displayBoard(model.getBoard());
        if (model.getWinner() == ' ') {
            view.displayDraw();
        } else {
            view.displayWinner(model.getWinner());
        }
    }

    private boolean getUserMoveAndApply() {
        System.out.print("Enter your move (row column): ");
        
        try {
            if (!scanner.hasNextInt()) {
                scanner.nextLine(); // Clear invalid input
                view.displayInvalidMove();
                return false;
            }

            int row = scanner.nextInt();
            
            if (!scanner.hasNextInt()) {
                scanner.nextLine(); // Clear invalid input
                view.displayInvalidMove();
                return false;
            }

            int col = scanner.nextInt();
            scanner.nextLine(); // Clear the newline

            if (model.makeMove(row, col)) {
                return true;
            } else {
                view.displayInvalidMove();
                return false;
            }
        } catch (Exception e) {
            scanner.nextLine(); // Clear any remaining input
            view.displayInvalidMove();
            return false;
        }
    }

    private boolean askPlayAgain() {
        while (true) {
            view.displayPlayAgain();
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("yes") || response.equals("y")) {
                return true;
            } else if (response.equals("no") || response.equals("n")) {
                return false;
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        }
    }
    public static void main(String[] args) {
    TicTacToeModel model = new TicTacToeModel();
    TicTacToeView view = new TicTacToeView();
    TicTacToeController controller = new TicTacToeController(model, view);
    controller.startGame();
}
}
