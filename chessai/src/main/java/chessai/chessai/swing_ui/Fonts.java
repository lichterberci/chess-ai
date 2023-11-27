package chessai.chessai.swing_ui;

import java.awt.*;
import java.io.IOException;

/**
 * Supplies custom fonts
 */
public class Fonts {

    private static Font roboto;

    public static Font getRobotoFont(int style, float size) {

        if (roboto == null) {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            try (var inputStream = Fonts.class.getResourceAsStream("/chessai/chessai/swing_ui/fonts/Roboto-Regular.ttf")) {
                assert inputStream != null;
                roboto = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                ge.registerFont(roboto);
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return roboto.deriveFont(style, size);
    }
}
