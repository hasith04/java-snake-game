package com.jai.snakegame;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Snake extends JFrame {

    public Snake() {
        initUI();
    }

    private void initUI() {
        add(new Board());
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Snake::new);
    }
}