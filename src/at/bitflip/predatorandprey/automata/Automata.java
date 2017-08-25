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
package at.bitflip.predatorandprey.automata;

import at.bitflip.predatorandprey.automata.creatures.Creature;
import at.bitflip.predatorandprey.automata.creatures.Predator;
import at.bitflip.predatorandprey.automata.creatures.Prey;
import at.bitflip.predatorandprey.tile.Tile;
import at.bitflip.predatorandprey.tile.TileType;
import java.util.Random;

/**
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class Automata {
    
    private final int sizeX;
    private final int sizeY;
    
    private int preyCount;
    private int predCount;
    private int generation;
    
    private double preySpawnRate;
    private double predSpawnRate;
    
    private Creature[][] field;
    
    public Automata(int sizeX, int sizeY, double preySpawnRate, double predSpawnRate){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.preySpawnRate = preySpawnRate;
        this.predSpawnRate = predSpawnRate;
       
        initializeField();
        
        generation = 0;
    }
    
    public Tile[][] step(){
        
        updateField();
        
        return Field2Tile();
    }

    public void updateValues(double preySpawnRate, double predSpawnRate){
        this.preySpawnRate = preySpawnRate;
        this.predSpawnRate = predSpawnRate;
    }
    
    public int getPreyCount() {
        return preyCount;
    }

    public void setPreyCount(int preyCount) {
        this.preyCount = preyCount;
    }

    public int getPredCount() {
        return predCount;
    }

    public void setPredCount(int predCount) {
        this.predCount = predCount;
    }
    
    public int getGeneration(){
        return generation;
    }
    
    private void initializeField(){
        field = new Creature[sizeX][sizeY];
        
        Random rand = new Random(System.nanoTime());
        
        for(int i = 0; i < sizeX; i++){
            for(int j = 0; j < sizeY; j++){
                double r = rand.nextDouble() * 200;
                
                if(r <= preySpawnRate){
                    field[i][j] = new Prey();
                }else if(r >= (200.0 - predSpawnRate)){
                    field[i][j] = new Predator();
                }else{
                    field[i][j] = null;
                }
            }
        }
        
    }
    
    private void updateField(){
        
    }
    
    private Tile[][] Field2Tile(){
               Tile[][] tile = new Tile[sizeX][sizeY];
        
        for(int i = 0; i < sizeX; i++){
            for(int j = 0; j < sizeY; j++){
                Tile t;
                
                if(field[i][j] instanceof Prey){
                    t = new Tile(TileType.PREY);
                }else if(field[i][j] instanceof Predator){
                    t = new Tile(TileType.PREDATOR);
                }else{
                    t = new Tile(TileType.EMPTY);
                }
                
                tile[i][j] = t;
            }
        }
        
        return tile;
    }
    
}
