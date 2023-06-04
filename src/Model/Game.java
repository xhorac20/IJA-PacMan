package Model;

import Controller.GameHistory;
import Controller.GameHistory.PlaybackMode;
import Controller.Input;
import View.View;

import java.util.ArrayList;
import java.util.List;

import static App.PacManApp.findGhostStartPositions;
import static App.PacManApp.findPlayerStartPosition;

public class Game implements GameRestorer {
    private boolean playbackInProgress;
    private int stepCounter;
    private Map map;
    private Player player;
    private List<Ghost> ghosts;
    private View view;
    private final Input input;
    private boolean gameRunning;
    private final Object gameLock = new Object();

    private boolean gameJustReset;

    public Map getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }


    public void setView(View view) {
        this.view = view;
    }

    public boolean isRunning() {
        return gameRunning;
    }

    private final GameHistory gameHistory;

    public Game(Map map, Player player, List<Ghost> ghosts, View view, Input input, GameHistory gameHistory) {
        this.map = map;
        this.player = player;
        this.ghosts = ghosts;
        this.view = view;
        this.input = input;
        this.gameRunning = true;
        this.gameHistory = gameHistory;
        input.setGame(this);
    }

    public boolean isPlaybackInProgress() {
        return playbackInProgress;
    }

    public void setPlaybackInProgress(boolean playbackInProgress) {
        this.playbackInProgress = playbackInProgress;
    }

    public void start() {
        Thread gameThread = new Thread(this::run);
        gameThread.start();
    }

    private void run() {
        long lastTime = System.nanoTime();
        double delta = 0.0;
        double nsPerUpdate = 1000000000.0 / 15.0; // 15 FPS
        int ghostUpdateCounter = 0; // ghost update counter, ako casto sa duchovia maju pohnut
        int ghostUpdateDelay = 1; //  (higher value = slower ghosts)

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            synchronized (gameLock) {
                while (!gameRunning) {
                    try {
                        gameLock.wait();
                        lastTime = System.nanoTime(); // Update lastTime to current time when resuming the game
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            while (delta >= 1) {
                int previousPlayerX = player.getX();
                int previousPlayerY = player.getY();

                if (!playbackInProgress) {
                    // Update player
                    player.move(input.getDirection(), map);
                }

                // Update ghosts only if ghostUpdateCounter >= ghostUpdateDelay (ako casto sa maju duchovia pohnut)
                if (!playbackInProgress && ghostUpdateCounter >= ghostUpdateDelay) {
                    for (Ghost ghost : ghosts) {
                        ghost.updatePosition();
                    }
                    ghostUpdateCounter = 0;
                } else {
                    ghostUpdateCounter++;
                }

                // Check for collisions
                checkCollisions();
                delta--;

                if (player.getX() != previousPlayerX || player.getY() != previousPlayerY) {
                    stepCounter++;
                }

                if (gameHistory.isRecording()) {
                    gameHistory.recordSnapshot(map, player, ghosts);
                }

                if (gameHistory.getPlaybackMode() == PlaybackMode.SINGLE_STEP) {
                    try {
                        Thread.sleep(gameHistory.getPlaybackStepDelay());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            view.drawMap(map);
            view.updateDisplay(player, ghosts);

            try {
                Thread.sleep(1000 / 15); // zastavenie vlakna na 15 frame/s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetGame(String fileName) {
        // Reset stepCounter
        stepCounter = 0;

        // Stop the game
        gameRunning = false;

        // Create a new Map instance
        var newMap = new Map(fileName);

        // Create a new Player instance
        var playerStartPosition = findPlayerStartPosition(newMap.getMap());
        var newPlayer = new Player(playerStartPosition[0], playerStartPosition[1], newMap);

        // Create a new list of Ghost instances
        List<Ghost> newGhosts = new ArrayList<>();
        List<int[]> ghostPositions = findGhostStartPositions(newMap.getMap());
        for (int[] pos : ghostPositions) {
            newGhosts.add(new Ghost(pos[0], pos[1], newMap));
        }

        // Replace the existing map, player, and ghosts with the new instances
        this.map = newMap;
        this.player = newPlayer;
        this.ghosts = newGhosts;


        // Set gameJustReset to true
        gameJustReset = true;
    }


    public void toggleRunning() {
        synchronized (gameLock) {
            gameRunning = !gameRunning;
            if (gameRunning) {
                gameLock.notify();
                gameJustReset = false;
            }
        }
    }

    // Kolizie ducha
    private void checkCollisions() {
        for (Ghost ghost : ghosts) {
            // Check for collision with the ghost
            if (player.getX() == ghost.getX() && player.getY() == ghost.getY()) {
                player.die();
                gameRunning = false;
                break;
            }
        }

        // Check for picking up the key
        if (map.getMap()[player.getY()][player.getX()] == 'K') {
            player.pickUpKey();
            map.getMap()[player.getY()][player.getX()] = '.'; // Remove the key from the map
        }

        // Check for reaching the target
        if (player.hasKey() && map.getMap()[player.getY()][player.getX()] == 'T') {
            gameRunning = false;
        }
    }

    public boolean gameJustReset() {
        return gameJustReset;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    public void applySnapshot(GameSnapshot snapshot) {
        this.map = snapshot.getMapCopy();
        this.player = snapshot.getPlayerCopy();
        this.ghosts = snapshot.getGhostListCopy();
    }

}
