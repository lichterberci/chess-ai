package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class BoardPanel extends JPanel {
    private final boolean whiteIsAtTheBottom;
    private final int squareSize;
    private JPanel[] squarePanels;

    public BoardPanel(Color whiteTileColor, Color blackTileColor, boolean whiteIsAtTheBottom, int squareSize) {

        this.whiteIsAtTheBottom = whiteIsAtTheBottom;
        this.squareSize = squareSize;

        this.setLayout(new GridLayout(8, 8));

        drawBoardAndSetUpSquares(whiteTileColor, blackTileColor);

    }

    private void drawBoardAndSetUpSquares(Color whiteTileColor, Color blackTileColor) {

        squarePanels = new JPanel[64];

        for (int file = 0; file < 8; file++) {
            for (int row = 0; row < 8; row++) {

                int index = row * 8 + file;

                JPanel squarePanel = new JPanel();
                squarePanel.setPreferredSize(new Dimension(squareSize, squareSize));

                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == (this.whiteIsAtTheBottom ? 0 : 1);

                squarePanel.setBackground(shouldSquareBeColoredWhite ? whiteTileColor : blackTileColor);

                this.add(squarePanel);

                squarePanels[index] = squarePanel;
            }
        }

    }

    public void drawPosition(Board board) throws IOException {

        for (int i = 0; i < 64; i++) {

            Piece piece = board.get(i);

            if (piece == null)
                continue;

            String urlString = "/chessai/chessai/swing_ui/pieces/%s%s.png".formatted(
                    piece.getColor() == PieceColor.WHITE ? 'w' : 'b',
                    Character.toUpperCase(piece.getFENChar())
            );

            URL imageResource = getClass().getResource(urlString);

            if (imageResource == null)
                throw new FileNotFoundException("Image resource path is null! (%s)".formatted(urlString));

            var image = ImageIO.read(imageResource).getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH);

            var imageComponent = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(image, squareSize, squareSize, null);
                }
            };

//            imageComponent.paintComponent(getGraphics());
            imageComponent.setVisible(true);

            squarePanels[i].add(imageComponent);
        }

    }
}
