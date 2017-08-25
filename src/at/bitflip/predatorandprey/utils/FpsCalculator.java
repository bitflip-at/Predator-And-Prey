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
package at.bitflip.predatorandprey.utils;

import at.bitflip.predatorandprey.PredatorAndPrey;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emanuel Gitterle <emanuel.gitterle@bitflip.at>
 */
public class FpsCalculator implements Runnable{
    
    private int calculatedFps;
    private static boolean stop = false;
    
    @Override
    public void run() {
        
        while(!stop){
            try {
            sleep(1000);
                calculatedFps  = PredatorAndPrey.getFramesCount();
                PredatorAndPrey.setFramesCount(0);
                PredatorAndPrey.setCalculatedFps(calculatedFps);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(FpsCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void kill(){
        stop = true;
    }
}
