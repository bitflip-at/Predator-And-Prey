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

import at.bitflip.predatorandprey.automata.Automata;
import at.bitflip.predatorandprey.tile.Tile;
import java.util.Map;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class Board {
    
    private Tile[][] board;
    private final Map<String, StackPane> boardMap;
    
    private final MainScreenController controller;
    
    private final int sizeX;
    private final int sizeY;
    
    private final Automata automata;

    
    public Board(MainScreenController controller, Map<String, StackPane> boardMap, int x, int y){
        this.controller = controller;
        this.sizeX = x;
        this.sizeY = y;
        this.boardMap = boardMap;
        
        board = new Tile[x][y];
        
        automata = new Automata(sizeX, sizeY, controller.getPreySpawnRate(), controller.getPredSpawnRate());
    }
    
    public void update(){
        board = automata.step();
        
        render();
    }
    
    private void render(){
        for(int x = 0; x < this.sizeX; x++){
            for(int y = 0; y < this.sizeY; y++){
                
                StackPane cell = boardMap.get(x + " " + y );
                cell.getStyleClass().clear();
                
                cell.setStyle(board[x][y].getStyle());
            }
        }
    }
    
    public void updateValues(double preySpawnRate, double predSpawnRate){
        automata.updateValues(preySpawnRate, predSpawnRate);
    }
    
    public int getPreyCount(){
        return automata.getPreyCount();
    }
    
    public int getPredCount(){
        return automata.getPredCount();
    }
    
}
