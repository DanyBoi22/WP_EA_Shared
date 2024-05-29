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

package de.heaal.eaf.testbench;

import de.heaal.eaf.algorithm.DifferentialEvolution;
import de.heaal.eaf.crossover.DifferentialCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import de.heaal.eaf.mutation.DifferentialMutation;

import java.util.Random;

import static de.heaal.eaf.testbench.TestFunctions.evalAckleyFunc2D;

/**
 * Test bench for the Differential Evolution algorithm.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class TestDifferential {
    public static void main(String[] args) {
        float[] min = {-5.12f, -5.12f};
        float[] max = {+5.12f, +5.12f};

        var comparator = new MinimizeFunctionComparator(evalAckleyFunc2D);

        float stepsize = 0.5f;
        float crossoverRate = 0.5f;
        int numDA = 1; // 1 or 2
        String trialVectorVariation = "best"; // "rnd" or "best"
        String scaleFactorVariation = "J"; // "D" for Dither or "J" for Jitter or "S" for Static

        var combination = new DifferentialCrossover();
        combination.setCrossoverRate(crossoverRate);
        combination.setRandom(new Random());

        var mutation = new DifferentialMutation(new Random());
        mutation.setRandom(new Random());

        for(int i = 0; i < 1; i++){
            var algo = new DifferentialEvolution(min, max, stepsize, crossoverRate, numDA, 40, combination,
                    comparator, trialVectorVariation, scaleFactorVariation, mutation, new ComparatorIndividual(0.001f));
            algo.run();
        }
    }
}
