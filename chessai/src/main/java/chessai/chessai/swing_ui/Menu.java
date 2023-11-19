package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MinimaxEngine;
import chessai.chessai.engine.MonteCarloEngine;
import chessai.chessai.engine.RandomEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Menu {

	private static final Map<String, ChessEngine> PLAYABLE_CHESS_ENGINES;
	private final JFrame window;
	private final JPanel mainPanel;
	private final JPanel settingsPanel;
	private final JPanel pvpGameSettingsPanel;
	private final JPanel pveGameSettingsPanel;

	static {
		PLAYABLE_CHESS_ENGINES = new HashMap<>();
		PLAYABLE_CHESS_ENGINES.put("Random", new RandomEngine());
		PLAYABLE_CHESS_ENGINES.put("Monte Carlo", new MonteCarloEngine(0, 1.4142, 100, 1000));
		PLAYABLE_CHESS_ENGINES.put("Minimax", new MinimaxEngine(10, 1_000_000_000));
	}

    public Menu() {

	    window = new JFrame("Chess AI");
	    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		try {
			window.setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/chessai/chessai/swing_ui/themes/old/bp.png"))));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		window.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
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

		window.setMinimumSize(new Dimension(300, 150));
		window.setVisible(true);
	}

	private void setupSettingsPanel() {

		Settings currentUnsavedSettings = new Settings(Settings.getInstance());

		settingsPanel.setLayout(new BorderLayout());

		JPanel colorSettingsHolder = new JPanel();
		colorSettingsHolder.setLayout(new GridLayout(4, 3, 10, 20));
		colorSettingsHolder.setBorder(BorderFactory.createTitledBorder("Color palette"));

		// ---------------------- BLACK TILE -----------------------------------

		JLabel blackTileColorLabel = new JLabel("Black tile color: ");
		blackTileColorLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		blackTileColorLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		blackTileColorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		colorSettingsHolder.add(blackTileColorLabel);

		JPanel blackTileColorPreviewPanel = new JPanel();
		blackTileColorPreviewPanel.setBackground(currentUnsavedSettings.getBlackTileColor());
		blackTileColorPreviewPanel.setSize(new Dimension(15, 15));
		blackTileColorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		colorSettingsHolder.add(blackTileColorPreviewPanel);

		JButton blackTileColorBtn = new PrimaryButton("Choose color", e -> {
			Color newColor = JColorChooser.showDialog(colorSettingsHolder, "Choose color", currentUnsavedSettings.getBlackTileColor());
			if (newColor != null)
				currentUnsavedSettings.setBlackTileColor(newColor);
			blackTileColorPreviewPanel.setBackground(currentUnsavedSettings.getBlackTileColor());
		});
		colorSettingsHolder.add(blackTileColorBtn);

		// ---------------------- WHITE TILE -----------------------------------

		JLabel whiteTileColorLabel = new JLabel("White tile color: ");
		whiteTileColorLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		whiteTileColorLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		whiteTileColorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		colorSettingsHolder.add(whiteTileColorLabel);

		JPanel whiteTileColorPreviewPanel = new JPanel();
		whiteTileColorPreviewPanel.setBackground(currentUnsavedSettings.getWhiteTileColor());
		whiteTileColorPreviewPanel.setSize(new Dimension(15, 15));
		whiteTileColorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		colorSettingsHolder.add(whiteTileColorPreviewPanel);

		JButton whiteTileColorBtn = new PrimaryButton("Choose color", e -> {
			Color newColor = JColorChooser.showDialog(colorSettingsHolder, "Choose color", currentUnsavedSettings.getBlackTileColor());
			if (newColor != null)
				currentUnsavedSettings.setWhiteTileColor(newColor);
			whiteTileColorPreviewPanel.setBackground(currentUnsavedSettings.getWhiteTileColor());
		});
		colorSettingsHolder.add(whiteTileColorBtn);

		// ---------------------- MOVE HIGHLIGHT -----------------------------------

		JLabel moveHighlightColorLabel = new JLabel("Move highlight color: ");
		moveHighlightColorLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		moveHighlightColorLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		moveHighlightColorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		colorSettingsHolder.add(moveHighlightColorLabel);

		JPanel moveHighlightColorPreviewPanel = new JPanel();
		moveHighlightColorPreviewPanel.setBackground(currentUnsavedSettings.getMoveHighlightColor());
		moveHighlightColorPreviewPanel.setSize(new Dimension(15, 15));
		moveHighlightColorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		colorSettingsHolder.add(moveHighlightColorPreviewPanel);

		JButton moveHighlightColorBtn = new PrimaryButton("Choose color", e -> {
			Color newColor = JColorChooser.showDialog(colorSettingsHolder, "Choose color", currentUnsavedSettings.getBlackTileColor());
			if (newColor != null)
				currentUnsavedSettings.setMoveHighlightColor(newColor);
			moveHighlightColorPreviewPanel.setBackground(currentUnsavedSettings.getMoveHighlightColor());
		});
		colorSettingsHolder.add(moveHighlightColorBtn);

		// ---------------------- SELECTED SQUARE -----------------------------------

		JLabel selectedSquareColorLabel = new JLabel("Selected square color: ");
		selectedSquareColorLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		selectedSquareColorLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		selectedSquareColorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		colorSettingsHolder.add(selectedSquareColorLabel);

		JPanel selectedSquareColorPreviewPanel = new JPanel();
		selectedSquareColorPreviewPanel.setBackground(currentUnsavedSettings.getSelectedPieceBackgroundColor());
		selectedSquareColorPreviewPanel.setSize(new Dimension(15, 15));
		selectedSquareColorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		colorSettingsHolder.add(selectedSquareColorPreviewPanel);

		JButton selectedSquareColorBtn = new PrimaryButton("Choose color", e -> {
			Color newColor = JColorChooser.showDialog(colorSettingsHolder, "Choose color", currentUnsavedSettings.getBlackTileColor());
			if (newColor != null)
				currentUnsavedSettings.setSelectedPieceBackgroundColor(newColor);
			selectedSquareColorPreviewPanel.setBackground(currentUnsavedSettings.getSelectedPieceBackgroundColor());
		});
		colorSettingsHolder.add(selectedSquareColorBtn);

		settingsPanel.add(colorSettingsHolder, BorderLayout.CENTER);

		// ------------------------------- BTN HOLDER --------------------------

		JButton backBtn = new PrimaryButton("Back", e -> {
			currentUnsavedSettings.reset();
			blackTileColorPreviewPanel.setBackground(currentUnsavedSettings.getBlackTileColor());
			whiteTileColorPreviewPanel.setBackground(currentUnsavedSettings.getWhiteTileColor());
			moveHighlightColorPreviewPanel.setBackground(currentUnsavedSettings.getMoveHighlightColor());
			selectedSquareColorPreviewPanel.setBackground(currentUnsavedSettings.getSelectedPieceBackgroundColor());
			selectMenuPanel("MAIN");
		});
		JButton resetBtn = new PrimaryButton("Reset", e -> {
			currentUnsavedSettings.reset();
			blackTileColorPreviewPanel.setBackground(currentUnsavedSettings.getBlackTileColor());
			whiteTileColorPreviewPanel.setBackground(currentUnsavedSettings.getWhiteTileColor());
			moveHighlightColorPreviewPanel.setBackground(currentUnsavedSettings.getMoveHighlightColor());
			selectedSquareColorPreviewPanel.setBackground(currentUnsavedSettings.getSelectedPieceBackgroundColor());
		});
		JButton saveBtn = new PrimaryButton("Save", e -> {
			Settings.updateInstance(currentUnsavedSettings);
			Settings.saveSettings();
			selectMenuPanel("MAIN");
		});

		JPanel buttonHolder = new JPanel(new FlowLayout());
		buttonHolder.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		buttonHolder.add(backBtn);
		buttonHolder.add(resetBtn);
		buttonHolder.add(saveBtn);

		settingsPanel.add(buttonHolder, BorderLayout.SOUTH);

		settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}


	private void setupMainPanel() {
		mainPanel.setLayout(new GridLayout(4, 1, 0, 20));

		JLabel title = new JLabel("GM dojo");
		Image pawnImage;
		try {
			pawnImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/chessai/chessai/swing_ui/themes/old/bk.png"))).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			title.setIcon(new ImageIcon(Objects.requireNonNull(pawnImage)));
			title.setHorizontalTextPosition(SwingConstants.LEFT);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setIconTextGap(10);
		} catch (IOException e) {
			System.err.println("Cannot load pawn icon, defaulting to only text...");
		}
		title.setFont(Fonts.getRobotoFont(Font.PLAIN, 30));
		mainPanel.add(title);

		JButton pvpBtn = new PrimaryButton("Play against your friend!", e -> selectMenuPanel("PVP"));
		JButton pveBtn = new PrimaryButton("Play against the engine!", e -> selectMenuPanel("PVE"));
		JButton settingsBtn = new PrimaryButton("Settings", e -> selectMenuPanel("SETTINGS"));

		mainPanel.add(pvpBtn);
		mainPanel.add(pveBtn);
		mainPanel.add(settingsBtn);

		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void setupPvpGamePanel() {

		pvpGameSettingsPanel.setLayout(new GridLayout(2, 1));

		JLabel humanVsHumanLabel = new JLabel("Play against your friend!");
		humanVsHumanLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));

		pvpGameSettingsPanel.add(humanVsHumanLabel);

		JButton playBtn = new PrimaryButton("Play", e -> SwingUtilities.invokeLater(() -> {
			var pvpFrame = new PvPGameFrame();
			pvpFrame.setSize(new Dimension(800, 800));
			pvpFrame.setLocationRelativeTo(null);
			pvpFrame.setVisible(true);

			selectMenuPanel("MAIN");

		}));
		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		JPanel buttonHolder = new JPanel(new FlowLayout());

		buttonHolder.add(backBtn);
		buttonHolder.add(playBtn);

		buttonHolder.setVisible(true);

		pvpGameSettingsPanel.add(buttonHolder);

		pvpGameSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void setupPveGamePanel() {

		pveGameSettingsPanel.setLayout(new GridLayout(4, 1));

		JLabel humanVsEngineLabel = new JLabel("Choose game settings!");
		humanVsEngineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		humanVsEngineLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		humanVsEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));

		pveGameSettingsPanel.add(humanVsEngineLabel);

		JPanel engineSelectorPanel = new JPanel(new FlowLayout());

		JLabel selectEngineLabel = new JLabel("Select engine: ");
		selectEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));

		engineSelectorPanel.add(selectEngineLabel);

		JComboBox<String> engineSelectorDropdown = new JComboBox<>(new Vector<>(PLAYABLE_CHESS_ENGINES.keySet().stream().toList()));
		engineSelectorDropdown.setSelectedIndex(0);
		engineSelectorPanel.add(engineSelectorDropdown);

		pveGameSettingsPanel.add(engineSelectorPanel);

		JPanel isPlayingWithWhitePanel = new JPanel(new FlowLayout());

		JLabel isPlayingWithWhiteLabel = new JLabel("Player is white: ");
		isPlayingWithWhiteLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		isPlayingWithWhitePanel.add(isPlayingWithWhiteLabel);

		JCheckBox isPlayingWithWhiteCheckBox = new JCheckBox();
		isPlayingWithWhiteCheckBox.setSize(new Dimension(20, 20));
		isPlayingWithWhitePanel.add(isPlayingWithWhiteCheckBox);

		pveGameSettingsPanel.add(isPlayingWithWhitePanel);

		JPanel timeAvailableForTheEnginePanel = new JPanel(new FlowLayout());

		JLabel timeAvailableForTheEngineLabel = new JLabel("Time available for the engine (in seconds): ");
		timeAvailableForTheEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		timeAvailableForTheEnginePanel.add(timeAvailableForTheEngineLabel);

		JTextField timeAvailableForTheEngineTextField = new JTextField(5);
		timeAvailableForTheEngineTextField.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		timeAvailableForTheEnginePanel.add(timeAvailableForTheEngineTextField);

		pveGameSettingsPanel.add(timeAvailableForTheEnginePanel);


		JPanel buttonHolder = new JPanel(new FlowLayout());

		Optional<Integer> availableTimeForTheEngine;

		try {
			availableTimeForTheEngine = Optional.of((int) Math.abs(Math.floor(Double.parseDouble(timeAvailableForTheEngineTextField.getText().trim()) * 1000)));
		} catch (NumberFormatException e) {
			availableTimeForTheEngine = Optional.of(1_000);
		}

		Optional<Integer> finalAvailableTimeForTheEngine = availableTimeForTheEngine;

		JButton playBtn = new PrimaryButton("Play", e -> SwingUtilities.invokeLater(() -> {
			var pvpFrame = new PvEGameFrame(
					PLAYABLE_CHESS_ENGINES.get((String) engineSelectorDropdown.getSelectedItem()),
					isPlayingWithWhiteCheckBox.isSelected(),
					finalAvailableTimeForTheEngine);
			pvpFrame.setVisible(true);
			pvpFrame.setSize(new Dimension(800, 800));
			pvpFrame.setLocationRelativeTo(null);

			selectMenuPanel("MAIN");
		}));
		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		buttonHolder.add(backBtn);
		buttonHolder.add(playBtn);

		pveGameSettingsPanel.add(buttonHolder);

		pveGameSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

		window.setTitle(switch (panelName) {
			case "MAIN" -> "Menu";
			case "SETTINGS" -> "Settings";
			case "PVP" -> "Play against your friend!";
			case "PVE" -> "Play against the engine!";
			default -> throw new IllegalStateException("Unexpected value: " + panelName);
		});

		window.validate();
		window.pack();
		window.setLocationRelativeTo(null);
	}

	public void show() {
        window.setVisible(true);
    }
}
