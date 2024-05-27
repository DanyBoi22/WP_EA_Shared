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
import de.heaal.eaf.crossover.Combination;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.mutation.Mutation;
import de.heaal.eaf.mutation.MutationOptions;

import java.util.*;

import static de.heaal.eaf.logger.Logger.createLogFile;
import static de.heaal.eaf.logger.Logger.logLineToCSV;

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
    // Stepsize Parameter [0.4; 0.9]
    private final float stepsize;
    // Crossover Rate [0.1; 1.0]
    private final float crossoverRate;
    // Number of differential additions [1;2]
    private final int numDA;
    // Mutation variation "rnd"/"best"
    private final String trialVectorVariation;
    private final String scaleFactorVariation;
    private final String logFile;


    public DifferentialEvolution(float[] min, float[] max, float stepsize, float crossoverRate, int numDA, int populationSize,
                                 Combination combination, Comparator<Individual> comparator,
                                 String trialVectorVariation, String scaleFactorVariation,
                                 Mutation mutator, ComparatorIndividual terminationCriterion)
    {
        super(comparator, mutator);
        this.indFac = new ParticleFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        this.combination = combination;
        this.stepsize = stepsize;
        this.crossoverRate = crossoverRate;
        if (numDA < 1 || numDA > 2) {
           throw new IllegalArgumentException("Number ob differential additions can be either 1 or 2");
        }
        this.numDA = numDA;
        if(populationSize <= numDA*2+1) {
            throw new IllegalArgumentException("Population size is too small for given number of differential additions");
        }
        this.populationSize = populationSize;
        if (trialVectorVariation.equals("rnd") || trialVectorVariation.equals("best")) {
            this.trialVectorVariation = trialVectorVariation;
        } else {
           throw new IllegalArgumentException("Trial Vector variation is not known");
        }
        if (scaleFactorVariation.equals("D") || scaleFactorVariation.equals("J") || scaleFactorVariation.equals("S")) {
            this.scaleFactorVariation = scaleFactorVariation;
        } else {
            throw new IllegalArgumentException("Scale Factor variation is not known");
        }

        // Create the log file with configuration data in the name
        StringBuilder path = new StringBuilder();
        path.append("data/");

        StringBuilder name = new StringBuilder();
        name.append("de_");
        name.append(trialVectorVariation).append("_");
        name.append(numDA).append("_");
        name.append("bin_");
        name.append(populationSize).append("_");
        name.append(stepsize).append("f_");
        name.append(crossoverRate).append("f");

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

        population.sort(comparator);
        logData(logFile);

        MutationOptions opt = new MutationOptions();
        opt.put(MutationOptions.KEYS.STEPSIZE, stepsize);
        opt.put(MutationOptions.KEYS.NUMDA, numDA);
        if(trialVectorVariation.equals("rnd")) {
            opt.put(MutationOptions.KEYS.TRIAL_VECTOR_VARIATION, 1);
        } else if (trialVectorVariation.equals("best")) {
            opt.put(MutationOptions.KEYS.TRIAL_VECTOR_VARIATION, 2);
        }
        if(scaleFactorVariation.equals("D")) {
            opt.put(MutationOptions.KEYS.SCALE_FACTOR_VARIATION, 1);
        } else if (scaleFactorVariation.equals("J")) {
            opt.put(MutationOptions.KEYS.SCALE_FACTOR_VARIATION, 2);
        } else {
            opt.put(MutationOptions.KEYS.SCALE_FACTOR_VARIATION, 0);
        }


        // For each Individual of the current Population
        for (int i = 0; i < population.size(); i++) {
            // Step 1. Create the trial vector by applying mutation
            Individual parent =  population.get(i).copy();
            Individual trial = population.get(i).copy();
            mutator.setPopulation(population);
            mutator.mutate(trial, opt);

            // Step 2. Create a child by applying crossover
            Individual[] parents = new Individual[2];
            parents[0] = trial;
            parents[1] = parent;
            Individual child = combination.combine(parents);

            // Step 3. Calculate the fitness of the child and the parent Individual and select the fittest
            if(comparator.compare(child, parent) >= 0) {
                population.set(i, child);
            }
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
        int runaway = 1001;
        int count = 0;
        while(!isTerminationCondition() && count < runaway) {
            System.out.println("Gen: " + count);
            nextGeneration();
            count++;
        }

        population.sort(comparator);
        logData(logFile);

        if (count >= runaway) {
            System.out.println("The Algorithm is terminated. It is a Runaway");
        }
        System.out.println("Best Genome: " + population.get(0).getGenome());
        System.out.println("Cache: " + population.get(0).getCache());

    }

}
