import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            System.out.println("Connected to server.");

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // Handle login or register
            System.out.println(inputStream.readUTF());
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();
            outputStream.writeUTF(choice);

            if ("register".equals(choice)) {
                System.out.println(inputStream.readUTF());
                String username = scanner.nextLine();
                outputStream.writeUTF(username);

                System.out.println(inputStream.readUTF());
                String password = scanner.nextLine();
                outputStream.writeUTF(password);

                String response = inputStream.readUTF();
                System.out.println(response);
                if (response.startsWith("Username already exists")) {
                    return;
                }
            } else if ("login".equals(choice)) {
                System.out.println(inputStream.readUTF());
                String username = scanner.nextLine();
                outputStream.writeUTF(username);

                System.out.println(inputStream.readUTF());
                String password = scanner.nextLine();
                outputStream.writeUTF(password);

                String response = inputStream.readUTF();
                System.out.println(response);
                if (response.startsWith("Incorrect username or password")) {
                    return;
                }

            } else {
                System.out.println(inputStream.readUTF());
                return;
            }

            // Play the game
            while (true) {
                // Get the guess prompt
                System.out.println(inputStream.readUTF());

                // Get the guess from the user
                String guess = scanner.nextLine();
                outputStream.writeUTF(guess);

                // Get the result
                String result = inputStream.readUTF();
                System.out.println(result);

                // Check if the user wants to logout
                System.out.println("Press Enter to continue playing or type 'logout' to exit:");
                String input = scanner.nextLine();
                if ("logout".equals(input)) {
                    outputStream.writeUTF(input);
                    break;
                }
            }

        } catch (EOFException e) {
            System.out.println("Connection to server lost.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}