package com.github.deknil;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * The Canvas class is a JPanel component for rendering graphics.
 * It allows you to set custom rendering logic via the Consumer<Graphics> interface.
 * When setting the rendering logic and subsequently calling the repaint() method, the component will be redrawn
 * using established logic.
 */
public class Canvas extends JPanel {
    private Consumer<Graphics> drawLogic;

    /**
     * Default constructor that initializes rendering logic with an empty function.
     */
    public Canvas() {
        this.drawLogic = g -> {};
    }

    /**
     * Sets custom rendering logic.
     * @param drawLogic A function that accepts a Graphics object to draw.
     */
    public void setDrawLogic(Consumer<Graphics> drawLogic) {
        this.drawLogic = drawLogic;
        repaint();
    }

    /**
     * Overridden component rendering method.
     * @param g The Graphics object to draw.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLogic.accept(g);
    }
}

