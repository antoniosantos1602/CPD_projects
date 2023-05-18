import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ScoreManager {
    private Map<String, Integer> scores;
    private String filename;

    public ScoreManager() {
        this.filename = "scores.txt";
        this.scores = new HashMap<>();
        loadScores();
    }

    public void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.put(name, score);
                } else {
                    System.out.println("ignoring line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing scores found.");
        }
    }

    public int getScore(String username) {
        return scores.getOrDefault(username, 0);
    }

    public void updateScore(String username, int points) {
        int score = getScore(username);
        score += points;
        scores.put(username, score);
    }

    public void saveScores() {
        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                out.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Failed to save scores.");
        }
    }
}