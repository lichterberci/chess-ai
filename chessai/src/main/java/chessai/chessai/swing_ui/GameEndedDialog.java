package chessai.chessai.swing_ui;

import chessai.chessai.lib.GameState;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameEndedDialog extends JDialog {

	public GameEndedDialog(Frame owner, GameState resultState, String pgnString) {

		super(owner, "Game ended", true);

		this.setModalityType(Dialog.ModalityType.MODELESS);
		this.setLayout(new GridLayout(2, 1, 0, 0));

		String message = switch (resultState) {
			case WHITE_WIN -> "White won!";
			case BLACK_WIN -> "Black won!";
			case DRAW -> "Draw!";
			case PLAYING -> throw new IllegalStateException("State cannot be playing!");
		};


		JLabel messageLabel = new JLabel(message);

		messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		messageLabel.setVerticalTextPosition(SwingConstants.CENTER);
		messageLabel.setVerticalAlignment(SwingConstants.CENTER);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.add(messageLabel);


		JPanel buttonHolderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

		JButton backToBoardBtn = new PrimaryButton("Back to board", e -> this.dispose());
		backToBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		JButton copyPgnToClipboardBtn = new PrimaryButton("Copy PGN to clipboard", e -> {
			var selection = new StringSelection(pgnString);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		});
		copyPgnToClipboardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		JButton savePgnBtn = new PrimaryButton("Save PGN to file", e -> {

			var fileChooser = new JFileChooser();
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("PGN files", "pgn", "txt"));
			int fileChoosingResult = fileChooser.showSaveDialog(this);

			if (fileChoosingResult == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				try {
					if (!file.exists()) {
						boolean wasFileSuccessfullyCreated = file.createNewFile();

						if (!wasFileSuccessfullyCreated)
							System.err.printf("Could not create file [path=%s]%n", file.getPath());
					}
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}

				try (var fw = new FileWriter(file)) {
					fw.write(pgnString);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		savePgnBtn.setHorizontalAlignment(SwingConstants.CENTER);

		JButton closeBoardBtn = new PrimaryButton("Close board", e -> {
			this.dispose();
			owner.dispose();
		});
		closeBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		buttonHolderPanel.add(backToBoardBtn);
		buttonHolderPanel.add(copyPgnToClipboardBtn);
		buttonHolderPanel.add(savePgnBtn);
		buttonHolderPanel.add(closeBoardBtn);

		this.add(buttonHolderPanel);

		this.pack();

		this.setLocationRelativeTo(null);
	}

}
