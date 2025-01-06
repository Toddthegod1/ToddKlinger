import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TableTennisGame extends Application {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new javafx.scene.Group(canvas), WIDTH, HEIGHT, Color.BLACK);

        stage.setTitle("Table Tennis Game");
        stage.setScene(scene);
        stage.show();

        TableTennis game = new TableTennis(WIDTH, HEIGHT, gc, scene);
        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
