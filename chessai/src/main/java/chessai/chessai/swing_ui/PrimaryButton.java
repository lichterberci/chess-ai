package chessai.chessai.swing_ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Represents the button to use in the application for most purposes.
 */
public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        this(text, (e) -> {
        });
    }

    public PrimaryButton(String text, ActionListener actionListener) {
        super(text);

        this.setBackground(new Color(50, 97, 222, 255));
        this.setForeground(new Color(255, 255, 255, 255));
        this.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.addActionListener(actionListener);
    }
}
