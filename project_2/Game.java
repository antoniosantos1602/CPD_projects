import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
    private List<Socket> userSockets;
    private int numberOfPlayers;
    private int targetNumber;
    private ScoreManager scoreManager;

    public Game(int players, List<Socket> userSockets, ScoreManager scoreManager) {
        this.numberOfPlayers = players;
        this.userSockets = userSockets;
        this.scoreManager = scoreManager;
    }

    public void start() {
        try {
            // Generate random number between 0 and 1000
            this.targetNumber = new Random().nextInt(1001);

            System.out.println("Starting game with " + numberOfPlayers + " players.");

            // Create input and output streams for each client socket
            List<DataInputStream> inputStreams = new ArrayList<>();
            List<DataOutputStream> outputStreams = new ArrayList<>();
            for (Socket socket : userSockets) {
                inputStreams.add(new DataInputStream(socket.getInputStream()));
                outputStreams.add(new DataOutputStream(socket.getOutputStream()));
            }

            // Play the game
            int[] guesses = new int[numberOfPlayers];
            boolean[] hasGuessed = new boolean[numberOfPlayers];
            int numberOfGuesses = 0;
            while (numberOfGuesses < numberOfPlayers) {
                // Send guess prompt to all clients
                for (DataOutputStream outputStream : outputStreams) {
                    outputStream.writeUTF("Enter your guess: ");
                }

                // Read guesses from clients
                for (int i = 0; i < numberOfPlayers; i++) {
                    if (!hasGuessed[i]) {
                        int guess;
                        try {
                            guess = Integer.parseInt(inputStreams.get(i).readUTF());
                            System.out.println("Player " + (i + 1) + " guessed " + guess);
                            int distance = Math.abs(guess - targetNumber);
                            System.out.println("Distance from target: " + distance);
                        } catch (IOException e) {
                            System.err.println("Failed to receive guess from player " + (i + 1) + ".");
                            hasGuessed[i] = true;
                            numberOfGuesses++;
                            continue;
                        }
                        guesses[i] = guess;
                        hasGuessed[i] = true;
                        numberOfGuesses++;
                    }
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
            for (int i = 0; i < numberOfPlayers; i++) {
                try {
                    int distance = Math.abs(guesses[i] - targetNumber);
                    if (i == winnerIndex) {
                        outputStreams.get(i).writeUTF("Congratulations, you won! The target number was " + targetNumber + " and you failed by " + distance);
                        scoreManager.updateScore(String.valueOf(i), 5);
                    } else if (distance < closestGuess) {
                        outputStreams.get(i).writeUTF("You were close! The target number was " + targetNumber + " and you failed by " + distance);
                        scoreManager.updateScore(String.valueOf(i), 1);
                    } else {
                        outputStreams.get(i).writeUTF("Sorry, you lost. The target number was " + targetNumber + " and your guess was " + guesses[i]);
                        scoreManager.updateScore(String.valueOf(i), 0);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to send game result to player " + (i + 1) + ".");
                }
            }

            // Save scores to file
            scoreManager.saveScores();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close all sockets
            for (Socket socket : userSockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}