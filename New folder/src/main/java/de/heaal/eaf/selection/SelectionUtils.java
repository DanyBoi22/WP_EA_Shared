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

package de.heaal.eaf.selection;

import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.Population;
import java.util.Random;

/**
 * A helper class that contains useful functions for the selection step in
 * EA.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class SelectionUtils {
    
    /**
     * Select an Individual from given population using a
     * gaussian random value. Population must be sorted by fitness descending, e.g.
     * Individuals with lower indices are selected more often.
     * 
     * @param population
     * @param rng
     * @param avoid
     * @return 
     */
    public static Individual selectNormal(Population population, Random rng, Individual avoid) {
        Individual selected = null;
        
        do {
            int p = (int)(Math.abs(rng.nextGaussian()) * population.size() / 2);
            while (p >= population.size()) {
                p--;
            }
            selected = population.get(p);
        } while (selected == avoid);
        
        return selected;
    }
    
    public static Individual selectUniform(Population population, Random rng, Individual[] avoid) {
        Individual selected = null;
        do {
            selected = population.get(rng.nextInt(population.size()));
        } while (contains(avoid, selected));
        return selected;
    }
    
    private static boolean contains(Object[] array, Object element) {
        if (array == null)
            return false;
        
        for (Object obj : array) {
            if (obj.equals(element))
                return true;
        }
        return false;
    }
}
