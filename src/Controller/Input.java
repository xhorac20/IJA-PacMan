package Controller;

import Model.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// The Input class handles keyboard inputs from the user and updates the player's movement direction accordingly
public class Input implements KeyListener {
    private int direction; // The player's movement direction (0 = up, 1 = right, 2 = down, 3 = left, -1 = no direction)
    private Game game; // Reference to the Game instance

    // Constructor for the Input class
    public Input() {
        direction = -1; // Initialize the direction to -1 (no direction)
    }

    // Set the reference to the Game instance
    public void setGame(Game game) {
        this.game = game;
    }

    // Get the player's movement direction
    public int getDirection() {
        return direction;
    }

    // Handle key press events
    public void keyPressed(KeyEvent e) {
        if (!game.isPlaybackInProgress()) { // Only handle input if the game is not in playback mode
            int key = e.getKeyCode(); // Get the key code of the pressed key

            // Set the direction according to the pressed key
            switch (key) {
                case KeyEvent.VK_UP -> direction = 0; // Up
                case KeyEvent.VK_RIGHT -> direction = 1; // Right
                case KeyEvent.VK_DOWN -> direction = 2; // Down
                case KeyEvent.VK_LEFT -> direction = 3; // Left
                default -> {
                }
            }
        }
    }

    // Handle key release events
    public void keyReleased(KeyEvent e) {
        direction = -1; // Reset the direction to -1 (no direction) when a key is released
    }

    // Handle key typed events
    public void keyTyped(KeyEvent e) {
    }
}