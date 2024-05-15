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

package de.heaal.eaf.base;

/**
 * A generic individual factory that can be used in various EA algorithms.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class GenericIndividualFactory extends AbstractIndividualFactory {

    protected float[] min, max;
    
    /**
     * Initialized this factory with the min and max values for the genomes
     * of the created individuals.
     * 
     * @param min Minimum value limits for the genome (inclusive)
     * @param max Maximum value limits for the genome (exclusive)
     */
    public GenericIndividualFactory(float[] min, float[] max) {
        if (min.length != max.length) {
            throw new IllegalArgumentException("min.length != max.length");
        }
        this.min = min;
        this.max = max;
    }
    
    @Override
    public GenericIndividual create() {
        return new GenericIndividual(createRandomGenome());
    }
    
    protected VecN createRandomGenome() {
        var genome = new float[min.length];
        
        for (int i = 0; i < genome.length; i++) {
            genome[i] = rng.nextFloat() * (max[i] - min[i]) + min[i];
        }
        
        return new VecN(genome);
    }
    
}
