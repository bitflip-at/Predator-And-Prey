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
package at.bitflip.predatorandprey.screens;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class MainScreenController implements Initializable {

    private Integer generation;
    private Integer predators;
    private Integer preys;

    private XYChart.Series preyData;
    private XYChart.Series predData;
    
    private Board board;
    
    @FXML
    private Label GenLabel;
    @FXML
    private Label PredLabel;
    @FXML
    private Label PreyLabel;
    
    @FXML
    private LineChart<Number, Number> chart;
    
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private Pane frame;
    
    @FXML
    private Button runBtn;
    @FXML
    private Button pauseBtn;
    @FXML
    private Button stepBtn;
    
    @FXML
    private Slider preySlider;
    @FXML
    private Slider predSlider;
    
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize(Board board) {
        generation = 0;
        predators = 0;
        preys = 0;
       
        preyData = new XYChart.Series();
        predData = new XYChart.Series();
                    
        preyData.setName("Preys");
        predData.setName("Predators");
        
        chart.getData().add(preyData);
        chart.getData().add(predData);
        
        chart.setTitle("Population");
        chart.setCreateSymbols(false);
                
        this.board = board;
    }
    
   
    public void update(){
        generation++;
        
        preys = board.getPreyCount();
        predators = board.getPredCount();
        
        preyData.getData().add(new XYChart.Data(generation, preys));
        predData.getData().add(new XYChart.Data(generation, predators));
        
        GenLabel.setText(generation.toString());
        PredLabel.setText(predators.toString());
        PreyLabel.setText(preys.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Button getRunBtn() {
        return runBtn;
    }

    public Button getPauseBtn() {
        return pauseBtn;
    }

    public Button getStepBtn() {
        return stepBtn;
    }

    public Slider getPreySlider() {
        return preySlider;
    }

    public Slider getPredSlider() {
        return predSlider;
    }

    public double getPreySpawnRate(){
        return preySlider.getValue();
    }
    
    public double getPredSpawnRate(){
        return predSlider.getValue();
    }
    
}
