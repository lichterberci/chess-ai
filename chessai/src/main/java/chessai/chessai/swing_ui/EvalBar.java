package chessai.chessai.swing_ui;

import javax.swing.*;
import java.awt.*;

/**
 * Represents an evaluation bar (showing the current advantage of either side on a black-white slider)
 */
public class EvalBar extends JPanel {
    private int eval = 0;

    public void setEval(int eval) {
        this.eval = eval;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        float ratio;

        if (eval < Integer.MIN_VALUE + 20)
            ratio = 0.0f;
        else if (eval > Integer.MAX_VALUE - 10)
            ratio = 1.0f;
        else
            ratio = Math.min(Math.max((eval / 1000.0f + 0.5f), 0.05f), 0.95f);

        int heightOfBlack = (int) (getHeight() * (1.0f - ratio));

        g.setColor(Color.WHITE);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.fillRect(getX(), getY(), getWidth(), heightOfBlack);

    }
}
