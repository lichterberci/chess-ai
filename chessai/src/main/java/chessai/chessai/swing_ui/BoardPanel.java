package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class BoardPanel extends JPanel {
    private static final float PIECE_SIZE = 0.95f;
    private final boolean whiteIsAtTheBottom;
    private final int squareSize;
    private JPanel[] squarePanels;
    private transient Board positionDisplayed;
    private final transient List<Consumer<Square>> onSquareClickListeners;
    private final transient List<Consumer<Square>> onSquareDragStartListeners;
    private final transient List<Consumer<Square>> onSquareDragEndListeners;
    private final transient Map<String, BackgroundColorLayer> backgroundColorLayerMap;
    public record MoveSoundType(boolean isCapture, boolean isCheck, boolean isPromotion, boolean isCastle) {
    }

    private record BackgroundColorLayer(List<Square> squares, Color backgrounColor, int priority) {
    }

    public BoardPanel(boolean whiteIsAtTheBottom, int squareSize) {
        this(whiteIsAtTheBottom, squareSize, null);
    }

    public BoardPanel(boolean whiteIsAtTheBottom, int squareSize, Board board) {

        this.whiteIsAtTheBottom = whiteIsAtTheBottom;
        this.squareSize = squareSize;
        this.onSquareClickListeners = new LinkedList<>();
        this.onSquareDragEndListeners = new LinkedList<>();
        this.onSquareDragStartListeners = new LinkedList<>();
        this.backgroundColorLayerMap = new HashMap<>();

        this.setSize(squareSize * 8, squareSize * 8);
        this.setLayout(new GridLayout(8, 8));

        drawBoardAndSetUpSquares();

        if (board != null)
            drawPosition(board);
    }

    private void drawBoardAndSetUpSquares() {

        squarePanels = new JPanel[64];

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {

                int index = this.whiteIsAtTheBottom ? row * 8 + file : (7 - row) * 8 + (7 - file);

                final Square square = new Square(index);

                JPanel squarePanel = new JPanel();
                squarePanel.setSize(new Dimension(squareSize, squareSize));

                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == (this.whiteIsAtTheBottom ? 0 : 1);

                squarePanel.setBackground(shouldSquareBeColoredWhite ? Settings.getInstance().getWhiteTileColor() : Settings.getInstance().getBlackTileColor());
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

        if (board != null) {
            positionDisplayed = new Board(board);
        }

        for (int i = 0; i < 64; i++) {
            // WARNING: this removes all children, not only the chess pieces
            this.squarePanels[i].removeAll();
        }

        for (int i = 0; i < 64; i++) {

            Piece piece = positionDisplayed.get(this.whiteIsAtTheBottom ? i : 63 - i);
            Square square = new Square(this.whiteIsAtTheBottom ? i : 63 - i);

            if (piece == null)
                continue;

            String urlString = "/chessai/chessai/swing_ui/themes/%s/%s%s.png".formatted(
                    Settings.getInstance().getPieceTheme(),
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
                image = ImageIO.read(imageResource).getScaledInstance((int) (squareSize * PIECE_SIZE), (int) (squareSize * PIECE_SIZE), Image.SCALE_SMOOTH);
//                image = ImageIO.read(imageResource);
            } catch (IOException e) {
                System.err.printf("Image resource path is null! (%s)%n", urlString);
                continue;
            }

            var imageComponent = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
            imageComponent.setSize(new Dimension((int) (squareSize * PIECE_SIZE), (int) (squareSize * PIECE_SIZE)));
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

    public void drawLayer(String name, Color color, List<Square> squares, int priority) {
        backgroundColorLayerMap.put(name, new BackgroundColorLayer(squares, color, priority));
        repaintBackground();
    }

    private void repaintBackground() {

        List<BackgroundColorLayer> orderedBackgroundLayers = backgroundColorLayerMap.values()
                .stream()
                .sorted(Comparator.comparing(BackgroundColorLayer::priority).reversed())
                .toList();

        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {
                final boolean shouldSquareBeColoredWhite = (file + row) % 2 == 0;

                Color defaultTileColor = shouldSquareBeColoredWhite ? Settings.getInstance().getWhiteTileColor() : Settings.getInstance().getBlackTileColor();

                Square currentSquare = whiteIsAtTheBottom ? new Square(file, 7 - row) : new Square(7 - file, row);

                Optional<BackgroundColorLayer> colorOfMaxPriorityLayer = orderedBackgroundLayers.stream()
                        .filter(layer -> layer.squares().contains(currentSquare))
                        .findFirst();

                if (colorOfMaxPriorityLayer.isEmpty()) {
                    squarePanels[row * 8 + file].setBackground(defaultTileColor);
                    continue;
                }

                Color layerColor = colorOfMaxPriorityLayer.get().backgrounColor();

                if (!shouldSquareBeColoredWhite)
                    layerColor = layerColor.darker();

                squarePanels[row * 8 + file].setBackground(layerColor);
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

    public void playMoveSound(MoveSoundType soundType) {

        if (soundType.isCheck) {
            playClip("move-check");
        } else if (soundType.isCapture) {
            playClip("capture");
        } else if (soundType.isPromotion) {
            playClip("promote");
        } else if (soundType.isCastle) {
            playClip("castle");
        } else {
            playClip("move-self");
        }
    }

    private void playClip(String name) {
        String urlString = "/chessai/chessai/swing_ui/%s/%s.wav".formatted(
                Settings.getInstance().getSoundTheme(),
                name
        );

        InputStream resourceInputStream = getClass().getResourceAsStream(urlString);

        if (resourceInputStream == null) {
            System.err.printf("Sound resource path is null! (%s)%n", urlString);
            return;
        }

        AudioInputStream audioInputStream;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(resourceInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.printf("Cannot load sound! (%s)%n", urlString);
            return;
        }

        Clip clip;

        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            System.err.printf("Cannot load clip! (%s)%n", urlString);
            return;
        }

        try {
            clip.open(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            System.err.printf("Cannot open clip! (%s)%n", urlString);
            return;
        }

        clip.start();
    }
}
