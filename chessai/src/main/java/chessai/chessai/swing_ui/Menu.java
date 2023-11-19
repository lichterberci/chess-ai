package chessai.chessai.swing_ui;

import chessai.chessai.engine.MinimaxEngine;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class Menu {

	private final JFrame window;
	private final JPanel mainPanel;
	private final JPanel settingsPanel;
	private final JPanel pvpGameSettingsPanel;
	private final JPanel pveGameSettingsPanel;

    public Menu() {

	    window = new JFrame("Chess AI");
	    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		window.setSize(new Dimension(300, 400));

		mainPanel = new JPanel();
		setupMainPanel();
		mainPanel.setVisible(true);

		pvpGameSettingsPanel = new JPanel();
		setupPvpGamePanel();
		pvpGameSettingsPanel.setVisible(true);

		pveGameSettingsPanel = new JPanel();
		setupPveGamePanel();
		pveGameSettingsPanel.setVisible(true);

		settingsPanel = new JPanel();
		setupSettingsPanel();
		settingsPanel.setVisible(true);

		selectMenuPanel("MAIN");

		window.setLocationRelativeTo(null);
		window.setLayout(new BorderLayout());

		window.setVisible(true);
	}

	private void setupSettingsPanel() {

		JButton backBtn = new JButton("Back");

		backBtn.addActionListener(e -> selectMenuPanel("MAIN"));

		settingsPanel.add(backBtn);
	}

	private void setupPveGamePanel() {

		JLabel humanVsHumanLabel = new JLabel("Play against the engine!");

		pveGameSettingsPanel.add(humanVsHumanLabel, BorderLayout.NORTH);

		JPanel buttonHolder = new JPanel(new FlowLayout());

		JButton playBtn = new JButton("Play");
		JButton backBtn = new JButton("Back");

		backBtn.addActionListener(e -> selectMenuPanel("MAIN"));
		playBtn.addActionListener(e -> {
			selectMenuPanel("MAIN");

			var pvpFrame = new PvEGameFrame(
//					new MonteCarloEngine(0, 1.4142, 10, 10000),
//                    "8/1k6/8/8/8/8/1K1Q4/8 w - - 0 1",
					new MinimaxEngine(8),
					true,
					Optional.of(10_000));
			pvpFrame.setVisible(true);
			pvpFrame.setSize(new Dimension(800, 800));
			pvpFrame.setLocationRelativeTo(null);
		});

		buttonHolder.add(playBtn);
		buttonHolder.add(backBtn);

		pveGameSettingsPanel.add(buttonHolder, BorderLayout.CENTER);
	}

	private void setupMainPanel() {

		mainPanel.setSize(window.getSize());
		mainPanel.setLayout(new GridLayout(3, 1));

		JButton pvpBtn = new PrimaryButton("Play against your friend!");
		JButton pveBtn = new PrimaryButton("Play against the engine!");
		JButton settingsBtn = new PrimaryButton("Settings");

		pvpBtn.addActionListener(e -> selectMenuPanel("PVP"));
		pveBtn.addActionListener(e -> selectMenuPanel("PVE"));
		settingsBtn.addActionListener(e -> selectMenuPanel("SETTINGS"));

		mainPanel.add(pvpBtn);
		mainPanel.add(pveBtn);
		mainPanel.add(settingsBtn);

		pvpBtn.setVisible(true);
		pveBtn.setVisible(true);
		settingsBtn.setVisible(true);
	}

	private void setupPvpGamePanel() {


		JLabel humanVsHumanLabel = new JLabel("Play against your friend!");

		pvpGameSettingsPanel.add(humanVsHumanLabel, BorderLayout.NORTH);

		JButton playBtn = new JButton("Play");
		JButton backBtn = new JButton("Back");

		JPanel buttonHolder = new JPanel(new FlowLayout());

		backBtn.addActionListener(e -> selectMenuPanel("MAIN"));
		playBtn.addActionListener(e -> {
			selectMenuPanel("MAIN");

			var pvpFrame = new PvPGameFrame();
			pvpFrame.setSize(new Dimension(800, 800));
			pvpFrame.setLocationRelativeTo(null);
			pvpFrame.setVisible(true);
		});

		buttonHolder.add(playBtn);
		buttonHolder.add(backBtn);

		buttonHolder.setVisible(true);

		pvpGameSettingsPanel.add(buttonHolder, BorderLayout.CENTER);
	}

	private void selectMenuPanel(String panelName) {

		window.remove(mainPanel);
		window.remove(pvpGameSettingsPanel);
		window.remove(pveGameSettingsPanel);
		window.remove(settingsPanel);

		if (panelName.equals("MAIN"))
			window.add(mainPanel, BorderLayout.CENTER);
		if (panelName.equals("SETTINGS"))
			window.add(settingsPanel, BorderLayout.CENTER);
		if (panelName.equals("PVP"))
			window.add(pvpGameSettingsPanel, BorderLayout.CENTER);
		if (panelName.equals("PVE"))
			window.add(pveGameSettingsPanel, BorderLayout.CENTER);

		for (Component component : window.getComponents()) {
			component.validate();
			component.setVisible(true);
		}
	}

	public void show() {
        window.setVisible(true);
    }
}
