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

import de.heaal.eaf.algorithm.GeneticAlgorithm;
import de.heaal.eaf.crossover.AverageCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import de.heaal.eaf.mutation.RandomMutation;
import de.heaal.eaf.crossover.SinglePointCrossover;

import java.util.Random;

/**
 * Test bench for the Hill Climbing algorithm.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class TestGenetic {
    public static void main(String[] args) {
        float[] min = {-5.12f, -5.12f};
        float[] max = {+5.12f, +5.12f};

        TestFunctions test = new TestFunctions();

        var comparator = new MinimizeFunctionComparator(test.evalAckleyFunc2D);

        var combination = new AverageCrossover();

        // Random crossover point is bs
        // var combination = new SinglePointCrossover();
        // combination.setRandom(new Random());

        var algo = new GeneticAlgorithm(min, max, 40, combination, true,
                comparator, new RandomMutation(min, max), new ComparatorIndividual(0.001f));
        algo.run();
    }
}
