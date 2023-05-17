
import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

    private static final int numberOfPlayers = 3; // Set the number
    
    private static final int timeout = 30000; // 30 seconds
    
    private static final Queue<String> waitingPlayers = new LinkedList<>(); // fila de jogadores

    private static final Map<String, Socket> usernameToSocketMap = new HashMap<>(); // Mapa de associação entre nome de usuário e socket


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




