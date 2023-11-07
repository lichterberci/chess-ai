package chessai.chessai.swing_ui;

import javax.swing.*;
import java.awt.*;

public class Menu {

    private JFrame window;

    public Menu() {

        window = new JFrame("Chess AI");
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton playVsEngineBtn = new JButton("Human vs Human");
        playVsEngineBtn.addActionListener(e -> {
	        var pvpFrame = new PvPGameFrame();
	        pvpFrame.setVisible(true);
	        pvpFrame.setSize(new Dimension(800, 800));
	        pvpFrame.setLocationRelativeTo(null);
        });

        window.add(playVsEngineBtn);

        window.pack();
        window.setLocationRelativeTo(null);
    }

    public void show() {
        window.setVisible(true);
    }
}
