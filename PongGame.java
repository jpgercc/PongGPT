import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PongGame extends JPanel implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 20;
    private static final int PADDLE_HEIGHT = 80;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 5;
    private static final int BALL_SPEED = 3;

    private int paddle1Y;
    private int paddle2Y;
    private int ballX;
    private int ballY;
    private int ballXSpeed;
    private int ballYSpeed;
    private int score1;
    private int score2;
    private int winningScore;
    private boolean isGameRunning;
    private boolean isChoosingMode;
    private boolean isChoosingPoints;
    private boolean isGameOver;
    private boolean isAIEnabled;

    public PongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();
    }

    private void initializeGame() {
        paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXSpeed = BALL_SPEED;
        ballYSpeed = BALL_SPEED;
        score1 = 0;
        score2 = 0;
        winningScore = 3;
        isGameRunning = false;
        isChoosingMode = true;
        isChoosingPoints = false;
        isGameOver = false;
        isAIEnabled = true;
    }

    public void update() {
        if (isGameRunning) {
            movePaddles();

            if (isAIEnabled) {
                moveAIPaddle();
            }

            moveBall();
            checkCollision();
            checkGameOver();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isChoosingMode) {
            drawChoosingModeScreen(g);
        } else if (isChoosingPoints) {
            drawChoosingPointsScreen(g);
        } else if (isGameOver) {
            drawGameOverScreen(g);
        } else {
            drawGame(g);
        }
    }

    private void drawChoosingModeScreen(Graphics g) {
        String message = "Escolha o modo de jogo:";
        String option1 = "1 - Jogar com amigo";
        String option2 = "2 - Jogar com IA";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.BOLD, 24));
        FontMetrics fontMetrics = g.getFontMetrics();
        int messageWidth = fontMetrics.stringWidth(message);
        //int optionWidth = fontMetrics.stringWidth(option1);
        int x = WIDTH / 2 - messageWidth / 2;
        int y = HEIGHT / 2 - fontMetrics.getHeight();

        g.drawString(message, x, y);
        g.setFont(new Font("Verdana", Font.PLAIN, 20));
        fontMetrics = g.getFontMetrics();
        messageWidth = fontMetrics.stringWidth(option1);
        x = WIDTH / 2 - messageWidth / 2;
        y += fontMetrics.getHeight() * 2;

        g.drawString(option1, x, y);
        g.drawString(option2, x, y + fontMetrics.getHeight() * 2);
    }

    private void drawChoosingPointsScreen(Graphics g) {
        String message = "Escolha a dificuldade:";
        String option1 = "3 - Facil (3 pontos)";
        String option2 = "5 - Medio (5 pontos)";
        String option3 = "7 - Dificil (7 pontos)";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.BOLD, 24));
        FontMetrics fontMetrics = g.getFontMetrics();
        int messageWidth = fontMetrics.stringWidth(message);
        //int optionWidth = fontMetrics.stringWidth(option1);
        int x = WIDTH / 2 - messageWidth / 2;
        int y = HEIGHT / 2 - fontMetrics.getHeight();

        g.drawString(message, x, y);
        g.setFont(new Font("Verdana", Font.PLAIN, 20));
        fontMetrics = g.getFontMetrics();
        messageWidth = fontMetrics.stringWidth(option1);
        x = WIDTH / 2 - messageWidth / 2;
        y += fontMetrics.getHeight() * 2;

        g.drawString(option1, x, y);
        g.drawString(option2, x, y + fontMetrics.getHeight() * 2);
        g.drawString(option3, x, y + fontMetrics.getHeight() * 4);
    }

    private void drawGameOverScreen(Graphics g) {
        String message = "Game Over!";
        String winnerMessage = score1 > score2 ? "Player 1 Venceu!" : "Player 2 Venceu!";
        String restartMessage = "Pressione SPACE para reiniciar";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.BOLD, 24));
        FontMetrics fontMetrics = g.getFontMetrics();
        int messageWidth = fontMetrics.stringWidth(message);
        int winnerMessageWidth = fontMetrics.stringWidth(winnerMessage);
        int restartMessageWidth = fontMetrics.stringWidth(restartMessage);
        int x = WIDTH / 2 - messageWidth / 2;
        int y = HEIGHT / 2 - fontMetrics.getHeight();

        g.drawString(message, x, y);
        g.setFont(new Font("Verdana", Font.PLAIN, 20));
        fontMetrics = g.getFontMetrics();
        winnerMessageWidth = fontMetrics.stringWidth(winnerMessage);
        restartMessageWidth = fontMetrics.stringWidth(restartMessage);
        x = WIDTH / 2 - winnerMessageWidth / 2;
        y += fontMetrics.getHeight() * 2;

        g.drawString(winnerMessage, x, y);
        g.drawString(restartMessage, WIDTH / 2 - restartMessageWidth / 2, y + fontMetrics.getHeight() * 2);
    }

    private void drawGame(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.PLAIN, 24));
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        g.setFont(new Font("Verdana", Font.BOLD, 20));
        g.drawString("Player 1", 50, 50);
        g.drawString("Player 2", WIDTH - 120, 50);
        g.drawString(Integer.toString(score1), 50, 80);
        g.drawString(Integer.toString(score2), WIDTH - 50, 80);

        g.fillRect(0, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    public void movePaddles() {
        if (KeyState.isKeyPressed(KeyEvent.VK_W) && paddle1Y > 0) {
            paddle1Y -= PADDLE_SPEED;
        }

        if (KeyState.isKeyPressed(KeyEvent.VK_S) && paddle1Y + PADDLE_HEIGHT < HEIGHT) {
            paddle1Y += PADDLE_SPEED;
        }

        if (!isAIEnabled) {
            if (KeyState.isKeyPressed(KeyEvent.VK_UP) && paddle2Y > 0) {
                paddle2Y -= PADDLE_SPEED;
            }

            if (KeyState.isKeyPressed(KeyEvent.VK_DOWN) && paddle2Y + PADDLE_HEIGHT < HEIGHT) {
                paddle2Y += PADDLE_SPEED;
            }
        }
    }

    public void moveAIPaddle() {
        int paddleCenter = paddle2Y + PADDLE_HEIGHT / 2;
        int ballCenter = ballY + BALL_SIZE / 2;

        if (paddleCenter < ballCenter && paddle2Y + PADDLE_HEIGHT < HEIGHT) {
            paddle2Y += PADDLE_SPEED;
        } else if (paddleCenter > ballCenter && paddle2Y > 0) {
            paddle2Y -= PADDLE_SPEED;
        }
    }

    public void moveBall() {
        ballX += ballXSpeed;
        ballY += ballYSpeed;

        if (ballY <= 0 || ballY + BALL_SIZE >= HEIGHT) {
            ballYSpeed = -ballYSpeed;
        }
    }

    public void checkCollision() {
        if (ballX <= PADDLE_WIDTH && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballXSpeed = -ballXSpeed;
            increaseBallSpeed();
        } else if (ballX + BALL_SIZE >= WIDTH - PADDLE_WIDTH && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballXSpeed = -ballXSpeed;
            increaseBallSpeed();
        }

        if (ballX <= 0) {
            score2++;
            resetBall();
        } else if (ballX + BALL_SIZE >= WIDTH) {
            score1++;
            resetBall();
        }
    }

    public void increaseBallSpeed() {
        ballXSpeed = (int) (ballXSpeed * 1.35); //antigamente tava em 1.05
        ballYSpeed = (int) (ballYSpeed * 1.35); //antigamente tava em 1.05
    }

    public void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXSpeed = BALL_SPEED;
        ballYSpeed = BALL_SPEED;
    }

    public void checkGameOver() {
        if (score1 >= winningScore || score2 >= winningScore) {
            isGameOver = true;
            isGameRunning = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        KeyState.setKeyPressed(e.getKeyCode(), true);

        if (isChoosingMode) {
            if (e.getKeyChar() == '1') {
                isAIEnabled = false;
                isChoosingMode = false;
                isChoosingPoints = true;
            } else if (e.getKeyChar() == '2') {
                isAIEnabled = true;
                isChoosingMode = false;
                isChoosingPoints = true;
            }
        } else if (isChoosingPoints) {
            if (e.getKeyChar() == '3') {
                winningScore = 3;
                startGame();
            } else if (e.getKeyChar() == '5') {
                winningScore = 5;
                startGame();
            } else if (e.getKeyChar() == '7') {
                winningScore = 7;
                startGame();
            }
        } else if (isGameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            initializeGame();
            startGame();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    private void startGame() {
        isGameRunning = true;
        isChoosingPoints = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        KeyState.setKeyPressed(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        PongGame pongGame = new PongGame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(pongGame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(10, e -> {
            pongGame.update();
            pongGame.repaint();
        });
        timer.start();
    }
}

class KeyState {
    private static final boolean[] keys = new boolean[65536];

    public static synchronized void setKeyPressed(int keyCode, boolean pressed) {
        keys[keyCode] = pressed;
    }

    public static synchronized boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }
}
