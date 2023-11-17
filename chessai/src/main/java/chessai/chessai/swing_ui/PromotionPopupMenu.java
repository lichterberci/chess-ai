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

        this.setLayout(new GridLayout(4, 1, 0, 0));

        this.action = action;

        addMenuItem("Queen", Queen.class, pieceColor);
        addMenuItem("Rook", Rook.class, pieceColor);
        addMenuItem("Bishop", Bishop.class, pieceColor);
        addMenuItem("Knight", Knight.class, pieceColor);

        this.pack();
    }

    private void addMenuItem(String altText, Class<? extends Piece> promotionClass, PieceColor pieceColor) {
        JMenuItem item = new JMenuItem();

        Image image;

        try {
            image = loadImage(promotionClass.getConstructor(PieceColor.class).newInstance(pieceColor));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("%s is not a valid piece!".formatted(promotionClass));
        }

        if (image != null)
            item.setIcon(new ImageIcon(image));
        else
            item.setText(altText);

        item.setHorizontalAlignment(SwingConstants.CENTER);
        item.setVerticalAlignment(SwingConstants.CENTER);
        item.setVerticalTextPosition(SwingConstants.CENTER);
        item.setHorizontalTextPosition(SwingConstants.CENTER);
        item.setVisible(true);
        item.addActionListener(e -> this.action.accept(promotionClass));
        item.setBorderPainted(false);

        this.add(item, BorderLayout.CENTER);
    }

    private Image loadImage(Piece piece) {

        String urlString = "/chessai/chessai/swing_ui/themes/%s/%s%s.png".formatted(
                Settings.getInstance().getPieceTheme(),
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
            image = ImageIO.read(imageResource).getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.printf("Image resource path is null! (%s)%n", urlString);
            return null;
        }

        return image;
    }

}
