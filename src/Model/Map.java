package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

// The Map class represents the game map
public class Map implements Serializable {
    private int numberOfRows;
    private int numberOfColumns;
    private char[][] map;

    // Constructor that creates a map from a 2D array of characters
    public Map(char[][] mapData) {
        int rowCount = mapData.length + 2;
        int colCount = mapData[0].length + 2;
        map = new char[rowCount][colCount];

        // Adding walls around the map
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                if (row == 0 || row == rowCount - 1 || col == 0 || col == colCount - 1) {
                    map[row][col] = 'X';
                } else {
                    map[row][col] = mapData[row - 1][col - 1];
                }
            }
        }
    }

    // Constructor Copy
    public Map(Map map) {
        this.numberOfRows = map.numberOfRows;
        this.numberOfColumns = map.numberOfColumns;
        this.map = new char[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            System.arraycopy(map.map[i], 0, this.map[i], 0, numberOfColumns);
        }
    }

    // A constructor that loads a map from a file
    public Map(String filename) {
        loadMap(filename);
    }

    // Method for loading a map from a file
    public void loadMap(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            // Loading map dimensions
            String[] dimensions = br.readLine().split(" ");
            numberOfRows = Integer.parseInt(dimensions[0]) + 2;
            numberOfColumns = Integer.parseInt(dimensions[1]) + 2;

            // Map initialization
            map = new char[numberOfRows][numberOfColumns];

            // Adding walls around the map
            for (int row = 0; row < numberOfRows; row++) {
                for (int col = 0; col < numberOfColumns; col++) {
                    if (row == 0 || row == numberOfRows - 1 || col == 0 || col == numberOfColumns - 1) {
                        map[row][col] = 'X';
                    }
                }
            }

            // Loading map content
            for (int i = 1; i < numberOfRows - 1; i++) {
                String row = br.readLine();
                for (int j = 1; j < numberOfColumns - 1; j++) {
                    map[i][j] = row.charAt(j - 1);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for getting information about the map
    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    // pristup k mape
    public char[][] getMap() {
        return map;
    }
}
