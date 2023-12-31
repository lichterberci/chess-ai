package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MinimaxEngine;
import chessai.chessai.engine.MonteCarloEngine;
import chessai.chessai.engine.RandomEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * The class containing all the menu logic.
 */
public class Menu {

	private static final Map<String, ChessEngine> PLAYABLE_CHESS_ENGINES;
	private static final Map<String, String> AVAILABLE_THEMES;
	private final JFrame window;
	private final JPanel mainPanel;
	private final JPanel settingsPanel;
	private final JPanel pvpGameSettingsPanel;
	private final JPanel pveGameSettingsPanel;
	private final JPanel analyzeGamePanel;
	private final JPanel puzzleSolverPanel;

	static {
		PLAYABLE_CHESS_ENGINES = new HashMap<>();
		PLAYABLE_CHESS_ENGINES.put("Random", new RandomEngine());
		PLAYABLE_CHESS_ENGINES.put("Shallow Monte Carlo", new MonteCarloEngine(0, 1.1, 50, 10000));
		PLAYABLE_CHESS_ENGINES.put("Deep Monte Carlo", new MonteCarloEngine(0, 1.4, 300, 100000));
		PLAYABLE_CHESS_ENGINES.put("Minimax (10MB transposition table)", new MinimaxEngine(20, 10_000_000));
		PLAYABLE_CHESS_ENGINES.put("Minimax (1GB transposition table)", new MinimaxEngine(20, 1_000_000_000));

		AVAILABLE_THEMES = new HashMap<>();
		AVAILABLE_THEMES.put("Neo", "neo");
		AVAILABLE_THEMES.put("Classic", "classic");
		AVAILABLE_THEMES.put("Old", "old");
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

	    analyzeGamePanel = new JPanel();
	    setupAnalyzeGamePanel();
	    analyzeGamePanel.setVisible(true);
	    window.add(analyzeGamePanel);

	    settingsPanel = new JPanel();
	    setupSettingsPanel();
	    settingsPanel.setVisible(true);
	    window.add(settingsPanel);

	    puzzleSolverPanel = new JPanel();
	    setupPuzzleSolverPanel();
	    puzzleSolverPanel.setVisible(true);
	    window.add(puzzleSolverPanel);

	    window.setLocationRelativeTo(null);
	    window.setLayout(new FlowLayout(FlowLayout.CENTER));

	    selectMenuPanel("MAIN");

	    window.setMinimumSize(new Dimension(300, 150));
	    window.setVisible(true);
    }

