package chessai.chessai.swing_ui;

import chessai.chessai.engine.MonteCarloEngine;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class Menu {

	private final JFrame window;

    public Menu() {

	    window = new JFrame("Chess AI");
	    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	    window.setLayout(new GridLayout(2, 1));

	    JButton playVsHumanBtn = new JButton("Human vs Human");
	    playVsHumanBtn.addActionListener(e -> {
		    var pvpFrame = new PvPGameFrame();
		    pvpFrame.setVisible(true);
		    pvpFrame.setSize(new Dimension(800, 800));
		    pvpFrame.setLocationRelativeTo(null);
	    });

	    window.add(playVsHumanBtn);

	    JButton playVsEngineBtn = new JButton("Human vs Engine");
	    playVsEngineBtn.addActionListener(e -> {
		    var pvpFrame = new PvEGameFrame(
					new MonteCarloEngine(0, 1.4142, 10, 10000),
//                    "8/1k6/8/8/8/8/1K1Q4/8 w - - 0 1",
//					new MinimaxEngine(8),
					true,
					Optional.of(10_000));
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
