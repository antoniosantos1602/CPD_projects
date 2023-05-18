import java.io.*;
import java.net.*;
import java.util.*;

public class AuthenticationServer {

    private static Map<String, String> userPasswords = new HashMap<>();

    public static Map<String, Socket> authenticatePlayers(int numberOfPlayers, int timeout) {
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

        Map<String, Socket> userSockets = new HashMap<>();

        try {
            ServerSocket serverSocket = new ServerSocket(8055);

            // Wait for all players to connect for 30 seconds
            System.out.println("Waiting for players to connect...");
            serverSocket.setSoTimeout(timeout); // set timeout for 30 seconds
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("A player connected!");

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
                        userSockets.put(username, socket); // Add to map

                        // Save username and password to file
                        try (FileWriter fw = new FileWriter("userPasswords.txt", true);
                             BufferedWriter bw = new BufferedWriter(fw);
                             PrintWriter out1 = new PrintWriter(bw)) {
                            out1.println(username + ":" + password);
                        } catch (IOException e) {
                            System.out.println("Failed to save user password.");
                        }

                        out.writeUTF("Registration successful, waiting for other players...");
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

                        userSockets.put(username, socket); // Add to map

                        out.writeUTF("Login successful, waiting for other players...");
                    } else {
                        out.writeUTF("Invalid choice. Connection will be closed.");
                        socket.close();
                    }

                    if (userSockets.size() >= numberOfPlayers) {
                        break; // Break when we have enough players
                    }
                } catch (SocketTimeoutException e) {
                    break; // timeout reached, exit loop
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userSockets;
    }
}