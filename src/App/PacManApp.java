package App;

import Controller.GameHistory;
import Controller.Input;
import View.View;
import Model.Game;
import Model.Ghost;
import Model.Map;
import Model.Player;


import java.util.ArrayList;
import java.util.List;

public class PacManApp {
    public static void main(String[] args) {
        // Create instances of Model, View, and Controller classes
        Map map = new Map("data/mapa01.txt"); // Create a new Map instance with the specified file path
        int[] playerStartPosition = findPlayerStartPosition(map.getMap()); // Find the player's starting position on the map
        Player player = new Player(playerStartPosition[0], playerStartPosition[1], map); // Create a new Player instance with the found position and map
        List<Ghost> ghosts = new ArrayList<>(); // Create an empty list for Ghost instances
        List<int[]> ghostPositions = findGhostStartPositions(map.getMap()); // Find the starting positions of ghosts on the map
        for (int[] pos : ghostPositions) { // Iterate through the found ghost positions
            ghosts.add(new Ghost(pos[0], pos[1], map)); // Create a new Ghost instance with the found position and map, and add it to the list
        }

        GameHistory gameHistory = new GameHistory(); // Create a new GameHistory instance for recording and playing back game snapshots

        Input input = new Input(); // Create a new Input instance for handling user input

        // Create the Game instance
        Game game = new Game(map, player, ghosts, null, input, gameHistory); // Create a new Game instance with the specified parameters

        // Create the View instance
        View view = new View(game, gameHistory); // Create a new View instance with the specified parameters
        game.setView(view); // Set the view for the game instance
        view.addKeyListener(input); // Add the input object as a key listener to the view
        view.setFocusable(true); // Make the view focusable to receive key events

        // Start the game
        game.start(); // Start the game loop in a new thread

    }

    // Find the starting positions of ghosts on the map
    public static List<int[]> findGhostStartPositions(char[][] map) {
        List<int[]> positions = new ArrayList<>(); // Initialize an empty list of positions
        for (int i = 0; i < map.length; i++) { // Iterate through the rows of the map
            for (int j = 0; j < map[i].length; j++) { // Iterate through the columns of the map
                if (map[i][j] == 'G') { // Check if the current position has a ghost
                    positions.add(new int[]{j, i}); // Add the found ghost position to the list
                }
            }
        }
        if (positions.isEmpty()) { // If no ghost positions are found
            throw new RuntimeException("No ghost start positions found in the map");
        }
        return positions;
    }

    // Find the player's starting position on the map
    public static int[] findPlayerStartPosition(char[][] mapData) {
        for (int row = 0; row < mapData.length; row++) { // Iterate through the rows of the map
            for (int col = 0; col < mapData[row].length; col++) { // Iterate through the columns of the map
                if (mapData[row][col] == 'S') { // Check if the current position is the player's starting position
                    return new int[]{col, row}; // Return the found player starting position as an array of two integers (column, row)
                }
            }
        }
        System.out.println("Map data:");
        for (char[] mapDatum : mapData) { // Iterate through the rows of the map
            for (char c : mapDatum) { // Iterate through the columns of the map
                System.out.print(c); // Print the current map tile
            }
            System.out.println(); // Move to the next line after printing a row
        }
        throw new RuntimeException("Player start position not found in the map"); // Throw an exception if the player's start position is not found
    }

    // Check for collisions between the player and other objects on the map
    public static boolean checkCollision(Player player, Map map, int newX, int newY) {
        char tile = map.getMap()[newY][newX]; // Get the map tile at the specified new position (newX, newY)

        // Check for wall collision
        if (tile == 'X') { // If the tile is a wall
            return true; // Collision detected
        }

        // Check for ghost collision
        if (tile == 'G') { // If the tile is a ghost
            player.die(); // Kill the player
            return true; // Collision detected
        }

        // Check for key pickup
        if (tile == 'K') { // If the tile is a key
            player.pickUpKey(); // Player picks up the key
            map.getMap()[newY][newX] = '.'; // Remove the key from the map by replacing it with a dot
            return false; // No collision detected
        }
        return false; // No collision detected
    }
}