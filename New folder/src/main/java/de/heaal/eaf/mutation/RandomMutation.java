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

package de.heaal.eaf.mutation;

import de.heaal.eaf.base.Individual;
import java.util.Random;

/**
 * Random mutation operator.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class RandomMutation implements Mutation {

    private Random rng;
    private final float[] min;
    private final float[] max;
    
    public RandomMutation(float[] min, float[] max) {
        assert min.length == max.length;
        
        this.min = min;
        this.max = max;
    }
    
    @Override
    public void setRandom(Random rng) {
        this.rng = rng;
    }

    @Override
    public void mutate(Individual ind, MutationOptions opt) {
        float p = opt.get(MutationOptions.KEYS.MUTATION_PROBABILITY, 0.1f);
        if (p < rng.nextFloat()) {
            // Skip this individual
            return;
        }
        
        int dim = min.length;
        int i = opt.get(MutationOptions.KEYS.FEATURE_INDEX, rng.nextInt(dim));
        ind.getGenome().array()[i] = rng.nextFloat() * (max[i] - min[i]) + min[i];
    }
    
}
