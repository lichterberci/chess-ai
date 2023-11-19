package chessai.chessai.swing_ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class PrimaryButton extends JButton {

    private static final Font ROBOTO;

    static {

        try (InputStream inputStream = BoardPanel.class.getResourceAsStream("/chessai/chessai/swing_ui/fonts/Roboto-Regular.ttf")) {
            assert inputStream != null;
            ROBOTO = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public PrimaryButton(String text) {
        super(text);

        this.setBackground(new Color(50, 97, 222, 255));
        this.setForeground(new Color(255, 255, 255, 255));
        this.setFont(ROBOTO);
    }

}
