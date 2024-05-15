/*
 * Evolutionary Algorithms Framework
 *
 * Copyright (c) 2023-2024 Christian Lins <christian.lins@haw-hamburg.de>
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
 * An abstract class representing an individual (solution candidate).
 * The class has a caching mechanism to store already computed fitness values for 
 * later access. Every time the individual is mutated the cache must be cleared
 * using clearCache().
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public abstract class AbstractIndividual implements Individual {
    protected VecN genome;
       
    protected boolean hasCache = false;
    protected float cache = Float.NaN;
    
    @Override
    public abstract Individual copy();
 
    @Override
    public VecN getGenome() {
        return genome;
    }
    
    @Override
    public void clearCache() {
        hasCache = false;
    }

    @Override
    public void setCache(float value) {
        hasCache = true;
        cache = value;
    }

    /**
     * Returns the cached fitness value. One should check if this
     * value is valid using hasCache().
     */
    @Override
    public float getCache() {
        return cache;
    }

    @Override
    public boolean hasCache() {
        return hasCache;
    }

    @Override
    public Individual add(Individual other) {
        genome.add(other.getGenome());
        clearCache();
        return this;
    }

    @Override
    public Individual sub(Individual other) {
        genome.sub(other.getGenome());
        clearCache();
        return this;
    }

    @Override
    public Individual mul(float factor) {
        for (int i = 0; i < genome.len(); i++) {
            genome.array()[i] *= factor;
        }
        clearCache();
        return this;
    }
    
}
