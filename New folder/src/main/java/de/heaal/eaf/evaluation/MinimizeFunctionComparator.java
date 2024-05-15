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

package de.heaal.eaf.evaluation;

import de.heaal.eaf.base.Individual;
import java.util.Comparator;
import java.util.function.Function;

/**
 * A comparator that can be used to compare the fitness of two individuals
 * while minimizing a function value.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class MinimizeFunctionComparator implements Comparator<Individual> {

    private final Function<Individual,Float> evaluator;
    
    public MinimizeFunctionComparator(Function<Individual,Float> evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator must not be null");
        }
        this.evaluator = evaluator;
    }
    
    /**
     * Evaluates the individuals with the provided evaluator function.
     * If the function value of i0 is smaller than the value of i1, then i0 
     * is "greater" or "better" than i1 thus a positive integer is returned.
     * Otherwise 0 or a negative integer is returned.
     * 
     * @param i0
     * @param i1
     * @return a negative integer, zero, or a positive integer as the first 
     * argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Individual i0, Individual i1) {
        float ev0 = i0.hasCache() ? i0.getCache() : evaluator.apply(i0);
        float ev1 = i1.hasCache() ? i1.getCache() : evaluator.apply(i1);
        
        i0.setCache(ev0);
        i1.setCache(ev1);
        
        if (ev0 < ev1) { // because smaller is better for a minimization problem
            return 1;
        } else if (ev1 < ev0) {
            return -1;
        } else {
            return 0;
        }
    }
    
}
