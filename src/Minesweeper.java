import java.awt.*;
import java.awt.Event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numColumns = 8;
    int boardWidth = numColumns * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton restartButton = new JButton("Restart");

    int mineCount = 10; // number of mines to randomly place
    MineTile[][] board = new MineTile[numRows][numColumns];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver;

    Minesweeper() {
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        restartButton.setFocusable(false);
        buttonPanel.add(restartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        boardPanel.setLayout(new GridLayout(numRows, numColumns));
        // boardPanel.setBackground(Color.green);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver)
                            return;
                        MineTile tile = (MineTile) e.getSource();
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    // lose
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3 && tile.isEnabled()) {
                            if (tile.getText() == "")
                                tile.setText("🚩");
                            else if (tile.getText() == "🚩")
                                tile.setText("");
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        setMines();

        frame.setVisible(true);
    }

    void setMines() {
        mineList = new ArrayList<MineTile>();
        // mineList.add(board[2][2]);
        // mineList.add(board[6][3]);
        // mineList.add(board[7][1]);
        // mineList.add(board[5][5]);
        // mineList.add(board[4][3]);
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numColumns);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }

    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns) {
            return;
        }

        MineTile tile = board[r][c];

        if (tile.isEnabled() == false)
            return;

        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;
        // check neighbors
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            // recursion
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numColumns - mineList.size()) {
            gameOver = true;
            textLabel.setText("You win!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns) {
            return 0;
        }
        if (mineList.contains(board[r][c]))
            return 1;
        else
            return 0;
    }

    void restartGame() {
        tilesClicked = 0;
        gameOver = false;
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MineTile tile = board[r][c];
                tile.setText("");
                tile.setEnabled(true);
            }
        }
        setMines();
    }
}
