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

import at.bitflip.predatorandprey.tile.Tile;
import at.bitflip.predatorandprey.tile.TileType;
import at.bitflip.predatorandprey.utils.Pair;
import java.util.Random;

/**
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class Automata {

    private static final int STARTHP = 100;
    private static final int PREYPTURN = 10;
    private static final int PREDPTURN = -5;
    private static final int REPHP = 100;
    private static final double PREYSPAWNRATE = 0.80;
    private static final double PREDSPAWNRATE = 0.30;

    private final int sizeX;
    private final int sizeY;

    private int preyCount;
    private int predCount;
    private int generation;

    private double preyBirthRate;
    private double predBirthRate;

    private TileType[][] field;
    private int[][] health;

    public Automata(int sizeX, int sizeY, double preySpawnRate, double predSpawnRate) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.preyBirthRate = preySpawnRate;
        this.predBirthRate = predSpawnRate;

        preyCount = 0;
        predCount = 0;
        generation = -1;

        initializeField();
    }

    public Tile[][] step() {

        updateField();
        return Field2Tile();
    }

    public void updateValues(double preySpawnRate, double predSpawnRate) {
        this.preyBirthRate = preySpawnRate;
        this.predBirthRate = predSpawnRate;
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

    public int getGeneration() {
        return generation;
    }

    private void initializeField() {
        field = new TileType[sizeX][sizeY];
        health = new int[sizeX][sizeY];

        Random rand = new Random(System.nanoTime());

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                double r = rand.nextDouble() * 200;

                if (r <= PREYSPAWNRATE) {
                    field[i][j] = TileType.PREY;
                    health[i][j] = STARTHP;
                    preyCount++;
                } else if (r >= (200.0 - PREDSPAWNRATE)) {
                    field[i][j] = TileType.PREDATOR;
                    health[i][j] = STARTHP;
                    predCount++;
                } else {
                    field[i][j] = TileType.EMPTY;
                    health[i][j] = -1;
                }
            }
        }

    }

    private void updateField() {

        generation++;

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                TileType type = field[i][j];
                int hp = health[i][j];

                Pair Pos = move(i, j);
                int x = (int) Pos.getX();
                int y = (int) Pos.getY();

                if (type != TileType.EMPTY) {
                    switch (field[x][y]) {
                        case EMPTY:
                            field[x][y] = type;
                            health[x][y] = hp;
                            field[i][j] = TileType.EMPTY;
                            health[i][j] = -1;
                            break;
                        case PREY:
                            if (type == TileType.PREDATOR) {
                                //prey moves on predator
                                field[x][y] = type;
                                health[x][y] = hp + health[i][j] + 50;
                                field[i][j] = TileType.EMPTY;
                                health[i][j] = -1;
                                preyCount--;
                            }
                            break;
                        case PREDATOR:
                            if (type == TileType.PREY) {
                                //predator moves on prey
                                health[x][y] = hp + health[i][j] + 50;
                                field[i][j] = TileType.EMPTY;
                                health[i][j] = -1;
                                preyCount--;
                            }
                            break;
                    }
                }
            }
        }

        // update health
        // replicate if possible die if health < 0
        // minimum chance to spawn Prey/Predator depending on Birth rate
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                Random rand = new Random(System.nanoTime());

                switch (field[i][j]) {
                    case PREY:
                        health[i][j] += PREYPTURN;
                        if (health[i][j] > REPHP + 100 - preyBirthRate) {
                            if (replicate(i, j, field)) {
                                preyCount++;
                            }
                        }
                        break;
                    case PREDATOR:
                        health[i][j] += PREDPTURN;
                        if (health[i][j] < 0) {
                            health[i][j] = -1;
                            field[i][j] = TileType.EMPTY;
                            predCount--;
                        } else if (health[i][j] > REPHP + 100 - predBirthRate) {
                            if (replicate(i, j, field)) {
                                predCount++;
                            }
                        }
                        break;
                    case EMPTY:
                        double r = rand.nextDouble() * 1000000;
                        if (r < preyBirthRate) {
                            //spawn Prey
                            field[i][j] = TileType.PREY;
                            health[i][j] = STARTHP;
                            preyCount++;
                        } else if (r > 1000000 - predBirthRate) {
                            //spawn Pred
                            field[i][j] = TileType.PREDATOR;
                            health[i][j] = STARTHP;
                            predCount++;
                        }
                        break;
                }
            }
        }
    }

    private boolean replicate(int i, int j, TileType[][] field) {
        Pair pos = move(i, j);
        int pX, pY;
        pX = (int) pos.getX();
        pY = (int) pos.getY();
        if (field[pX][pY] == TileType.EMPTY) {
            field[pX][pY] = field[i][j];
            health[pX][pY] = STARTHP;
            health[i][j] = 20;
            return true;
        }
        return false;
    }

    private Pair move(int x, int y) {

        Random rand = new Random(System.nanoTime());
        int rX = rand.nextInt(3);
        int rY = rand.nextInt(3);

        if (rX == 0) {
            --x;
        } else if (rX == 2) {
            ++x;
        }
        if (rY == 0) {
            --y;
        } else if (rY == 2) {
            ++y;
        }

        x = (x + sizeX) % sizeX;
        y = (y + sizeY) % sizeY;

        return new Pair(x, y);
    }

    private Tile[][] Field2Tile() {
        Tile[][] tile = new Tile[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                tile[i][j] = new Tile(field[i][j]);
            }
        }

        return tile;
    }

}
