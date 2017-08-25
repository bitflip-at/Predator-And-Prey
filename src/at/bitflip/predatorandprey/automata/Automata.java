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
    private static final int REPLICATEHP = 200;
    private static final int PREYPTURN = 10;
    private static final int PREDPTURN = -1;

    private final int sizeX;
    private final int sizeY;

    private int preyCount;
    private int predCount;
    private int generation;

    private double preySpawnRate;
    private double predSpawnRate;

    private TileType[][] field;
    private int[][] health;

    public Automata(int sizeX, int sizeY, double preySpawnRate, double predSpawnRate) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.preySpawnRate = preySpawnRate;
        this.predSpawnRate = predSpawnRate;

        preyCount = 0;
        predCount = 0;
        generation = 0;

        initializeField();
    }

    public Tile[][] step() {

        updateField();
        return Field2Tile();
    }

    public void updateValues(double preySpawnRate, double predSpawnRate) {
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

                if (r <= preySpawnRate) {
                    field[i][j] = TileType.PREY;
                    health[i][j] = STARTHP;
                    preyCount++;
                } else if (r >= (200.0 - predSpawnRate)) {
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
        TileType[][] tmpField = new TileType[sizeX][sizeX];
        int[][] tmpHealth = new int[sizeX][sizeY];
        generation++;

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                tmpField[i][j] = TileType.EMPTY;
                tmpHealth[i][j] = -1;
            }
        }

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                TileType type = field[i][j];
                int hp = health[i][j];

                Pair Pos = move(i, j);

                int x = (int) Pos.getX();
                int y = (int) Pos.getY();

                switch (tmpField[x][y]) {
                    case EMPTY:
                        tmpField[x][y] = type;
                        tmpHealth[x][y] = hp;
                        break;
                    case PREY:
                        if (type == TileType.PREY) {
                            Pair pos = getNextFreePos(i, j);
                            if (pos != null) {
                                tmpField[x][y] = type;
                                tmpHealth[x][y] = hp;
                            }
                        } else {
                            tmpHealth[x][y] += health[i][j];
                            tmpField[x][y] = TileType.PREDATOR;
                            preyCount--;
                        }
                        break;
                    case PREDATOR:
                        if (type == TileType.PREDATOR) {
                            Pair pos = getNextFreePos(i, j);
                            if (pos != null) {
                                tmpField[x][y] = type;
                                tmpHealth[x][y] = hp;
                            }
                        } else {
                            tmpHealth[x][y] += health[i][j];
                            tmpField[x][y] = TileType.PREDATOR;
                            preyCount--;
                        }
                        break;
                }
            }
        }

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                health[i][j] = tmpHealth[i][j];
                field[i][j] = tmpField[i][j];
            }
        }

        // update health and replicate if possible
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                switch (field[i][j]) {
                    case PREY:
                        health[i][j] += PREYPTURN;
                        break;
                    case PREDATOR:
                        health[i][j] += PREDPTURN;
                        break;
                }

                if (health[i][j] > REPLICATEHP) {
                    Pair pos = getNextFreePos(i, j);
                    if (pos != null) {
                        field[(int) pos.getX()][(int) pos.getY()] = field[i][j];
                        health[(int) pos.getX()][(int) pos.getY()] = STARTHP;
                        health[i][j] -= STARTHP;

                        switch (field[i][j]) {
                            case PREY:
                                preyCount++;
                                break;
                            case PREDATOR:
                                predCount++;
                                break;
                        }
                    }

                } else if (health[i][j] < 0) {
                    health[i][j] = -1;

                    switch (field[i][j]) {
                        case PREY:
                            preyCount--;
                            break;
                        case PREDATOR:
                            predCount--;
                            break;
                    }

                    field[i][j] = TileType.EMPTY;
                }
            }
        }
    }

    private Pair getNextFreePos(int i, int j) {

        for (int x = (i - 1); x < (i + 1); x++) {
            for (int y = (j - 1); y < (j + 1); y++) {
                if (x < 0) {
                    continue;
                }
                if (y < 0) {
                    continue;
                }
                if (field[x][y] == TileType.EMPTY) {
                    return new Pair(x, y);
                }
            }
        }

        return null;
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
