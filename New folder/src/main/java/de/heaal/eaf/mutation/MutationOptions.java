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

import java.util.HashMap;
import java.util.Map;

/**
 * Options that can be passed to a Mutator instance.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class MutationOptions {
    public static enum KEYS {
        FEATURE_INDEX,
        MUTATION_PROBABILITY,
        STEPSIZE,
        NUMDA,
        TRIAL_VECTOR_VARIATION, //1 - rnd, 2 - best
        SCALE_FACTOR_VARIATION
    }
    
    private final Map<KEYS, Integer> intOptions = new HashMap<>();
    private final Map<KEYS, Float> floatOptions = new HashMap<>();
    
    public int get(KEYS key, int _default) {
        if (intOptions.containsKey(key)) {
            return intOptions.get(key);
        } else {
            return _default;
        }
    }
    
    public float get(KEYS key, float _default) {
        if (floatOptions.containsKey(key)) {
            return floatOptions.get(key);
        } else {
            return _default;
        }
    }

    /*
    private final Map<KEYS, Individual[]> indListOptions = new HashMap<>();

    public Individual[] get(KEYS key, Individual[] _default) {
        if (indListOptions.containsKey(key)) {
            return indListOptions.get(key);
        } else {
            return _default;
        }
    }

    public void put(KEYS key, Individual[] value) {
        indListOptions.put(key, value);
    }
    */
    
    public void put(KEYS key, int value) {
        intOptions.put(key, value);
    }
    
    public void put(KEYS key, float value) {
        floatOptions.put(key, value);
    }
}
