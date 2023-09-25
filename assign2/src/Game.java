import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

public class Game {
    private int numberOfPlayers;
    private Map<String, Socket> userSockets;
    private int targetNumber;
    private ScoreManager scoreManager;

    public Game(int players, Map<String, Socket> userSockets, ScoreManager scoreManager) {
        this.numberOfPlayers = players;
        this.userSockets = userSockets;
        this.scoreManager = scoreManager;
    }

    public void start() {
        try {
            // Generate random number between 0 and 1000
            this.targetNumber = new Random().nextInt(1001);

            System.out.println("Starting game with " + numberOfPlayers + " players.");

            // Play the game
            boolean[] hasGuessed = new boolean[numberOfPlayers];
            int numberOfGuesses = 0;
            int[] guesses = new int[numberOfPlayers];
            while (numberOfGuesses < numberOfPlayers) {
                // Read guesses from clients
                int playerIndex = 0;
                for (Map.Entry<String, Socket> entry : userSockets.entrySet()) {
                    String username = entry.getKey();
                    Socket socket = entry.getValue();
                    if (!hasGuessed[playerIndex]) {
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.writeUTF("Enter your guess: ");
                        try {
                            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                            int guess = Integer.parseInt(inputStream.readUTF());
                            System.out.println("Player " + username + " guessed " + guess);
                            guesses[playerIndex] = guess;
                            hasGuessed[playerIndex] = true;
                            numberOfGuesses++;
                        } catch (IOException e) {
                            System.err.println("Failed to receive guess from player " + username + ".");
                        }
                    }
                    playerIndex++;
                }
            }

            // Determine the winner
            int closestGuess = Integer.MAX_VALUE;
            int winnerIndex = -1;
            for (int i = 0; i < numberOfPlayers; i++) {
                int distance = Math.abs(guesses[i] - targetNumber);
                if (distance < closestGuess) {
                    closestGuess = distance;
                    winnerIndex = i;
                }
            }

            // Send result to all clients and update scores
            int playerIndex = 0;
            for (Map.Entry<String, Socket> entry : userSockets.entrySet()) {
                String username = entry.getKey();
                Socket socket = entry.getValue();
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    int distance = Math.abs(guesses[playerIndex] - targetNumber);
                    if (playerIndex == winnerIndex) {
                        outputStream.writeUTF("Congratulations, you won! The target number was " + targetNumber + " and you failed by " + distance);
                        scoreManager.updateScore(username, 5);
                    } else {
                        outputStream.writeUTF("Sorry, you lost. The target number was " + targetNumber + " and your guess was " + guesses[playerIndex]);
                        scoreManager.updateScore(username, 0);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to send game result to player " + username + ".");
                }
                playerIndex++;
            }

            // Save scores to file
            scoreManager.saveScores();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close all sockets
            for (Socket socket : userSockets.values()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}