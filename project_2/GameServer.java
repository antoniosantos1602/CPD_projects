import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

    private static Map<String, String> userPasswords = new HashMap<>();

    public static void main(String[] args) {
        // Load user passwords from file
        try (BufferedReader reader = new BufferedReader(new FileReader("userPasswords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    String name = parts[0];
                    String password = parts[1];
                    userPasswords.put(name, password);
                } else {
                    System.out.println("ignoring line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing user passwords found.");
        }

        try {
            ServerSocket serverSocket = new ServerSocket(8800);

            // Wait for all players to connect for 30 seconds
            List<Socket> userSockets = new ArrayList<>();
            System.out.println("Waiting for players to connect...");
            serverSocket.setSoTimeout(30000); // set timeout for 30 seconds
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    userSockets.add(socket);
                    System.out.println("Player " + (userSockets.size()) + " connected!");

                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    // Ask for login or register
                    out.writeUTF("Please type 'login' or 'register':");
                    String choice = in.readUTF();

                    if ("register".equals(choice)) {
                        out.writeUTF("Please type a new username:");
                        String username = in.readUTF();
                        out.writeUTF("Please type a new password:");
                        String password = in.readUTF();

                        if (userPasswords.containsKey(username)) {
                            out.writeUTF("Username already exists. Connection will be closed.");
                            socket.close();
                            continue;
                        }

                        userPasswords.put(username, password);
                        // Save username and password to file
                        try (FileWriter fw = new FileWriter("userPasswords.txt", true);
                             BufferedWriter bw = new BufferedWriter(fw);
                             PrintWriter out1 = new PrintWriter(bw)) {
                            out1.println(username + ":" + password);
                        } catch (IOException e) {
                            System.out.println("Failed to save user password.");
                        }

                        out.writeUTF("Registration successful, you can now play the game.");
                    } else if ("login".equals(choice)) {
                        out.writeUTF("Please type your username:");
                        String username = in.readUTF();
                        out.writeUTF("Please type your password:");
                        String password = in.readUTF();

                        String correctPassword = userPasswords.get(username);
                        if (correctPassword == null || !correctPassword.equals(password)) {
                            out.writeUTF("Incorrect username or password. Connection will be closed.");
                            socket.close();
                            continue;
                        }

                        out.writeUTF("Login successful, you can now play the game.");
                    } else {
                        out.writeUTF("Invalid choice. Connection will be closed.");
                        socket.close();
                    }
                } catch (SocketTimeoutException e) {
                    break; // timeout reached, exit loop
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<List<Socket>> divideIntoTeams(List<Socket> userSockets) {
        List<List<Socket>> teams = new ArrayList<>();
        for (int i = 0; i < userSockets.size(); i += 2) {
            List<Socket> team = new ArrayList<>();
            team.add(userSockets.get(i));
            if (i + 1 < userSockets.size()) {
                team.add(userSockets.get(i + 1));
            }
            teams.add(team);
        }
        return teams;
    }
    private static class GameThread implements Runnable {
        private List<Socket> team;

        public GameThread(List<Socket> team) {
            this.team = team;
        }

        @Override
        public void run() {
            Game game = new Game(team.size(), team);
            game.start();
        }
    }
}