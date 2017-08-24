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

import at.bitflip.predatorandprey.tile.Predator;
import at.bitflip.predatorandprey.tile.Prey;
import at.bitflip.predatorandprey.tile.Tile;
import at.bitflip.predatorandprey.utils.Pair;
import java.util.Map;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class Board {
    
    private Tile[][] board;
    private Map<String, StackPane> boardMap;
    
    private Pane frame;
    private Integer x;
    private Integer y;
    
    
    public Board(Pane frame, Map<String, StackPane> boardMap, Integer x, Integer y){
        this.frame = frame;
        this.x = x;
        this.y = y;
        this.boardMap = boardMap;
        
        board = new Tile[x][y];
    }
    
    /**
     * 
     * @param preyRate rate for Pray to spawn 0.0 - 1.0
     * @param predRate rate for Predator to spawn 0.0-1.0
     */
    public void initialize(double preyRate, double predRate){
        
        Random rand = new Random();
        
        for(int x = 0; x < 50; x++){
            for(int y = 0; y < 40; y++){
                StackPane cell = boardMap.get(x + " " + y );
                cell.getStyleClass().clear();
                
                double r = rand.nextDouble()*2;
                
                if(r <= preyRate){
                    Prey prey = new Prey();
                    board[x][y] = prey;
                    cell.setStyle(prey.getStyle());
                } else if(r >= 2.0-predRate ) {
                    Predator pred = new Predator();
                    board[x][y] = pred;
                    cell.setStyle(pred.getStyle());
                } else {
                    Tile tile = new Tile();
                    board[x][y] = tile;
                    cell.setStyle(tile.getStyle());
                } 
            }
        }
    }
    
    public void update(){
        
        Tile[][] tmp = new Tile[this.x][this.y];
        
        //make temporary new field
        for(int x = 0; x < this.x; x++ ){
            for(int y = 0; y < this.y; y++){
                tmp[x][y] = new Tile();
            }    
        }
        
        //calculate movements + update creatures
        for(int x = 0; x < this.x; x++ ){
            for(int y = 0; y < this.y; y++){
                
                Tile tile = board[x][y];
                Pair<Integer> position = new Pair(x, y);
                
                if(tile instanceof Prey){
                    ((Prey) tile).update();
                    
                    position = move(x, y);
                }else if(tile instanceof Predator){
                    ((Predator)tile).update();
                    
                    position = move(x, y);
                }                   
                
                if(tile instanceof Prey){
                    
                    //would collide with other Prey
                    if(tmp[position.getX()][position.getY()] instanceof Prey ){
                        
                        //TODO: maybe find nearest free tile?
                        tmp[x][y] = tile;
                    }else{
                        tmp[position.getX()][position.getY()] = tile;
                    }
                   
                }else if(tile instanceof Predator){
                    
                    //would collide with other Predator
                    if(tmp[position.getX()][position.getY()] instanceof Predator ){
                        
                        //TODO: maybe find nearest free tile?
                        tmp[x][y] = tile;
                    }else{
                        tmp[position.getX()][position.getY()] = tile;
                    }
                }
                                
            }
        }
        
        for(int x = 0; x < this.x; x++ ){
            for(int y = 0; y < this.y; y++){
                
              //  if(board[x][y] instanceof Prey){
                    
               // }else if(board[x][y] instanceof Predator){
                    
               // }else {
                    board[x][y] = tmp[x][y];
               // }
                
            }
        }
        
        render();
    }
    
    private void render(){
        for(int x = 0; x < this.x; x++){
            for(int y = 0; y < this.y; y++){
                
                StackPane cell = boardMap.get(x + " " + y );
                cell.getStyleClass().clear();
                
                cell.setStyle(board[x][y].getStyle());
            }
        }
    }
    
    
    
    private Pair<Integer> normalize(Pair pair){
        pair.setX(((int)pair.getX() + 50)%50);
        pair.setY(((int)pair.getY() + 40)%40);
        
        return pair;
    }
    
    private Pair move(int x, int y){
        Random rand = new Random();
        Pair<Integer> pair = new Pair(x, y);
        
        double d = rand.nextDouble();
        if(d < 0.33){
            pair.setX(x - 1);
        }else if(d > 0.66){
            pair.setX(x + 1);
        }
        
        d = rand.nextDouble();
        if(d < 0.33){
            pair.setY(y - 1);
        }else if(d > 0.66){
            pair.setY(y + 1);
        }
        
        return normalize(pair);
    }
    
    public Integer getP(boolean prey){
        
        int iprey = 0;
        int pred = 0;
        
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                if(board[i][j] instanceof Prey){
                    iprey++;
                }else if(board[i][j] instanceof Predator){
                    pred++;
                }
            }
        }
        Pair<Integer> pair = new Pair(prey,pred);
        
        if(prey)
            return iprey;
        
        return pred;
    }
    
}
