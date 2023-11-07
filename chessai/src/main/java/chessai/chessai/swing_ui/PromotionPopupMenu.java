package chessai.chessai.swing_ui;

import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.pieces.Bishop;
import chessai.chessai.lib.pieces.Knight;
import chessai.chessai.lib.pieces.Queen;
import chessai.chessai.lib.pieces.Rook;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.function.Consumer;

public class PromotionPopupMenu extends JPopupMenu {

    private final transient Consumer<Class<? extends Piece>> action;

    public PromotionPopupMenu(Consumer<Class<? extends Piece>> action, PieceColor pieceColor) {
        super("Piece type");

        this.action = action;

        addMenuItem("Queen", Queen.class, pieceColor);
        addMenuItem("Rook", Rook.class, pieceColor);
        addMenuItem("Bishop", Bishop.class, pieceColor);
        addMenuItem("Knight", Knight.class, pieceColor);
    }

    private void addMenuItem(String altText, Class<? extends Piece> promotionClass, PieceColor pieceColor) {
        JMenuItem queenItem = new JMenuItem();
        queenItem.setSize(new Dimension(100, 100));

        Image queenImage = null;

        try {
            queenImage = loadImage(promotionClass.getConstructor(PieceColor.class).newInstance(pieceColor));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("%s is not a valid piece!".formatted(promotionClass));
        }

        if (queenImage != null)
            queenItem.setIcon(new ImageIcon(queenImage));
        else
            queenItem.setText(altText);

        queenItem.setAlignmentX(CENTER_ALIGNMENT);
        queenItem.setAlignmentY(CENTER_ALIGNMENT);
        queenItem.addActionListener(e -> this.action.accept(promotionClass));

        this.add(queenItem);
    }

    private Image loadImage(Piece piece) {

        String urlString = "/chessai/chessai/swing_ui/pieces/%s%s.png".formatted(
                piece.getColor() == PieceColor.WHITE ? 'w' : 'b',
                Character.toUpperCase(piece.getFENChar())
        );

        URL imageResource = getClass().getResource(urlString);

        if (imageResource == null) {
            System.err.printf("Image resource path is null! (%s)%n", urlString);
            return null;
        }

        Image image;

        try {
            image = ImageIO.read(imageResource).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.printf("Image resource path is null! (%s)%n", urlString);
            return null;
        }

        return image;
    }

}
