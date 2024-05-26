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
import de.heaal.eaf.mutation.RngDifferentialMutation;

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
    private final float stepsize;
    private final float crossoverRate;
    private final String logFile;


    public DifferentialEvolution(float[] min, float[] max, float stepsize, float crossoverRate, int populationSize,
                                 Combination combination, Comparator<Individual> comparator,
                                 Mutation mutator, ComparatorIndividual terminationCriterion)
    {
        super(comparator, mutator);
        this.indFac = new ParticleFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        this.combination = combination;
        if(populationSize <= 4) {
            throw new IllegalArgumentException("Population size must be at least 4");
        }
        this.populationSize = populationSize;

        // Stepsize Parameter [0.4; 0.9]
        this.stepsize = stepsize;
        // Crossover Rate [0.1; 1.0]
        this.crossoverRate = crossoverRate;

        // Create the log file with configuration data in the name
        StringBuilder path = new StringBuilder();
        path.append("data/");

        StringBuilder name = new StringBuilder();
        name.append("de_rnd_1_bin").append("_");
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
        // Sort population before?
        logData(logFile);

        List<Individual> children = new ArrayList<>();
        // For each Individual of the current Population
        for (int i = 0; i < population.size(); i++) {
            // Step 1. Create the trial vector by applying mutation
            Individual parent =  population.get(i).copy();
            Individual child = population.get(i).copy();
            mutate(child);
            /*
            MutationOptions opt = new MutationOptions();
            opt.put(MutationOptions.KEYS.STEPSIZE, stepsize);
            mutator = mutator.setPopulation(population);
            mutator.mutate(child, opt);
            */

            // Step 2. Create a child by applying crossover
            Individual[] parents = new Individual[2];
            parents[0] = child;
            parents[1] = parent;
            child = combination.combine(parents);

            // Step 3. Calculate the fitness of the child and the parent Individual and select the fittest
            if(comparator.compare(child, parent) >= 0) {
                population.set(i, child);
            }
        }
    }

    private void mutate(Individual ind){
        int i = population.indexOf(ind);
        int a, b, c;
        do {
            a = rng.nextInt(population.size());
        } while (a == i);
        do {
            b = rng.nextInt(population.size());
        } while (b == i || b == a);
        do {
            c = rng.nextInt(population.size());
        } while (c == i || c == a || c == b);

        int dim = ind.getGenome().array().length;

        for (int j = 0; j < dim; j++) {
            ind.getGenome().array()[j] = population.get(a).getGenome().array()[j] +
                    stepsize * (population.get(b).getGenome().array()[j] + population.get(c).getGenome().array()[j]);
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
