/*
 * Evolutionary Algorithms Framework
 *
 * Copyright (c) 2023 Christian Lins <christian.lins@haw-hamburg.de>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heaal.eaf.crossover;

import de.heaal.eaf.base.Individual;

import java.util.Random;

public class DifferentialCrossover implements Combination {

    protected Random rng;
    protected float crossoverRate;

    /**
     * CrossoverRate that needs to be set at least once
     * @param crossoverRate crossoverRate...
     */
    public void setCrossoverRate(float crossoverRate) { this.crossoverRate = crossoverRate; }

    @Override
    public void setRandom(Random rng) {
        this.rng = rng;
    }

    @Override
    public Individual combine(Individual[] parents) {
        int dim = parents[0].getGenome().len();
        
        Individual child = parents[0].copy();

        for(int i = 0; i < dim; i++){
            if (rng.nextDouble() < crossoverRate || i == rng.nextInt(2)) {
                // trial vector
                child.getGenome().array()[i] = parents[0].getGenome().array()[i];
            } else {
                // parent vector
                child.getGenome().array()[i] = parents[1].getGenome().array()[i];
            }
        }
        
        return child;
    }
    
}
