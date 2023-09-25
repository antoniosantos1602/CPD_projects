import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private static final int numberOfPlayers = 3;
    private static ScoreManager scoreManager = new ScoreManager();

    public static void main(String[] args) throws IOException {
        LinkedHashMap<String, Socket> playerList = new LinkedHashMap<>();
        LinkedHashMap<String, Socket> playerQueue = new LinkedHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        try {
            ServerSocket serverSocket = new ServerSocket(8015);
            new Thread(() -> {
                while(true){
                    try {
                        Socket socket;
                        try {
                            socket = serverSocket.accept();
                            System.out.println("A player connected!");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        String newPlayer = AuthenticationServer.authenticatePlayers(socket);
                        System.out.println(newPlayer);

                        synchronized(playerList){
                            playerList.put(newPlayer, socket);
                            playerList.notifyAll();
                        }
                        synchronized(playerQueue){
                            playerQueue.put(newPlayer,socket);
                            playerQueue.notifyAll();

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    }
            }).start();  // Don't forget to start the Thread.

            while(true){
                System.out.println(playerQueue.size());
                if (playerQueue.size() >= numberOfPlayers) {
                    LinkedHashMap<String, Socket> tempQueue = new LinkedHashMap<>(playerQueue);
                    String player1;
                    String player2;
                    String player3;

                    Iterator keyIterator= tempQueue.keySet().iterator();
                    player1 = (String)keyIterator.next();
                    player2 = (String)keyIterator.next();
                    player3 = (String)keyIterator.next();

                    executor.execute(() -> {
                        HashMap<String, Socket> currentPlayers= new HashMap<>();

                        currentPlayers.put(player1, playerList.get(player1));
                        currentPlayers.put(player2, playerList.get(player2));
                        currentPlayers.put(player3, playerList.get(player3));

                        synchronized (playerQueue) {
                            playerQueue.remove((player1));
                            playerQueue.remove((player2));
                            playerQueue.remove((player3));
                            playerQueue.notifyAll();
                        }

                        Game game = new Game(numberOfPlayers, currentPlayers, scoreManager);
                        game.start();
                    });
                }}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}