	private void setupSettingsPanel() {

		Settings currentUnsavedSettings = new Settings(Settings.getInstance());

		settingsPanel.setLayout(new BorderLayout());

		// ---------------------- THEMES ---------------------------------------

		JPanel themeSelectorPanel = new JPanel();
		themeSelectorPanel.setLayout(new GridLayout(1, 2, 20, 10));
		themeSelectorPanel.setBorder(BorderFactory.createTitledBorder("Themes"));

		JLabel pieceThemeLabel = new JLabel("Pieces:");
		pieceThemeLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		pieceThemeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		pieceThemeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		themeSelectorPanel.add(pieceThemeLabel);

		JComboBox<String> pieceThemeDropdown = new JComboBox<>(new Vector<>(AVAILABLE_THEMES.keySet().stream().toList()));
		pieceThemeDropdown.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		pieceThemeDropdown.setEditable(false);
		pieceThemeDropdown.setSelectedItem(
				AVAILABLE_THEMES.entrySet()
						.stream()
						.filter(entry -> entry.getValue().equals(currentUnsavedSettings.getPieceTheme()))
						.map(Map.Entry::getKey)
						.findFirst()
						.get()
		);
		pieceThemeDropdown.repaint();
		pieceThemeDropdown.addActionListener(e -> {
			currentUnsavedSettings.setPieceTheme(AVAILABLE_THEMES.get((String) pieceThemeDropdown.getSelectedItem()));
		});
		themeSelectorPanel.add(pieceThemeDropdown);

		settingsPanel.add(themeSelectorPanel, BorderLayout.NORTH);

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
			pieceThemeDropdown.setSelectedItem(
					AVAILABLE_THEMES.entrySet()
							.stream()
							.filter(entry -> entry.getValue().equals(currentUnsavedSettings.getPieceTheme()))
							.map(Map.Entry::getKey)
							.findFirst()
							.get()
			);
			selectMenuPanel("MAIN");
		});
		JButton resetBtn = new PrimaryButton("Reset", e -> {
			currentUnsavedSettings.reset();
			blackTileColorPreviewPanel.setBackground(currentUnsavedSettings.getBlackTileColor());
			whiteTileColorPreviewPanel.setBackground(currentUnsavedSettings.getWhiteTileColor());
			moveHighlightColorPreviewPanel.setBackground(currentUnsavedSettings.getMoveHighlightColor());
			selectedSquareColorPreviewPanel.setBackground(currentUnsavedSettings.getSelectedPieceBackgroundColor());
			pieceThemeDropdown.setSelectedItem(
					AVAILABLE_THEMES.entrySet()
							.stream()
							.filter(entry -> entry.getValue().equals(currentUnsavedSettings.getPieceTheme()))
							.map(Map.Entry::getKey)
							.findFirst()
							.get()
			);
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
		mainPanel.setLayout(new GridLayout(7, 1, 0, 20));

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
		JButton analyzeBtn = new PrimaryButton("Analyze your game!", e -> selectMenuPanel("ANALYZE"));
		JButton settingsBtn = new PrimaryButton("Settings", e -> selectMenuPanel("SETTINGS"));
		JButton solveBtn = new PrimaryButton("Let the engine solve your puzzle!", e -> selectMenuPanel("PUZZLE"));
		JButton exitBtn = new PrimaryButton("Exit", e -> window.dispose());

		mainPanel.add(pvpBtn);
		mainPanel.add(pveBtn);
		mainPanel.add(analyzeBtn);
		mainPanel.add(solveBtn);
		mainPanel.add(settingsBtn);
		mainPanel.add(exitBtn);

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

		pveGameSettingsPanel.setLayout(new GridLayout(5, 1));

		JLabel humanVsEngineLabel = new JLabel("Choose game settings!");
		humanVsEngineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		humanVsEngineLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		humanVsEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 20));

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
		timeAvailableForTheEngineTextField.setText("10.0");
		timeAvailableForTheEngineTextField.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		timeAvailableForTheEnginePanel.add(timeAvailableForTheEngineTextField);

		pveGameSettingsPanel.add(timeAvailableForTheEnginePanel);


		JPanel buttonHolder = new JPanel(new FlowLayout());


		JButton playBtn = new PrimaryButton("Play", e -> SwingUtilities.invokeLater(() -> {
			Optional<Integer> availableTimeForTheEngine;

			try {
				availableTimeForTheEngine = Optional.of((int) Math.abs(Math.floor(Double.parseDouble(timeAvailableForTheEngineTextField.getText().trim()) * 1000)));
			} catch (NumberFormatException err) {
				availableTimeForTheEngine = Optional.empty();
			}

			var pvpFrame = new PvEGameFrame(
					PLAYABLE_CHESS_ENGINES.get((String) engineSelectorDropdown.getSelectedItem()),
					isPlayingWithWhiteCheckBox.isSelected(),
					availableTimeForTheEngine);
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

	private void setupAnalyzeGamePanel() {

		analyzeGamePanel.setLayout(new GridLayout(5, 1));

		JLabel analyzeYourGameLabel = new JLabel("Analyze your game!");
		analyzeYourGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		analyzeYourGameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		analyzeYourGameLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 20));

		analyzeGamePanel.add(analyzeYourGameLabel);

		JPanel engineSelectorPanel = new JPanel(new FlowLayout());

		JLabel selectEngineLabel = new JLabel("Select engine: ");
		selectEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));

		engineSelectorPanel.add(selectEngineLabel);

		JComboBox<String> engineSelectorDropdown = new JComboBox<>(new Vector<>(PLAYABLE_CHESS_ENGINES.keySet().stream().toList()));
		engineSelectorDropdown.setSelectedIndex(0);
		engineSelectorPanel.add(engineSelectorDropdown);

		analyzeGamePanel.add(engineSelectorPanel);

		JPanel pgnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel pgnLabel = new JLabel("PGN: ");
		pgnLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		pgnPanel.add(pgnLabel);


		JTextArea pgnTextArea = new JTextArea(3, 20);
		pgnTextArea.setFont(Fonts.getRobotoFont(Font.PLAIN, 12));
		pgnTextArea.setLineWrap(true);
		pgnTextArea.setAutoscrolls(true);
		;
		JScrollPane pgnTextAreaScrollPane = new JScrollPane(pgnTextArea);

		JButton loadFromFileBtn = new PrimaryButton("Load from file", e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("PGN files", "pgn", "txt"));

			int dialogResult = fileChooser.showOpenDialog(analyzeGamePanel);

			if (dialogResult == JFileChooser.APPROVE_OPTION) {

				File file = fileChooser.getSelectedFile();

				if (!file.exists())
					return;

				try (var scanner = new Scanner(file)) {

					scanner.useDelimiter("\\Z");

					String fileContents = scanner.next();

					pgnTextArea.setText(fileContents.trim());

				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}

			}
		});

		pgnPanel.add(loadFromFileBtn);

		analyzeGamePanel.add(pgnPanel);

		analyzeGamePanel.add(pgnTextAreaScrollPane);

		JPanel buttonHolder = new JPanel(new FlowLayout());

		JButton analyzeBtn = new PrimaryButton("Analyze", e -> SwingUtilities.invokeLater(() -> {
			String pgnString = pgnTextArea.getText();

			GameAnalyzerFrame gameAnalyzerFrame;

			try {
				gameAnalyzerFrame = new GameAnalyzerFrame(pgnString, PLAYABLE_CHESS_ENGINES.get((String) engineSelectorDropdown.getSelectedItem()));
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}

			gameAnalyzerFrame.setLocationRelativeTo(null);
			gameAnalyzerFrame.setVisible(true);

			selectMenuPanel("MAIN");
		}));
		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));


		buttonHolder.add(backBtn);
		buttonHolder.add(analyzeBtn);

		analyzeGamePanel.add(buttonHolder);
	}

	private void setupPuzzleSolverPanel() {

		puzzleSolverPanel.setLayout(new GridLayout(4, 1));

		JLabel solveThePuzzleLabel = new JLabel("Help the engine solve the puzzle!");
		solveThePuzzleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		solveThePuzzleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		solveThePuzzleLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 20));

		puzzleSolverPanel.add(solveThePuzzleLabel);

		JPanel engineSelectorPanel = new JPanel(new FlowLayout());

		JLabel selectEngineLabel = new JLabel("Select engine: ");
		selectEngineLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));

		engineSelectorPanel.add(selectEngineLabel);

		JComboBox<String> engineSelectorDropdown = new JComboBox<>(new Vector<>(PLAYABLE_CHESS_ENGINES.keySet().stream().toList()));
		engineSelectorDropdown.setSelectedIndex(0);
		engineSelectorPanel.add(engineSelectorDropdown);

		puzzleSolverPanel.add(engineSelectorPanel);

		JPanel fenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel fenLabel = new JLabel("FEN: ");
		fenLabel.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		fenPanel.add(fenLabel);

		JTextField fenField = new JTextField(20);
		fenField.setFont(Fonts.getRobotoFont(Font.PLAIN, 15));
		fenPanel.add(fenField);

		puzzleSolverPanel.add(fenPanel);

		JPanel buttonHolder = new JPanel(new FlowLayout());

		JButton solveBtn = new PrimaryButton("Solve", e -> SwingUtilities.invokeLater(() -> {
			PuzzleSolverFrame puzzleSolverFrame;

			try {
				puzzleSolverFrame = new PuzzleSolverFrame(fenField.getText().trim(), PLAYABLE_CHESS_ENGINES.get((String) engineSelectorDropdown.getSelectedItem()));
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}

			puzzleSolverFrame.setLocationRelativeTo(null);
			puzzleSolverFrame.setVisible(true);

			selectMenuPanel("MAIN");
		}));

		JButton backBtn = new PrimaryButton("Back", e -> selectMenuPanel("MAIN"));

		buttonHolder.add(backBtn);
		buttonHolder.add(solveBtn);

		puzzleSolverPanel.add(buttonHolder);

	}

	private void selectMenuPanel(String panelName) {

		mainPanel.setVisible(false);
		pvpGameSettingsPanel.setVisible(false);
		pveGameSettingsPanel.setVisible(false);
		settingsPanel.setVisible(false);
		analyzeGamePanel.setVisible(false);
		puzzleSolverPanel.setVisible(false);

		if (panelName.equals("MAIN"))
			mainPanel.setVisible(true);
		if (panelName.equals("SETTINGS"))
			settingsPanel.setVisible(true);
		if (panelName.equals("PVP"))
			pvpGameSettingsPanel.setVisible(true);
		if (panelName.equals("PVE"))
			pveGameSettingsPanel.setVisible(true);
		if (panelName.equals("ANALYZE"))
			analyzeGamePanel.setVisible(true);
		if (panelName.equals("PUZZLE"))
			puzzleSolverPanel.setVisible(true);

		window.setTitle(switch (panelName) {
			case "MAIN" -> "Menu";
			case "SETTINGS" -> "Settings";
			case "PVP" -> "Play against your friend!";
			case "PVE" -> "Play against the engine!";
			case "ANALYZE" -> "Analyze your game!";
			case "PUZZLE" -> "Puzzle solving!";
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
