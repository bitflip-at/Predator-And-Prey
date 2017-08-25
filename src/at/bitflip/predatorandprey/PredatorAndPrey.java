/*
 * The MIT License
 *
 * Copyright 2017 Emanuel Gitterle <emanuel.gitterle@bitflip.at>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package at.bitflip.predatorandprey;

import at.bitflip.predatorandprey.screens.Board;
import at.bitflip.predatorandprey.screens.MainScreenController;
import at.bitflip.predatorandprey.utils.FpsCalculator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author emanu
 */
public class PredatorAndPrey extends Application {

    private Map<String, StackPane> boardMap = new HashMap<>();
    private Board board;
    private MainScreenController controller;

    public int timer = 50;
    public boolean running = false;
    public static Integer calculatedFPS = 0;
    private static int framesRendered = 0;

    @Override
    public void start(Stage primaryStage) {

        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
            @Override
            public void handle(Event event) {
                iterateBoard();
                updateUI();
            }
        }), new KeyFrame(Duration.millis(timer)));

        timeline.setCycleCount(Timeline.INDEFINITE);

        AnchorPane root = null;
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PredatorAndPrey.class.getResource("screens/MainScreen.fxml"));
            root = (AnchorPane) loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            Logger.getLogger(PredatorAndPrey.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pane frame = null;

        ObservableList<Node> children = root.getChildren();
        for (Node node : children) {
            if (node instanceof Pane && !(node instanceof GridPane)) {
                frame = (Pane) node;
            }
        }

        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 40; y++) {
                StackPane cell = StackPaneBuilder.create().layoutX(x * 10).layoutY(y * 10).prefHeight(10).prefWidth(10).styleClass("frame.css").build();

                frame.getChildren().add(cell);

                boardMap.put(x + " " + y, cell);
            }
        }

        board = new Board(controller, boardMap, 50, 40);

        controller.initialize(board, this);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Predator and Prey");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            FpsCalculator.kill();
        });
        primaryStage.show();
        board.update();
        timeline.play();
    }

    public void iterateBoard() {
        if (running) {
            board.update();
            framesRendered++;
        }
    }

    public void updateUI() {
        controller.update();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Thread thread = new Thread(new FpsCalculator());
        thread.start();

        launch(args);
    }

    public static synchronized int getFramesCount() {
        return framesRendered;
    }

    public static synchronized void setFramesCount(int value) {
        framesRendered = value;
    }

    public static void setCalculatedFps(int value) {
        calculatedFPS = value;
    }

}
