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
import de.heaal.eaf.base.VecN;

/**
 * A fake individual that can be constructed with a fixed evaluation value.
 * Can be used to represent the target or threshold value to determine the
 * termination criterion in algorithms. It has no genome of course.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class ComparatorIndividual implements Individual {

    protected final float value;
    
    public ComparatorIndividual(float value) {
        this.value = value;
    }
    
    @Override
    public void clearCache() {
        // No effect
    }

    @Override
    public void setCache(float value) {
        // No effect
    }

    @Override
    public float getCache() {
        return value;
    }

    @Override
    public boolean hasCache() {
        return true;
    }

    @Override
    public Individual copy() {
        return new ComparatorIndividual(value);
    }

    @Override
    public VecN getGenome() {
        return null;
    }

    @Override
    public Individual add(Individual other) {
        // No effect
        return this;
    }

    @Override
    public Individual sub(Individual other) {
        // No effect
        return this;
    }

    @Override
    public Individual mul(float factor) {
        // No effect
        return this;
    }
    
}
