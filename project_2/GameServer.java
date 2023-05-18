import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

public class GameServer {

    private static final int numberOfPlayers = 3;
    private static final int timeout = 30000;
    private static final Queue<String> waitingPlayers = new LinkedList<>();
    private static final Map<String, Socket> usernameToSocketMap = new HashMap<>();
    private static ScoreManager scoreManager = new ScoreManager();

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                Map<String, Socket> newPlayers = AuthenticationServer.authenticatePlayers(numberOfPlayers, timeout);
                usernameToSocketMap.putAll(newPlayers);
                waitingPlayers.addAll(newPlayers.keySet());

                if (waitingPlayers.size() >= numberOfPlayers) {
                    List<Socket> currentPlayers = new ArrayList<>();
                    for (int i = 0; i < numberOfPlayers; i++) {
                        String nextPlayer = waitingPlayers.poll();
                        Socket nextPlayerSocket = usernameToSocketMap.get(nextPlayer);
                        currentPlayers.add(nextPlayerSocket);
                    }
                    new Thread(new GameThread(currentPlayers)).start();
                }
            }
        }).start();
    }

    private static class GameThread implements Runnable {
        private List<Socket> team;

        public GameThread(List<Socket> team) {
            this.team = team;
        }

        @Override
        public void run() {
            Game game = new Game(team.size(), team, scoreManager);
            game.start();
        }
    }
}