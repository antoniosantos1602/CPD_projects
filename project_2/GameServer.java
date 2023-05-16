import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {

    private static final int numberOfPlayers = 3; // Set the number
    private static final int timeout = 30000; // 30 seconds

    public static void main(String[] args) {
        List<Socket> userSockets = AuthenticationServer.authenticatePlayers(numberOfPlayers, timeout);

        // Start the game after successful authentication
        if (userSockets.size() >= numberOfPlayers) {
            new Thread(new GameThread(new ArrayList<>(userSockets))).start();
            userSockets.clear();  // Clear the list for the next batch of players
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
