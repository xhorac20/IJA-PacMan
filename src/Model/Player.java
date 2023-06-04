package Model;

import App.PacManApp;

import java.io.Serializable;

// The Player class represents a player in the Pac-Man application
public class Player implements Serializable {
    private int x;
    private int y;
    private boolean hasKey;

    // A player constructor with its initial coordinates and a reference to the game map
    public Player(int x, int y, Map map) {
        this.x = x;
        this.y = y;
        this.hasKey = false;

        // Setting the initial position to an empty field
        map.getMap()[y][x] = '.';
    }

    // Constructor Copy
    public Player(Player otherPlayer) {
        this.x = otherPlayer.x;
        this.y = otherPlayer.y;
        this.hasKey = otherPlayer.hasKey;
    }

    // Getters and setters for player coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Getters and setters for player key state
    public boolean hasKey() {
        return hasKey;
    }

    // A method for reducing the number of player lives
    public void die() {
    }

    // Method for the player to pick up the key
    public void pickUpKey() {
        hasKey = true;
    }

    // Method for moving the player in direction (0 = up, 1 = right, 2 = down, 3 = left)
    public void move(int direction, Map map) {
        int newX = x;
        int newY = y;

        switch (direction) {
            case 0 -> newY--; // Up
            case 1 -> newX++; // Right
            case 2 -> newY++; // Down
            case 3 -> newX--; // Left
            default -> {
            } // In the case of an invalid direction, nothing happens
        }

        // Checking the collision with the map and possible movement of the player
        if (!PacManApp.checkCollision(this, map, newX, newY)) {
            x = newX;
            y = newY;
        }
    }
}
