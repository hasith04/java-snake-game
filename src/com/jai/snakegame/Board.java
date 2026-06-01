package com.jai.snakegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int DOT_SIZE = 20;
    private static final int ALL_DOTS = (BOARD_WIDTH / DOT_SIZE) * (BOARD_HEIGHT / DOT_SIZE);
    private static final int DELAY = 100;

    private final Deque<Point> snake = new ArrayDeque<>();
    private Point apple;
    private Direction direction = Direction.RIGHT;
    private boolean running;
    private Timer timer;
    private final Random random = new Random();

    public Board() {
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new InputAdapter());
        startGame();
    }

    private void startGame() {
        if (timer != null) {
            timer.stop();
        }

        snake.clear();
        snake.add(new Point(100, 100));
        snake.add(new Point(80, 100));
        snake.add(new Point(60, 100));

        direction = Direction.RIGHT;
        running = true;
        placeApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        if (!running) {
            drawGameOver(g);
            return;
        }

        g.setColor(Color.RED);
        g.fillOval(apple.x, apple.y, DOT_SIZE, DOT_SIZE);

        boolean head = true;
        for (Point point : snake) {
            g.setColor(head ? Color.GREEN : new Color(45, 180, 0));
            g.fillRect(point.x, point.y, DOT_SIZE, DOT_SIZE);
            head = false;
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawGameOver(Graphics g) {
        String msg = "GAME OVER";
        Font font = new Font("Arial", Font.BOLD, 40);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (BOARD_WIDTH - metrics.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);

        String restart = "Press ENTER to restart";
        Font small = new Font("Arial", Font.PLAIN, 18);
        g.setFont(small);
        g.drawString(restart, (BOARD_WIDTH - getFontMetrics(small).stringWidth(restart)) / 2,
                BOARD_HEIGHT / 2 + 40);
    }

    private void placeApple() {
        int x = random.nextInt(BOARD_WIDTH / DOT_SIZE) * DOT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / DOT_SIZE) * DOT_SIZE;
        apple = new Point(x, y);

        while (snake.contains(apple)) {
            x = random.nextInt(BOARD_WIDTH / DOT_SIZE) * DOT_SIZE;
            y = random.nextInt(BOARD_HEIGHT / DOT_SIZE) * DOT_SIZE;
            apple.setLocation(x, y);
        }
    }

    private void move() {
        Point head = snake.peekFirst();
        Point next = new Point(head);

        switch (direction) {
            case LEFT:
                next.translate(-DOT_SIZE, 0);
                break;
            case RIGHT:
                next.translate(DOT_SIZE, 0);
                break;
            case UP:
                next.translate(0, -DOT_SIZE);
                break;
            case DOWN:
                next.translate(0, DOT_SIZE);
                break;
        }

        snake.addFirst(next);

        if (next.equals(apple)) {
            placeApple();
        } else {
            snake.removeLast();
        }
    }

    private void checkCollision() {
        Point head = snake.peekFirst();

        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < 0 || head.y >= BOARD_HEIGHT) {
            running = false;
            return;
        }

        int index = 0;
        for (Point point : snake) {
            if (index > 0 && head.equals(point)) {
                running = false;
                break;
            }
            index++;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();

            if (!running && timer != null) {
                timer.stop();
            }
        }
        repaint();
    }

    private class InputAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (running) {
                if (key == KeyEvent.VK_LEFT && direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                } else if (key == KeyEvent.VK_RIGHT && direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                } else if (key == KeyEvent.VK_UP && direction != Direction.DOWN) {
                    direction = Direction.UP;
                } else if (key == KeyEvent.VK_DOWN && direction != Direction.UP) {
                    direction = Direction.DOWN;
                }
            } else if (key == KeyEvent.VK_ENTER) {
                startGame();
            }
        }
    }

    private enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
