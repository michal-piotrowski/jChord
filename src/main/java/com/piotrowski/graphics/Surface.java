package com.piotrowski.graphics;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Michał Piotrowski on 2018-08-22.
 */
class Surface extends JPanel {

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.drawLine(20, 40, 250, 40);

        BasicStroke bs1 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2d.setStroke(bs1);
        g2d.drawLine(20, 80, 250, 80);

        g2d.dispose();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}

