package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class BoardPanel extends JPanel {
    private final Color whiteTileColor;
    private final Color blackTileColor;
    private final Color selectedSquareColor;
    private final boolean whiteIsAtTheBottom;
    private JPanel[] squarePanels;
    private final List<Consumer<Square>> onSquareClickListeners;
    private final List<Consumer<Square>> onSquareDragStartListeners;
    private final List<Consumer<Square>> onSquareDragEndListeners;

    public BoardPanel(Color whiteTileColor, Color blackTileColor, Color selectedSquareColor, boolean whiteIsAtTheBottom, int squareSize) {
        this(whiteTileColor, blackTileColor, selectedSquareColor, whiteIsAtTheBottom, squareSize, null);
    }

    public BoardPanel(Color whiteTileColor, Color blackTileColor, Color selectedSquareColor, boolean whiteIsAtTheBottom, int squareSize, Board board) {

        this.whiteTileColor = whiteTileColor;
        this.blackTileColor = blackTileColor;
        this.selectedSquareColor = selectedSquareColor;
        this.whiteIsAtTheBottom = whiteIsAtTheBottom;
        this.onSquareClickListeners = new LinkedList<>();
        this.onSquareDragEndListeners = new LinkedList<>();
        this.onSquareDragStartListeners = new LinkedList<>();

        this.setLayout(new GridLayout(8, 8));

        drawBoardAndSetUpSquares(whiteTileColor, blackTileColor, squareSize);

        if (board != null)
            drawPosition(board);
    }

    private void drawBoardAndSetUpSquares(Color whiteTileColor, Color blackTileColor, int squareSize) {

        squarePanels = new JPanel[64];

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {

                int index = this.whiteIsAtTheBottom ? row * 8 + file : (7 - row) * 8 + (7 - file);

                final Square square = new Square(index);

                JPanel squarePanel = new JPanel();
                squarePanel.setSize(new Dimension(squareSize, squareSize));

                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == (this.whiteIsAtTheBottom ? 0 : 1);

                squarePanel.setBackground(shouldSquareBeColoredWhite ? whiteTileColor : blackTileColor);
                squarePanel.setAlignmentY(CENTER_ALIGNMENT);
                squarePanel.setAlignmentX(CENTER_ALIGNMENT);

                squarePanel.setLayout(new BorderLayout());

                squarePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        BoardPanel.this.onSquareClickListeners.forEach(listener -> listener.accept(square));
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        BoardPanel.this.onSquareDragStartListeners.forEach(listener -> listener.accept(square));
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        BoardPanel.this.onSquareDragEndListeners.forEach(listener -> listener.accept(square));
                    }
                });

                this.add(squarePanel);

                // not the index, because we might want to switch the sides (black, white) at the bottom
                squarePanels[row * 8 + file] = squarePanel;
            }
        }

    }

    public void drawPosition(Board board) {

        for (int i = 0; i < 64; i++) {
            // WARNING: this removes all children, not only the chess pieces
            this.squarePanels[i].removeAll();
            this.squarePanels[i].validate();
        }

        for (int i = 0; i < 64; i++) {

            final int squareSize = squarePanels[i].getWidth();

            Piece piece = board.get(this.whiteIsAtTheBottom ? i : 63 - i);
            Square square = new Square(this.whiteIsAtTheBottom ? i : 63 - i);

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
                image = ImageIO.read(imageResource).getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH);
//                image = ImageIO.read(imageResource);
            } catch (IOException e) {
                System.err.printf("Image resource path is null! (%s)%n", urlString);
                continue;
            }

            var imageComponent = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
            imageComponent.setSize(new Dimension(squareSize, squareSize));
            imageComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BoardPanel.this.onSquareClickListeners.forEach(listener -> listener.accept(square));
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    BoardPanel.this.onSquareDragStartListeners.forEach(listener -> listener.accept(square));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    BoardPanel.this.onSquareDragEndListeners.forEach(listener -> listener.accept(square));
                }
            });
            imageComponent.setAlignmentX(CENTER_ALIGNMENT);
            imageComponent.setAlignmentY(CENTER_ALIGNMENT);
            imageComponent.setVisible(true);

            squarePanels[i].add(imageComponent, BorderLayout.CENTER);
        }
    }

    public void selectSquare(Square square) {

        int selectedIndex = square == null ? -1 : square.getIndex();

        if (!this.whiteIsAtTheBottom)
            selectedIndex = 63 - selectedIndex; // flip the board if needed (-1 will be invalid this way too)

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {

                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == (this.whiteIsAtTheBottom ? 0 : 1);

                Color tileColor = shouldSquareBeColoredWhite ? whiteTileColor : blackTileColor;

                squarePanels[row * 8 + file].setBackground(row * 8 + file != selectedIndex ? tileColor : selectedSquareColor);
            }
        }

        repaint();
    }

    public List<Consumer<Square>> getOnSquareClickListeners() {
        return onSquareClickListeners;
    }

    public List<Consumer<Square>> getOnSquareDragStartListeners() {
        return onSquareDragStartListeners;
    }

    public List<Consumer<Square>> getOnSquareDragEndListeners() {
        return onSquareDragEndListeners;
    }

    public void addOnSquareClickListeners(Consumer<Square> listener) {
        onSquareClickListeners.add(listener);
    }

    public void addOnSquareDragStartListeners(Consumer<Square> listener) {
        onSquareDragStartListeners.add(listener);
    }

    public void addOnSquareDragEndListeners(Consumer<Square> listener) {
        onSquareDragEndListeners.add(listener);
    }

    public void removeAllOnSquareClickListeners() {
        onSquareClickListeners.clear();
    }

    public void removeAllOnSquareDragStartListeners() {
        onSquareDragStartListeners.clear();
    }

    public void removeAllOnSquareDragEndListeners() {
        onSquareDragEndListeners.clear();
    }
}
