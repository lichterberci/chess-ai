package chessai.chessai.swing_ui;

import javax.swing.*;
import java.awt.*;

public class EvalBar extends JPanel {

    private int eval = 0;

    public void setEval(int eval) {
        this.eval = eval;
    }

    @Override
    public void paint(Graphics g) {

        float ratio;

        if (eval < Integer.MIN_VALUE + 20)
            ratio = 0.0f;
        else if (eval > Integer.MAX_VALUE - 10)
            ratio = 1.0f;
        else
            ratio = Math.max(Math.min((eval / 1000.0f) + 0.5f, 0.05f), 0.95f);

        int heightOfWhite = (int) (this.getHeight() * ratio);

        g.setColor(Color.BLACK);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), heightOfWhite);
    }
}
