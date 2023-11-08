package chessai.chessai.swing_ui;

import chessai.chessai.lib.GameState;

import javax.swing.*;
import java.awt.*;

public class GameEndedDialog extends JDialog {

	public GameEndedDialog(Frame owner, GameState resultState) {

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

		JButton backToBoardBtn = new JButton("Back to board");
		backToBoardBtn.addActionListener(e -> this.dispose());
		backToBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		JButton closeBoardBtn = new JButton("Close board");
		closeBoardBtn.addActionListener(e -> {
			this.dispose();
			owner.dispose();
		});
		closeBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		buttonHolderPanel.add(backToBoardBtn);
		buttonHolderPanel.add(closeBoardBtn);

		this.add(buttonHolderPanel);

		this.pack();

		this.setLocationRelativeTo(null);
	}

}
