package com.piotrowski.graphics;


import com.piotrowski.util.Pair;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GraphicsScheisse extends JFrame {

    static BufferedImage bi;
    private final int VISUAL_STRING_FRET_OFFSET = 20;
    private final int FRET_SEPARATION = 100;
    private final int STRING_SEPARATION = 50;

    private int FRET_COUNT = 4;
    private final double STRING_GAUGE_RATIO = 1.45;
    private final int LEFT_SIDE_OFFSET = 100;
    private final int FRET_ANNOTATION_OFFSET = 50;
    private final ArrayList<Integer> fretXPositions = new ArrayList<>();
    // added in order of EADGBE
    private final ArrayList<Integer> stringYPositions = new ArrayList<>();
    private int lowestFretInTab = 24;
    private int highestFretInTab = 0;
    // added in order of EADGBE
    private final ArrayList<Pair<MARK, XYPos>> fingersPositionsOnTemplate = new ArrayList<>();

    private enum MARK {
        CIRCLE,
        X,
        ZERO
    }

    class XYPos {
        int x, y;

        public XYPos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }
    }

    public GraphicsScheisse(String chordName, List<Pair<String, String>> tabs) throws IOException {
        initUI();
        BufferedImage chordImage = prepareChord(tabs);
        ImageIO.write(chordImage, "JPEG", new File(chordName.replace("/", "_over_") + ".jpg"));
    }

    private BufferedImage prepareChord(List<Pair<String, String>> tabs) {

        int HIGH_E_STRING_WIDTH = 1;
        Double FRET_LENGTH = DoubleStream
                .iterate(HIGH_E_STRING_WIDTH, n -> n * STRING_GAUGE_RATIO)
                .limit(5)
                .reduce(Double::sum)
                .orElse(0) + 5 * STRING_SEPARATION - 2;

        for (Pair<String, String> tab : tabs) {
            if (!tab.getRight().equalsIgnoreCase("x") && !tab.getRight().equalsIgnoreCase("0")) {
                lowestFretInTab = Integer.parseInt(tab.getRight()) < lowestFretInTab ? Integer.parseInt(tab.getRight()) : lowestFretInTab;
                highestFretInTab = Integer.parseInt(tab.getRight()) > highestFretInTab ? Integer.parseInt(tab.getRight()) : highestFretInTab;
                FRET_COUNT = highestFretInTab - lowestFretInTab + 2;
            }
        }

        final int STRING_RESIDUAL = FRET_SEPARATION;
        int FRET_WIDTH = 3;
        final int STRING_LENGTH = (FRET_COUNT - 1) * (FRET_WIDTH + FRET_SEPARATION) + STRING_RESIDUAL;

        setSize(VISUAL_STRING_FRET_OFFSET * 2 + STRING_LENGTH,
                VISUAL_STRING_FRET_OFFSET * 2 + FRET_LENGTH.intValue() + FRET_ANNOTATION_OFFSET);
        BufferedImage im = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = getGraphics2D(FRET_LENGTH, STRING_LENGTH, im);

        drawFrets(FRET_SEPARATION, VISUAL_STRING_FRET_OFFSET, FRET_WIDTH, FRET_COUNT, FRET_LENGTH, g2d);
        drawStrings(STRING_SEPARATION, VISUAL_STRING_FRET_OFFSET, HIGH_E_STRING_WIDTH, STRING_GAUGE_RATIO, STRING_LENGTH, g2d);
        annotateStrings(STRING_SEPARATION, g2d);
        layTabsAndFretsNs(tabs, g2d);
        g2d.dispose();
        return im;
    }

    private void layTabsAndFretsNs(List<Pair<String, String>> tabs, Graphics2D g2d) {
        preparePositions(tabs);
        layTabs(tabs, g2d); //Circles drawing
        layFretsNs(g2d); //Writing at which fret you are
    }

    private void preparePositions(List<Pair<String, String>> tabs) {
        for (int i = 0; i < tabs.size(); ++i) {
            if (tabs.get(i).getRight().equals("0")) {

                fingersPositionsOnTemplate.add(new Pair<>(MARK.ZERO, new XYPos(2 * VISUAL_STRING_FRET_OFFSET, VISUAL_STRING_FRET_OFFSET / 2 + stringYPositions.get(5 - i))));
            } else if (tabs.get(i).getRight().equalsIgnoreCase("x")) {
                fingersPositionsOnTemplate.add(new Pair<>(MARK.X, new XYPos(2 * VISUAL_STRING_FRET_OFFSET, VISUAL_STRING_FRET_OFFSET / 2 + stringYPositions.get(5 - i))));
            } else {
                fingersPositionsOnTemplate.add(new Pair<>(MARK.CIRCLE, new XYPos(
                        VISUAL_STRING_FRET_OFFSET + LEFT_SIDE_OFFSET + (int) (STRING_SEPARATION / 1.4) + (Integer.parseInt(tabs.get(i).getRight()) - (lowestFretInTab)) * FRET_SEPARATION,
                        stringYPositions.get(5 - i) - STRING_SEPARATION / 4)));
            }
        }
    }

    private void layFretsNs(Graphics2D g2d) {
        g2d.drawString(String.valueOf(lowestFretInTab - 1), fretXPositions.get(0), stringYPositions.get(5) + (int) (FRET_ANNOTATION_OFFSET *1.3));

    }

    private void layTabs(List<Pair<String, String>> tabs, Graphics2D g2d) {
        for (Pair<MARK, XYPos> tab : fingersPositionsOnTemplate) {
            layTab(tab.getLeft(), tab.getRight().x, tab.getRight().y, g2d);
        }
    }

    private void layTab(MARK m, int xPos, int yPos, Graphics2D g2d) {
        switch (m) {
            case CIRCLE:
                g2d.fill(new Ellipse2D.Double(xPos, yPos, FRET_SEPARATION / 3.4, FRET_SEPARATION / 3.4));
                break;
            case X: {
                g2d.setFont(new Font("Verdana", Font.BOLD, 30));
                g2d.drawString("X", xPos, yPos);
                break;
            }
            case ZERO: {
                g2d.setFont(new Font("Verdana", Font.BOLD, 30));
                g2d.drawString("0", xPos, yPos);
                break;
            }
        }
    }


    private Graphics2D getGraphics2D(Double FRET_LENGTH, int STRING_LENGTH, BufferedImage im) {
        Graphics2D g2d = im.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g2d;
    }

    private void annotateStrings(int stringSeparation, Graphics2D g2d) {
        g2d.setFont(new Font("Verdana", Font.PLAIN, 30));
        char[] stringNames = new char[]{'E', 'B', 'G', 'D', 'A', 'E'};
        for (int stringN = 0; stringN < 6; ++stringN) {
            g2d.drawString(String.valueOf(stringNames[stringN]), VISUAL_STRING_FRET_OFFSET - 10, VISUAL_STRING_FRET_OFFSET + 14 + stringN * stringSeparation);
        }

    }

    private void drawFrets(int fretSeparation, int VISUAL_STRING_FRET_OFFSET, int FRET_WIDTH, int FRET_COUNT, Double FRET_LENGTH, Graphics2D g2d) {
        BasicStroke firstFretStroke = new BasicStroke(FRET_WIDTH + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setStroke(firstFretStroke);
        for (int fretN = 0; fretN < FRET_COUNT; ++fretN) {
            if (fretN > 0) {// because 0th fret stroke is drawn above (fatter than rest)
                BasicStroke fretStroke = new BasicStroke(FRET_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                g2d.setStroke(fretStroke);
            }
            int xPos = VISUAL_STRING_FRET_OFFSET + LEFT_SIDE_OFFSET + fretN * fretSeparation;
            g2d.drawLine(xPos,
                    VISUAL_STRING_FRET_OFFSET,
                    VISUAL_STRING_FRET_OFFSET + LEFT_SIDE_OFFSET + fretN * fretSeparation,
                    VISUAL_STRING_FRET_OFFSET + FRET_LENGTH.intValue()
            );
            fretXPositions.add(xPos);
        }
    }

    private void drawStrings(int stringSeparation, int VISUAL_STRING_FRET_OFFSET, int HIGH_E_STRING_WIDTH, double STRING_GAUGE_RATIO, int STRING_LENGTH, Graphics2D g2d) {
        BasicStroke stringStroke = new BasicStroke(HIGH_E_STRING_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setStroke(stringStroke);
        for (int stringN = 0; stringN < 6; ++stringN) {
            int yPos = VISUAL_STRING_FRET_OFFSET + stringN * stringSeparation;
            g2d.drawLine(VISUAL_STRING_FRET_OFFSET + LEFT_SIDE_OFFSET,
                    yPos,
                    VISUAL_STRING_FRET_OFFSET + STRING_LENGTH,
                    VISUAL_STRING_FRET_OFFSET + stringN * stringSeparation
            );
            stringYPositions.add(yPos);
            stringStroke = new BasicStroke((float) (stringStroke.getLineWidth() * STRING_GAUGE_RATIO), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            g2d.setStroke(stringStroke);
        }
    }

    private void initUI() {
        add(new Surface());
        setTitle("Basic strokes");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * This main method is created only for testing during development
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            GraphicsScheisse gs = null;
            try {
                List<Pair<String, String>> testList = new ArrayList<>(
                        Arrays.asList(
                                new Pair<>("E", "0"),
                                new Pair<>("A", "7"),
                                new Pair<>("D", "X"),
                                new Pair<>("G", "12"),
                                new Pair<>("B", "8"),
                                new Pair<>("E", "7")
                        )
                );
                gs = new GraphicsScheisse("EmM6", testList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}