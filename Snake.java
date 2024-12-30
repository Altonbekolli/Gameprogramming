import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class Snake extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;  // Größe jedes Snake-Segments
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int BOARD_WIDTH = WIDTH * TILE_SIZE;
    private final int BOARD_HEIGHT = HEIGHT * TILE_SIZE;
    private final int DELAY = 200;

    private List<Point> snake;
    private Point food;
    private List<Point> obstacles = new ArrayList<>();

    private int score;
    private Timer timer;
    private boolean running;
    private boolean gameOver;
    private String playerName;
    private List<ScoreEntry> highScores = new ArrayList<>();

    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction direction;

    public Snake() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new TAdapter());

        loadHighScores();
        initializeGame();
    }

    private void initializeGame() {
        snake = new ArrayList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = Direction.RIGHT;
        spawnFood();
        spawnObstacles(3); // Hindernisse nur für "Mittel" und "Schwer" hinzugefügt
        score = 0;
        gameOver = false;
        running = true;

        if (timer == null) {
            timer = new Timer(DELAY, this);
        } else {
            timer.stop();
        }
        timer.start();
    }

    private void spawnFood() {
        Random rand = new Random();
        do {
            food = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        } while (snake.contains(food) || obstacles.contains(food));  // Nur innerhalb der Spielfeldgrenzen und ohne Hindernisse
    }

    private void spawnObstacles(int count) {
        obstacles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Point obstacle;
            do {
                obstacle = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
            } while (snake.contains(obstacle) || food.equals(obstacle));
            obstacles.add(obstacle);
        }
    }

    private void move() {
        if (!running) return;

        Point head = new Point(snake.get(0));
        switch (direction) {
            case UP -> head.y--;
            case DOWN -> head.y++;
            case LEFT -> head.x--;
            case RIGHT -> head.x++;
        }

        // Überprüfen, ob die Schlange die Spielfeldgrenzen überschreitet
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT || snake.contains(head) || obstacles.contains(head)) {
            gameOver = true;
            running = false;
            timer.stop();
            updateHighScores();
            return;
        }

        // Bewegen und Essen sammeln
        snake.add(0, head);
        if (head.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void updateHighScores() {
        if (playerName != null) {
            highScores.add(new ScoreEntry(playerName, score));
            Collections.sort(highScores);
            if (highScores.size() > 3) {
                highScores = highScores.subList(0, 3);
            }
            saveHighScores();
        }
    }

    private void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {  // Überprüfen, ob die Zeile das richtige Format hat
                    String name = parts[0].trim();
                    int score;
                    try {
                        score = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Ungültiges Format für Score: " + parts[1]);
                        continue;  // Diese Zeile überspringen, falls der Score kein Integer ist
                    }
                    highScores.add(new ScoreEntry(name, score));
                } else {
                    System.out.println("Ungültige Zeile in highscore.txt: " + line);
                }
            }
            // Sortiere und begrenze die Liste auf die Top 3
            Collections.sort(highScores, Comparator.comparingInt(e -> -e.score));
            while (highScores.size() > 3) highScores.remove(3);
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Highscores: " + e.getMessage());
        }
    }


    private void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscores.txt"))) {
            for (ScoreEntry entry : highScores) {
                writer.write(entry.name + ":" + entry.score);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Schlange zeichnen
        g.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            if (i == 0) {  // Kopf der Schlange
                g.setColor(Color.WHITE);
                g.fillOval(p.x * TILE_SIZE + 5, p.y * TILE_SIZE + 5, 5, 5);  // Auge 1
                g.fillOval(p.x * TILE_SIZE + 15, p.y * TILE_SIZE + 5, 5, 5);  // Auge 2
                g.setColor(Color.GREEN);
            }
        }

        // Essen zeichnen
        g.setColor(Color.ORANGE);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Hindernisse zeichnen
        g.setColor(Color.GRAY);
        for (Point obstacle : obstacles) {
            g.fillRect(obstacle.x * TILE_SIZE, obstacle.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (gameOver) {
            showGameOverScreen(g);
        }
    }

    private void showGameOverScreen(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Game Over!", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 2 - 20);
        g.drawString("Score: " + score, BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 2);
        g.drawString("High Scores:", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 2 + 40);
        for (int i = 0; i < highScores.size(); i++) {
            ScoreEntry entry = highScores.get(i);
            g.drawString((i + 1) + ". " + entry.name + ": " + entry.score, BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 2 + 60 + (i * 20));
        }
        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(BOARD_WIDTH / 2 - 30, BOARD_HEIGHT / 2 + 100, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
        repaint();
    }

    private void restartGame() {
        removeAll();
        initializeGame();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            repaint();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if ((key == KeyEvent.VK_LEFT) && (direction != Direction.RIGHT)) direction = Direction.LEFT;
            else if ((key == KeyEvent.VK_RIGHT) && (direction != Direction.LEFT)) direction = Direction.RIGHT;
            else if ((key == KeyEvent.VK_UP) && (direction != Direction.DOWN)) direction = Direction.UP;
            else if ((key == KeyEvent.VK_DOWN) && (direction != Direction.UP)) direction = Direction.DOWN;
        }
    }

    private static class ScoreEntry implements Comparable<ScoreEntry> {
        String name;
        int score;

        ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry o) {
            return Integer.compare(o.score, this.score);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            Snake game = new Snake();
            frame.add(game);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
