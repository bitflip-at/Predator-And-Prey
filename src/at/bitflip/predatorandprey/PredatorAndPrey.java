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
    
    @Override
    public void start(Stage primaryStage) {
        
        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
            @Override
            public void handle(Event event) {
               iterateBoard();
               updateUI();
            }
        }), new KeyFrame(Duration.millis(1000)));
        
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
        for(Node node : children){
            if(node instanceof Pane && !(node instanceof GridPane) ){
                frame = (Pane)node;  
            }
        }
        
        for(int x = 0; x < 50; x++){
            for(int y = 0; y < 40; y++){
                StackPane cell = StackPaneBuilder.create().layoutX(x*10).layoutY(y*10).prefHeight(10).prefWidth(10).styleClass("frame.css").build();
                
                             
                frame.getChildren().add(cell);
                
                boardMap.put(x + " " + y, cell);
            }
        }
        
        board = new Board(frame, boardMap, 50, 40);
        
        board.initialize(0.4, 0.2);
        controller.initialize(board);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Predator and Prey");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        timeline.play();
    }
    
    public void iterateBoard(){
        board.update();
    }
    
    public void updateUI(){
        controller.update();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
