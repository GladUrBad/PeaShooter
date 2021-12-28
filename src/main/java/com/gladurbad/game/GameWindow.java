package com.gladurbad.game;

import com.gladurbad.game.loop.GameLoop;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public static final int HEIGHT = 600;
    public static final int WIDTH = 600;

    public GameWindow() {
        GameLoop loop = new GameLoop();

        add(loop);

        setTitle("Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(GameWindow.WIDTH, GameWindow.HEIGHT));
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
