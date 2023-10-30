package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class BoardPanel extends JPanel {
    private final boolean whiteIsAtTheBottom;
    private final int squareSize;
    private JPanel[] squarePanels;

    public BoardPanel(Color whiteTileColor, Color blackTileColor, boolean whiteIsAtTheBottom, int squareSize) {
        this(whiteTileColor, blackTileColor, whiteIsAtTheBottom, squareSize, null);
    }

    public BoardPanel(Color whiteTileColor, Color blackTileColor, boolean whiteIsAtTheBottom, int squareSize, Board board) {

        this.whiteIsAtTheBottom = whiteIsAtTheBottom;
        this.squareSize = squareSize;

        this.setLayout(new GridLayout(8, 8));

        drawBoardAndSetUpSquares(whiteTileColor, blackTileColor);

        if (board != null)
            drawPosition(board);
    }

    private void drawBoardAndSetUpSquares(Color whiteTileColor, Color blackTileColor) {

        squarePanels = new JPanel[64];

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {

                int index = row * 8 + file;

                JPanel squarePanel = new JPanel();
                squarePanel.setPreferredSize(new Dimension(squareSize, squareSize));

                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == (this.whiteIsAtTheBottom ? 0 : 1);

                squarePanel.setBackground(shouldSquareBeColoredWhite ? whiteTileColor : blackTileColor);
                squarePanel.setAlignmentY(CENTER_ALIGNMENT);
                squarePanel.setAlignmentX(CENTER_ALIGNMENT);

                this.add(squarePanel);

                squarePanels[index] = squarePanel;
            }
        }

    }

    public void drawPosition(Board board) {

        for (int i = 0; i < 64; i++) {

            Piece piece = board.get(this.whiteIsAtTheBottom ? i : 63 - i);

            if (piece == null)
                continue;

            String urlString = "/chessai/chessai/swing_ui/pieces/%s%s.png".formatted(
                    piece.getColor() == PieceColor.WHITE ? 'w' : 'b',
                    Character.toUpperCase(piece.getFENChar())
            );

            URL imageResource = getClass().getResource(urlString);

            if (imageResource == null) {
                System.err.printf("Image resource path is null! (%s)%n", urlString);
                continue;
            }

            Image image;

            try {
                image = ImageIO.read(imageResource).getScaledInstance(squareSize, squareSize, Image.SCALE_AREA_AVERAGING);
            } catch (IOException e) {
                System.err.printf("Image resource path is null! (%s)%n", urlString);
                continue;
            }

            var imageComponent = new JLabel("A");
//            imageComponent.setIcon(new ImageIcon(image));
            imageComponent.setSize(new Dimension(squareSize, squareSize));
            imageComponent.setHorizontalAlignment(SwingConstants.CENTER);
            imageComponent.setVerticalAlignment(SwingConstants.CENTER);
            imageComponent.setVerticalTextPosition(SwingConstants.CENTER);
            imageComponent.setHorizontalTextPosition(SwingConstants.CENTER);
            imageComponent.setBackground(Color.RED);

            imageComponent.setVisible(true);

            squarePanels[i].add(imageComponent);
        }

    }
}
