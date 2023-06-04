package Model;

import java.util.Random;
import java.io.Serializable;

// The Ghost class represents the ghost in the game and is serializable
public class Ghost implements Serializable {
    private int x;
    private int y;
    private final Map map;
    private final Random random;

    // Constructor for initializing the ghost at the given position on the map
    public Ghost(int x, int y, Map map) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.random = new Random();

        // Remove the ghost from the map at the current position
        map.getMap()[y][x] = '.';
    }

    // Constructor Copy
    public Ghost(Ghost otherGhost) {
        this.x = otherGhost.x;
        this.y = otherGhost.y;
        this.map = new Map(otherGhost.map);
        this.random = new Random();
    }


    // Getter and setter for the ghost's X coordinate
    public int getX() {
        return x;
    }

    // Getter and setter for the ghost's Y coordinate
    public int getY() {
        return y;
    }

    // Method to update ghost position based on random direction
    public void updatePosition() {
        int newX = x;
        int newY = y;

        int direction = random.nextInt(4);
        switch (direction) {
            case 0 -> // Up
                    newY--;
            case 1 -> // Down
                    newY++;
            case 2 -> // Left
                    newX--;
            case 3 -> // Right
                    newX++;
        }

        // If the move is valid, update the ghost's position
        if (isValidMove(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    // Method for verifying the validity of the move based on the X and Y coordinates
    private boolean isValidMove(int x, int y) {
        // Check if the position is within the map
        if (x < 0 || x >= map.getNumberOfColumns() || y < 0 || y >= map.getNumberOfRows()) {
            return false;
        }

        // Check if the position is not blocked by a wall
        char position = map.getMap()[y][x];
        return position != 'X';
    }
}
