import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import java.util.Random;

public class TableTennis {
    private final int WIDTH, HEIGHT;
    private final double netWidth = 10;
    private final double holeHeight = 70;
    private double holeTop;
    private double ballSpeedX = 2;
    private double ballSpeedY = 2;
    private final int paddleWidth = 10, paddleHeight = 100;
    private double paddle1Y, paddle2Y;
    private double ballX, ballY;
    private final double paddleSpeed = 5;
    private int player1Score = 0, player2Score = 0;
    private boolean wPressed, sPressed, upPressed, downPressed;
    private GraphicsContext gc;

    // Apple variables
    private double appleX, appleY;
    private boolean appleVisible = false;
    private final Random random = new Random();

    private AnimationTimer timer;

    public TableTennis(int width, int height, GraphicsContext gc, Scene scene) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.gc = gc;

        this.holeTop = (HEIGHT - holeHeight) / 2;
        paddle1Y = (HEIGHT - paddleHeight) / 2;
        paddle2Y = (HEIGHT - paddleHeight) / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;

        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode(), true));
        scene.setOnKeyReleased(e -> handleKeyPress(e.getCode(), false));
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
                checkWinCondition();
            }
        };
        timer.start();
    }

    private void handleKeyPress(KeyCode code, boolean isPressed) {
        switch (code) {
            case W -> wPressed = isPressed;
            case S -> sPressed = isPressed;
            case UP -> upPressed = isPressed;
            case DOWN -> downPressed = isPressed;
        }
    }

    private void update() {
        if (wPressed && paddle1Y > 0) paddle1Y -= paddleSpeed;
        if (sPressed && paddle1Y < HEIGHT - paddleHeight) paddle1Y += paddleSpeed;
        if (upPressed && paddle2Y > 0) paddle2Y -= paddleSpeed;
        if (downPressed && paddle2Y < HEIGHT - paddleHeight) paddle2Y += paddleSpeed;

        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballY <= 0 || ballY >= HEIGHT) ballSpeedY *= -1;

        if (ballX <= paddleWidth && ballY >= paddle1Y && ballY <= paddle1Y + paddleHeight) {
            ballSpeedX *= -1;
            double relativeIntersect = (ballY - (paddle1Y + paddleHeight / 2)) / (paddleHeight / 2);
            ballSpeedY = relativeIntersect * 2;
            ballSpeedY = Math.max(-3, Math.min(ballSpeedY, 3));
            ballSpeedX = Math.max(-5, Math.min(ballSpeedX * 1.05, 5));
        }

        if (ballX >= WIDTH - paddleWidth - 10 && ballY >= paddle2Y && ballY <= paddle2Y + paddleHeight) {
            ballSpeedX *= -1;
            double relativeIntersect = (ballY - (paddle2Y + paddleHeight / 2)) / (paddleHeight / 2);
            ballSpeedY = relativeIntersect * 2;
            ballSpeedY = Math.max(-3, Math.min(ballSpeedY, 3));
            ballSpeedX = Math.max(-5, Math.min(ballSpeedX * 1.05, 5));
        }

        if (ballX >= (WIDTH / 2 - netWidth / 2) && ballX <= (WIDTH / 2 + netWidth / 2)) {
            if (ballY < holeTop || ballY > holeTop + holeHeight) {
                ballSpeedX *= -1;
            }
        }

        if (appleVisible && Math.abs(ballX - appleX) < 15 && Math.abs(ballY - appleY) < 15) {
            appleVisible = false;
            if (player1Score > player2Score) {
                player1Score = Math.max(0, player1Score - 1);
            } else if (player2Score > player1Score) {
                player2Score = Math.max(0, player2Score - 1);
            }
        }

        if (ballX <= 0) {
            player2Score++;
            resetBall();
            spawnApple();
        }
        if (ballX >= WIDTH) {
            player1Score++;
            resetBall();
            spawnApple();
        }
    }

    private void resetBall() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = 2 * (Math.random() > 0.5 ? 1 : -1);
        ballSpeedY = 2 * (Math.random() > 0.5 ? 1 : -1);
    }

    private void spawnApple() {
        if (random.nextDouble() < 0.2) {
            appleX = random.nextInt(WIDTH - 30) + 15;
            appleY = random.nextInt(HEIGHT - 30) + 15;
            appleVisible = true;
        }
    }

    private void checkWinCondition() {
        if (player1Score >= 15 && player1Score >= player2Score + 2) {
            stopGame("Player 1 Wins!");
        } else if (player2Score >= 15 && player2Score >= player1Score + 2) {
            stopGame("Player 2 Wins!");
        }
    }

    private void stopGame(String message) {
        timer.stop();

        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);
        gc.fillText(message, WIDTH / 2 - 50, HEIGHT / 2 - 20);
        gc.fillText("Final Scores:", WIDTH / 2 - 50, HEIGHT / 2);
        gc.fillText("Player 1: " + player1Score, WIDTH / 2 - 50, HEIGHT / 2 + 20);
        gc.fillText("Player 2: " + player2Score, WIDTH / 2 - 50, HEIGHT / 2 + 40);
        gc.fillText("Press R to Restart or Q to Quit", WIDTH / 2 - 100, HEIGHT / 2 + 80);

        gc.getCanvas().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R -> restartGame();
                case Q -> System.exit(0);
            }
        });
    }

    private void restartGame() {
        player1Score = 0;
        player2Score = 0;
        resetBall();
        paddle1Y = (HEIGHT - paddleHeight) / 2;
        paddle2Y = (HEIGHT - paddleHeight) / 2;
        start();
    }

    private void draw() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);

        gc.fillRect(0, paddle1Y, paddleWidth, paddleHeight);
        gc.fillRect(WIDTH - paddleWidth - 10, paddle2Y, paddleWidth, paddleHeight);

        gc.fillOval(ballX, ballY, 10, 10);

        gc.fillRect(WIDTH / 2 - netWidth / 2, 0, netWidth, holeTop);
        gc.fillRect(WIDTH / 2 - netWidth / 2, holeTop + holeHeight, netWidth, HEIGHT - (holeTop + holeHeight));

        if (appleVisible) {
            gc.setFill(Color.RED);
            gc.fillOval(appleX, appleY, 15, 15);
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Player 1: " + player1Score, 50, 50);
        gc.fillText("Player 2: " + player2Score, WIDTH - 150, 50);
    }
}




