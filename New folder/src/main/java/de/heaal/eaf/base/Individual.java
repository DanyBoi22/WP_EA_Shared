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
 * Interface for a very basic Individual of EA.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public interface Individual {
    
    void clearCache();
    void setCache(float value);
    float getCache();
    boolean hasCache();
    
    Individual copy();
    
    VecN getGenome();
    
    /**
     * Adds the genome of other to this individual's genome.That means: this = this + other
     * 
     * 
     * @param other 
     * @return this
     */
    Individual add(Individual other);
    
    /**
     * Subtract the genome of other from this individual's genome.That means: this = this - other 
     * 
     * @param other 
     * @return this
     */
    Individual sub(Individual other);
    
    /**
     * Multiplies the factor with this genome.
     * 
     * @param factor
     * @return this
     */
    Individual mul(float factor);
}
