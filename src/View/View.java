package View;

import Controller.GameHistory;
import Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// Trieda View reprezentuje hlavný zobrazený rám (JFrame) pre Pac-Man hru.
public class View extends JFrame {
    private final GamePanel gamePanel;
    private final Game game;
    private final GameHistory gameHistory;
    public Boolean HistoryMode = false;

    // Konštruktor triedy View, ktorý inicializuje potrebné objekty a komponenty.
    public View(Game game, GameHistory gameHistory) {
        this.game = game;
        this.gameHistory = gameHistory;

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.EAST);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setTitle("Pac-Man");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setResizable(false);        // pri nastaveni na True -> okno sa moze menit
        setVisible(true);

        // aktualizacia rozlozenia
        revalidate();
        // prekreslenie aktualizovaneho rozlozenia
        repaint();
    }

    // Metóda na aktualizáciu zobrazenia mapy.
    public void drawMap(Map map) {
        gamePanel.setMap(map);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    // Metóda na nastavenie hráča.
    public void setPlayer(Player player) {
        gamePanel.setPlayer(player);
    }

    // Metóda na nastavenie duchov.
    public void setGhosts(List<Ghost> ghosts) {
        gamePanel.setGhosts(ghosts);
    }

    // Metóda na aktualizáciu zobrazenia hráča a duchov.
    public void updateDisplay(Player player, List<Ghost> ghosts) {
        SwingUtilities.invokeLater(() -> {
            setPlayer(player);
            setGhosts(ghosts);
            gamePanel.revalidate();
            gamePanel.repaint();
        });
    }

    // Metóda na vytvorenie tlačidla s daným textom.
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }

    // Metóda na vytvorenie kontrolného panela s tlačidlami pre ovládanie hry.
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));

        // Create buttons
        JButton startStopButton = createButton("⏸️ Stop");
        JButton resetButton = createButton("↻ Reset");
        JButton exitButton = createButton("✕ Exit");

        // Add buttons to the control panel
        controlPanel.add(startStopButton);
        controlPanel.add(resetButton);
        controlPanel.add(exitButton);

        // Add the control panel to the main frame
        add(controlPanel, BorderLayout.EAST);

        startStopButton.addActionListener(e -> {
            game.toggleRunning();
            startStopButton.setText(game.isRunning() ? "⏸️ Stop" : "▶️ Start"); // Change button label based on game state
        });


        resetButton.addActionListener(e -> {
            // Stop any playback that might be in progress
            if (gameHistory.isPlaybackInProgress()) {
                gameHistory.setPlaybackInProgress(false);
                HistoryMode = false;
            }

            if (game.isPlaybackInProgress()) {
                game.setPlaybackInProgress(false);
            }

            // Reset the game
            game.resetGame("data/mapa01.txt");
            // Update the button text
            startStopButton.setText("▶️ Start");

            // Update the view
            this.drawMap(game.getMap());
            this.updateDisplay(game.getPlayer(), game.getGhosts());

        });


        exitButton.addActionListener(e -> {
            // Exit the application
            System.exit(0);
        });

        return controlPanel;
    }

    // Metóda na vytvorenie spodného panela s tlačidlami pre nahrávanie a prehrávanie histórie hry.
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 2));

        // Create buttons
        JButton recordButton = createButton("⏺ Rec");
        JButton loadButton = createButton("\uD83D\uDDAB Load");
        JButton nextStepButton = createButton("➡ Next Step");
        JButton previousStepButton = createButton("⬅ Previous Step");

        nextStepButton.setEnabled(false);
        previousStepButton.setEnabled(false);

        // Add buttons to the bottom panel
        bottomPanel.add(recordButton);
        bottomPanel.add(loadButton);
        bottomPanel.add(previousStepButton);
        bottomPanel.add(nextStepButton);

        recordButton.addActionListener(e -> {
            if (!gameHistory.isRecording()) {
                gameHistory.startRecording();
                recordButton.setText("⏹ Stop Rec");
            } else {
                gameHistory.stopRecording();
                recordButton.setText("⏺ Rec");
                gameHistory.setPlaybackInProgress(false);

                // Show a confirmation message
                JOptionPane.showMessageDialog(this, "Game saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            //game.resetGame("data/save_map.txt");

            HistoryMode = true;

            gameHistory.loadGame();

            if (gameHistory.getGameSnapshots().length == 0) {
                JOptionPane.showMessageDialog(this, "No game history found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the first snapshot and apply it to the game
            GameSnapshot firstSnapshot = gameHistory.getGameSnapshots()[0];
            game.applySnapshot(firstSnapshot);

            // Now draw the map and update the display
            drawMap(game.getMap());
            updateDisplay(game.getPlayer(), game.getGhosts());

            game.toggleRunning();

            gameHistory.setPlaybackInProgress(true);
            gameHistory.setCurrentSnapshotIndex(0);

            JDialog loadDialog = new JDialog(this, "Load Game", true);
            loadDialog.setSize(300, 150);
            loadDialog.setLocationRelativeTo(this);
            loadDialog.setLayout(new BorderLayout());

            JPanel radioPanel = new JPanel(new GridLayout(3, 1));
            ButtonGroup radioButtonGroup = new ButtonGroup();
            JRadioButton continuousSteppingRadioButton = new JRadioButton("Smooth stepping Front");
            JRadioButton continuousSteppingRadioButton2 = new JRadioButton("Smooth stepping Back", true);
            JRadioButton stepByStepRadioButton = new JRadioButton("Gradual stepping");

            radioButtonGroup.add(continuousSteppingRadioButton);
            radioButtonGroup.add(stepByStepRadioButton);
            radioButtonGroup.add(continuousSteppingRadioButton2);

            radioPanel.add(continuousSteppingRadioButton);
            radioPanel.add(continuousSteppingRadioButton2);
            radioPanel.add(stepByStepRadioButton);

            JButton confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(event -> {
                if (continuousSteppingRadioButton.isSelected()) {
                    game.setPlaybackInProgress(true);

                    // Implement continuous stepping with time delay, lambda konstruktor
                    new Thread(() -> {
                        GameSnapshot[] snapshots = gameHistory.getGameSnapshots();
                        for (GameSnapshot snapshot : snapshots) {
                            // Aktualizovanie mapy
                            SwingUtilities.invokeLater(() -> {
                                updateDisplay(snapshot.player(), snapshot.ghosts());
                                drawMap(snapshot.map());
                            });

                            try {
                                Thread.sleep(100); // Time delay between steps (1000ms)
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        gameHistory.setPlaybackInProgress(false); // Stop the playback after it's finished
                        HistoryMode = false; // Set the HistoryMode to false
                        gameHistory.setCurrentSnapshotIndex(-1); // Reset the snapshot index after playback
                    }).start();
                } else if (stepByStepRadioButton.isSelected()) {
                    game.setPlaybackInProgress(true);
                    // Implement step by step with button/keypress
                    nextStepButton.setEnabled(true);
                    previousStepButton.setEnabled(true);
                } else if (continuousSteppingRadioButton2.isSelected()) {
                    game.setPlaybackInProgress(true);

                    // Implement continuous stepping with time delay, lambda konstruktor
                    new Thread(() -> {
                        GameSnapshot[] snapshots = gameHistory.getGameSnapshots();
                        for (int counter = snapshots.length -1; counter >= 0; counter--) {
                            // Aktualizovanie mapy
                            int finalCounter = counter;
                            SwingUtilities.invokeLater(() -> {
                                updateDisplay(snapshots[finalCounter].player(), snapshots[finalCounter].ghosts());
                                drawMap(snapshots[finalCounter].map());
                            });

                            try {
                                Thread.sleep(100); // Time delay between steps (1000ms)
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        gameHistory.setPlaybackInProgress(false); // Stop the playback after it's finished
                        HistoryMode = false; // Set the HistoryMode to false
                        gameHistory.setCurrentSnapshotIndex(-1); // Reset the snapshot index after playback
                    }).start();
                }

                loadDialog.setVisible(false);
                loadDialog.dispose();
            });


            loadDialog.add(radioPanel, BorderLayout.CENTER);
            loadDialog.add(confirmButton, BorderLayout.SOUTH);
            loadDialog.setVisible(true);
        });

        nextStepButton.addActionListener(e -> {
            GameSnapshot snapshot = gameHistory.goToNextSnapshot();
            if (snapshot != null) {
                System.out.println("Applying snapshot index: " + gameHistory.getCurrentSnapshot());

                // Apply the game snapshot
                game.applySnapshot(snapshot);

                // Update the display
                updateDisplay(snapshot.player(), snapshot.ghosts());
                drawMap(snapshot.map());
            } else {
                System.out.println("No next snapshot available");
                gameHistory.setPlaybackInProgress(false); // Stop the playback after it's finished
                HistoryMode = false; // Set the HistoryMode to false
                nextStepButton.setEnabled(false);
                previousStepButton.setEnabled(false);
            }
        });


        previousStepButton.addActionListener(e -> {
            if (!gameHistory.isPlaybackInProgress()) {
                return;
            }

            GameSnapshot snapshot = gameHistory.goToPreviousSnapshot();
            if (snapshot != null) {
                // Apply the game snapshot
                game.applySnapshot(snapshot);

                // Update the display
                updateDisplay(snapshot.player(), snapshot.ghosts());
                drawMap(snapshot.map());
            } else {
                gameHistory.setPlaybackInProgress(false);
                nextStepButton.setEnabled(false);
                previousStepButton.setEnabled(false);
            }
        });


        return bottomPanel;
    }

    // Vnútorná trieda GamePanel reprezentuje zobrazenie hry (mapy, hráča, duchov).
    private class GamePanel extends JPanel implements MouseListener {
        private Map map;
        private Player player;
        private List<Ghost> ghosts;
        private final int cellSize = 40;

        public GamePanel() {
            addMouseListener(this);
        }

        // Nastavenie mapy pre GamePanel.
        public void setMap(Map map) {
            this.map = map;
        }

        // Nastavenie hráča pre GamePanel.
        public void setPlayer(Player player) {
            this.player = player;
        }

        // Nastavenie duchov pre GamePanel.
        public void setGhosts(List<Ghost> ghosts) {
            this.ghosts = ghosts;
        }

        // Metóda na vykreslenie mapy, hráča a duchov na GamePanel.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (map != null) {
                char[][] mapData = map.getMap();

                for (int row = 0; row < mapData.length; row++) {
                    for (int col = 0; col < mapData[row].length; col++) {
                        int x = col * cellSize;
                        int y = row * cellSize;

                        switch (mapData[row][col]) {
                            case 'T' -> g.setColor(Color.YELLOW);
                            case 'X' -> g.setColor(Color.BLUE);
                            case 'G' -> g.setColor(Color.RED);
                            case 'K' -> g.setColor(Color.GREEN);
                            case '.' -> g.setColor(Color.BLACK);
                            case 'S' -> g.setColor(Color.ORANGE);
                        }

                        g.fillRect(x, y, cellSize, cellSize);
                    }
                }

                if (player != null) {
                    g.setColor(Color.ORANGE);
                    g.fillRect(player.getX() * cellSize, player.getY() * cellSize, cellSize, cellSize);
                }
                if (ghosts != null) {
                    for (Ghost ghost : ghosts) {
                        g.setColor(Color.RED);
                        g.fillRect(ghost.getX() * cellSize, ghost.getY() * cellSize, cellSize, cellSize);
                    }
                }
            }

            drawEndGameMessage(g);
            drawStepCounter(g);
        }

        // Metóda na vykreslenie koncového odkazu pre hru.
        private void drawEndGameMessage(Graphics g) {
            if (!game.isRunning()) {
                String message;
                if (game.gameJustReset()) {
                    message = "Ready to play?";
                } else if (player.hasKey() && map.getMap()[player.getY()][player.getX()] == 'T') {
                    message = "WIN";
                } else if (HistoryMode) {
                    message = "REC ON";
                } else {
                    message = "LOSE";
                }

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                FontMetrics fm = g.getFontMetrics();
                int stringX = (getWidth() - fm.stringWidth(message)) / 2;
                int stringY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(message, stringX, stringY);
            }
        }

        // Metóda na vykreslenie počítadla krokov.
        private void drawStepCounter(Graphics g) {
            String stepCounterMessage = "Steps: " + game.getStepCounter();

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            FontMetrics fm = g.getFontMetrics();
            int stringX = 10;
            int stringY = fm.getAscent() + 5;
            g.drawString(stepCounterMessage, stringX, stringY);
        }

        // Rôzne metódy na spracovanie udalostí myši, ktoré môžu byť pridané podľa potreby.
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            int cellX = mouseX / cellSize;
            int cellY = mouseY / cellSize;

            if (cellX == player.getX() && cellY == player.getY()) {
                if (player.hasKey()) {
                    JOptionPane.showMessageDialog(this, "The player has the key.", "Key information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "The player has no key.", "Key information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        // mozne pridanie dalsich akcii
        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }


    }

}
