package Controller;

import Model.Ghost;
import Model.Player;
import Model.GameSnapshot;
import Model.Game;

import java.io.*;
import java.util.*;

// The GameHistory class manages the recording and playback of game snapshots for a game session
public class GameHistory {
    private List<GameSnapshot> gameSnapshots;
    private boolean recording;
    private int currentSnapshotIndex = -1;
    private final PlaybackMode playbackMode;
    private final int playbackStepDelay;
    private boolean playbackInProgress;


    // Enumeration for playback mode
    public enum PlaybackMode {
        CONTINUOUS,
        SINGLE_STEP,
    }

    // Constructor for the GameHistory class
    public GameHistory() {
        gameSnapshots = new ArrayList<>();
        recording = false;
        playbackMode = PlaybackMode.CONTINUOUS;
        playbackStepDelay = 500; // milliseconds
    }

    // Start recording game snapshots
    public void startRecording() {
        gameSnapshots.clear();
        recording = true;
    }

    // Stop recording game snapshots and save the game history to a file
    public void stopRecording() {
        recording = false;
        saveGame();
    }

    // Record a snapshot of the current game state
    public void recordSnapshot(Model.Map map, Player player, List<Ghost> ghosts) {
        if (recording) {
            GameSnapshot snapshot = new GameSnapshot(map, player, ghosts);
            gameSnapshots.add(snapshot);
            System.out.println("Snapshot recorded. Total snapshots: " + gameSnapshots.size());
        }
    }

    // Load game history from a file
    public void loadGame() {
        gameSnapshots.clear(); // Clear existing snapshots before loading
        loadHistoryFromFile();
        currentSnapshotIndex = 0; // Reset the snapshot index
    }

    // Go to the next snapshot in the game history
    public GameSnapshot goToNextSnapshot() {
        System.out.println("Current snapshot index: " + currentSnapshotIndex);
        if (currentSnapshotIndex + 1 < gameSnapshots.size()) {
            currentSnapshotIndex++;
            System.out.println("New snapshot index: " + currentSnapshotIndex);
            return gameSnapshots.get(currentSnapshotIndex);
        }
        return null;
    }

    // Go to the previous snapshot in the game history
    public GameSnapshot goToPreviousSnapshot() {
        if (currentSnapshotIndex > 0) {
            currentSnapshotIndex--;
            return gameSnapshots.get(currentSnapshotIndex);
        }
        return null;
    }

    // Get the current snapshot from the game history
    public GameSnapshot getCurrentSnapshot() {
        if (currentSnapshotIndex >= 0 && currentSnapshotIndex < gameSnapshots.size()) {
            return gameSnapshots.get(currentSnapshotIndex);
        }
        return null;
    }

    // Check if the game history is being recorded
    public boolean isRecording() {
        return recording;
    }

    private void saveGame() {
        // Save player movement
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/save_game.txt"))) {
            oos.writeObject(gameSnapshots);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Load game history from a file
    private void loadHistoryFromFile() {
        // Load game snapshots
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/save_game.txt"))) {
            gameSnapshots = (List<GameSnapshot>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Set the current snapshot index
    public void setCurrentSnapshotIndex(int index) {
        this.currentSnapshotIndex = index;
    }

    // Set the playback in progress flag
    public void setPlaybackInProgress(boolean playbackInProgress) {
        this.playbackInProgress = playbackInProgress;
    }

    // Check if the playback is in progress
    public boolean isPlaybackInProgress() {
        return playbackInProgress;
    }

    // Get an array of game snapshots
    public GameSnapshot[] getGameSnapshots() {
        return gameSnapshots.toArray(new GameSnapshot[0]);
    }

    // Get the current playback mode
    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    // Get the delay between steps in playback mode
    public int getPlaybackStepDelay() {
        return playbackStepDelay;
    }

}
