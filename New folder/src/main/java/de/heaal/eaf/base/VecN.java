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
 * An N-dimensional vector of floats.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class VecN {
    protected float[] array;
    
    public VecN(int length) {
        array = new float[length];
    }
    
    public VecN(float[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        this.array = array;
    }
    
    public VecN copy() {
        return new VecN(array.clone());
    }
    
    public float[] array() {
        return array;
    }
    
    public int len() {
        return array.length;
    }
    
    public VecN add(VecN other) {
        for (int i = 0; i < array.length; i++) {
            array[i] += other.array[i];
        }
        return this;
    }
    
    public VecN sub(VecN other) {
        for (int i = 0; i < array.length; i++) {
            array[i] -= other.array[i];
        }
        return this;
    }
    
    public VecN mul(VecN other) {
        for (int i = 0; i < array.length; i++) {
            array[i] *= other.array[i];
        }
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        for (int i = 0; i < array.length; i++) {
            buf.append(array[i]);
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }
}
