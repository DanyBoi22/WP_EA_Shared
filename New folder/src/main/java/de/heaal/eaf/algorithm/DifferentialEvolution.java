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

package de.heaal.eaf.algorithm;

import de.heaal.eaf.base.Algorithm;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.IndividualFactory;
import de.heaal.eaf.crossover.AverageCrossover;
import de.heaal.eaf.crossover.Combination;
import de.heaal.eaf.crossover.SinglePointCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static de.heaal.eaf.logger.Logger.createLogFile;
import static de.heaal.eaf.logger.Logger.logLineToCSV;
import static de.heaal.eaf.selection.SelectionUtils.selectNormal;

/**
 * Implementation of the Hill Climbing algorithm.
 * 
 * @author Christian Lins <christian.lins@haw-hamburg.de>
 */
public class DifferentialEvolution extends Algorithm {

    private final IndividualFactory indFac;
    private final ComparatorIndividual terminationCriterion;
    private final Combination combination;
    private final int populationSize;
    private final float mutationRate;
    private final float stepsize;
    private final float crossoverRate;
    private final String logFile;


    public DifferentialEvolution(float[] min, float[] max, int populationSize,
                                 Combination combination, Comparator<Individual> comparator,
                                 Mutation mutator, ComparatorIndividual terminationCriterion)
    {
        super(comparator, mutator);
        this.indFac = new ParticleFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        this.combination = combination;
        if(populationSize <= 1) {
            throw new IllegalArgumentException("Population size must be greater than 1");
        }
        this.populationSize = populationSize;

        // Better to be small: [0.05; 0.3], otherwise the algo degenerates to just random search
        this.mutationRate = 0.01f;
        // Stepsize Parameter [0.4; 0.9]
        this.stepsize = 0.5f;
        // Crossover Rate [0.1; 1.0]
        this.crossoverRate = 0.5f;

        // Create the log file with configuration data in the name
        StringBuilder path = new StringBuilder();
        path.append("data/");

        StringBuilder name = new StringBuilder();
        name.append("ge").append("_");
        name.append(populationSize).append("_");
        if(combination.getClass() == AverageCrossover.class){
            name.append("avg");
        } else if (combination.getClass() == SinglePointCrossover.class) {
            name.append("rng");
        }
        name.append("_");
        name.append(mutationRate).append("f");

        String strName = name.toString();

        path.append(strName).append("/").append(strName).append(".csv");

        this.logFile = createLogFile(path.toString());
        if(logFile == null){
            throw new NullPointerException("log file is null");
        }
    }
    
    @Override
    public void nextGeneration() {
        super.nextGeneration();

        // For each Individual of the current Population
        for (int i = 0; i < population.size(); i++) {
            // Step 1. Create the trial vector by applying mutation

            // Step 2. Create a child by applying crossover
            // Step 3. Calculate the fitness of the child and the parent Individual and select the fittest
        }
    }

    /**
     * Iterates trough population and writes fitness of each individual to the log file
     *
     * @param logFile name of the log file
     */
    public void logData(String logFile) {
        Float[] data = new Float[population.size()];
        for(int i = 0; i < population.size(); i++) {
            data[i] = population.get(i).getCache();
        }
        logLineToCSV(data,logFile);
    }
  
    @Override
    public boolean isTerminationCondition() {
        boolean termination = false;

        for(int i = 0; i < population.size(); i++) {
            if (comparator.compare(population.get(i), terminationCriterion) > 0)
                termination = true;
        }

        return termination;
    }

    @Override
    public void run() {
        initialize(indFac, populationSize);
        int count = 0;
        while(!isTerminationCondition()) {
            System.out.println("Gen: " + count);
            nextGeneration();
            count++;
        }

        population.sort(comparator);
        logData(logFile);

        System.out.println("Best Genome: " + population.get(0).getGenome());
        System.out.println("Cache: " + population.get(0).getCache());

    }

}
