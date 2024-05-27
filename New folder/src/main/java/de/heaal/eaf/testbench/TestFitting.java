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
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.crossover.DifferentialCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import de.heaal.eaf.mutation.RngDifferentialMutation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Test bench for testing Differential Evolution algorithm by fitting sinusoidal function on a IMU Data.
 */
public class TestFitting {
    private static List<Float> timeData = new ArrayList<>();
    private static List<Float> measurementData = new ArrayList<>();

    public static void main(String[] args) {
        //ToDo: 4D individual. Establish borders
        float[] min = {-2.f, -2.f, -2.f, -2.f};
        float[] max = {+2.f, +2.f, +2.f, +2.f};

        //Load measurements
        try {
            readCSV("data/sensordata.csv", timeData, measurementData);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Minimising function - sum of smallest squared distances to the measurements
        var comparator = new MinimizeFunctionComparator(squaredDistancesError);


        float stepsize = 0.4f;
        float crossoverRate = 0.5f;
        var combination = new DifferentialCrossover();
        combination.setCrossoverRate(crossoverRate);
        combination.setRandom(new Random());

        var mutation = new RngDifferentialMutation(new Random());

        var algo = new DifferentialEvolution(min, max, stepsize, crossoverRate, 1, 10, combination,
                comparator,"rnd", mutation, new ComparatorIndividual(7000.f));
        algo.run();
    }

    private static void readCSV(String filePath, List<Float> timeData, List<Float> measurementData) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int readlines = 0;

            // read first 1000 lines of file
            while ((line = br.readLine()) != null && readlines != 1000) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header line
                    continue;
                }

                String[] values = line.split(";");
                if (values.length >= 5) {
                    float time = Float.parseFloat(values[0].trim());
                    float accAbs = Float.parseFloat(values[4].trim()) - 9.83416414f;
                    timeData.add(time);
                    measurementData.add(accAbs);
                    readlines++;
                }
            }
        }
    }

    public static Function<Individual,Float> squaredDistancesError =
            (ind) -> {
                var x = ind.getGenome().array();

                float sum = 0.f;

                int n = timeData.size();

                for (int i = 0; i < n; i++) {
                    float t = timeData.get(i);
                    float yMeasured = measurementData.get(i);
                    float yPredicted = sinusoidalFunction(t, x[0] /*Amplitude*/,x[1] /*Frequency*/, x[2]/*Phase*/, x[3]/*Offset*/);
                    float distance = yMeasured - yPredicted;
                    sum += distance * distance;
                }

                return sum;
            };

    private static float sinusoidalFunction(float t, float A, float f, float phi, float D) {
        return (float) (A * Math.sin(2 * Math.PI * f * t + phi) + D);
    }
}
