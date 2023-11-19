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
		window.add(mainPanel);

		pvpGameSettingsPanel = new JPanel();
		setupPvpGamePanel();
		pvpGameSettingsPanel.setVisible(true);
		window.add(pvpGameSettingsPanel);

		pveGameSettingsPanel = new JPanel();
		setupPveGamePanel();
		pveGameSettingsPanel.setVisible(true);
		window.add(pveGameSettingsPanel);

		settingsPanel = new JPanel();
		setupSettingsPanel();
		settingsPanel.setVisible(true);
		window.add(settingsPanel);

		window.setLocationRelativeTo(null);
		window.setLayout(new FlowLayout(FlowLayout.CENTER));

		selectMenuPanel("MAIN");

		window.setVisible(true);
	}

	private void setupSettingsPanel() {

		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		settingsPanel.add(backBtn);
	}

	private void setupPveGamePanel() {

		JLabel humanVsHumanLabel = new JLabel("Play against the engine!");

		pveGameSettingsPanel.add(humanVsHumanLabel, BorderLayout.NORTH);

		JPanel buttonHolder = new JPanel(new FlowLayout());

		JButton playBtn = new PrimaryButton("Play", e -> {
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
		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		buttonHolder.add(backBtn);
		buttonHolder.add(playBtn);

		pveGameSettingsPanel.add(buttonHolder, BorderLayout.CENTER);
	}

	private void setupMainPanel() {
		mainPanel.setLayout(new GridLayout(3, 1, 0, 30));

		JButton pvpBtn = new PrimaryButton("Play against your friend!", e -> selectMenuPanel("PVP"));
		JButton pveBtn = new PrimaryButton("Play against the engine!", e -> selectMenuPanel("PVE"));
		JButton settingsBtn = new PrimaryButton("Settings", e -> selectMenuPanel("SETTINGS"));

		mainPanel.add(pvpBtn);
		mainPanel.add(pveBtn);
		mainPanel.add(settingsBtn);
	}

	private void setupPvpGamePanel() {

		JLabel humanVsHumanLabel = new JLabel("Play against your friend!");

		pvpGameSettingsPanel.add(humanVsHumanLabel, BorderLayout.NORTH);

		JButton playBtn = new PrimaryButton("Play", e -> {
			selectMenuPanel("MAIN");

			var pvpFrame = new PvPGameFrame();
			pvpFrame.setSize(new Dimension(800, 800));
			pvpFrame.setLocationRelativeTo(null);
			pvpFrame.setVisible(true);
		});
		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		JPanel buttonHolder = new JPanel(new FlowLayout());

		buttonHolder.add(backBtn);
		buttonHolder.add(playBtn);

		buttonHolder.setVisible(true);

		pvpGameSettingsPanel.add(buttonHolder, BorderLayout.CENTER);
	}

	private void selectMenuPanel(String panelName) {

		mainPanel.setVisible(false);
		pvpGameSettingsPanel.setVisible(false);
		pveGameSettingsPanel.setVisible(false);
		settingsPanel.setVisible(false);

		if (panelName.equals("MAIN"))
			mainPanel.setVisible(true);
		if (panelName.equals("SETTINGS"))
			settingsPanel.setVisible(true);
		if (panelName.equals("PVP"))
			pvpGameSettingsPanel.setVisible(true);
		if (panelName.equals("PVE"))
			pveGameSettingsPanel.setVisible(true);

		window.validate();
	}

	public void show() {
        window.setVisible(true);
    }
}
