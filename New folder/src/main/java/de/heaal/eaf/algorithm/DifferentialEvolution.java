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
import de.heaal.eaf.mutation.RngDifferentialMutation;

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
    private String variation;
    private final String logFile;


    public DifferentialEvolution(float[] min, float[] max, float stepsize, float crossoverRate, int numDA, int populationSize,
                                 Combination combination, Comparator<Individual> comparator, String variation,
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
        if (variation.equals("rnd") || variation.equals("best")) {
            this.variation = variation;
        } else {
           throw new IllegalArgumentException("Mutation variation is not known");
        }

        // Create the log file with configuration data in the name
        StringBuilder path = new StringBuilder();
        path.append("data/");

        StringBuilder name = new StringBuilder();
        name.append("de_");
        name.append(variation).append("_");
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
        // Sort population before?
        population.sort(comparator);
        logData(logFile);

        List<Individual> children = new ArrayList<>();
        // For each Individual of the current Population
        for (int i = 0; i < population.size(); i++) {
            // Step 1. Create the trial vector by applying mutation
            Individual parent =  population.get(i).copy();
            Individual trial = population.get(i).copy();
            mutate(trial, variation);

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
     * Check if position of candidate is already in list of candidates
     *
     * @param parent Position of parent Individual
     * @param candidates List of positions of candidates
     * @param candidatePos Position of the candidate to check in candidates list
     * @return true if position is unique and can be taken as candidate
     */
    private boolean isUnique(int parent, int[] candidates, int candidatePos){
        if (candidates[candidatePos] == parent) {
           return false;
        }

        for (int i = 0; i < candidates.length; i++){
            if (candidates[i] == candidates[candidatePos]) {
                if(i != candidatePos) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Adds a single gene of each of 2 candidates and multiplies it by stepsize
     *
     * @param stepsize stepsize
     * @param a position of first candidate
     * @param b position of second candidate
     * @param allele gene to add
     * @return
     */
    private float differentialAdditionAllele(float stepsize, int a, int b, int allele) {
        return stepsize * (population.get(a).getGenome().array()[allele] + population.get(b).getGenome().array()[allele]);
    }

    /**
     * Creates a trial vector out of given individual
     * @param ind individual to mutate
     */
    private void mutate(Individual ind, String variation){
        int i = population.indexOf(ind);
        int numCandidates = numDA*2+1;
        int[] candidates = new int[numCandidates];
        Arrays.fill(candidates, -1);
        int startpoint = 0;

        if (Objects.equals(variation, "best")) {
            candidates[0] = 0;
            startpoint = 1;
        } /* else if (Objects.equals(variation, "rnd")) {
            startpoint = 0;
        } */

        for(int j = startpoint; j < numCandidates; j++){
            do {
                candidates[j] = rng.nextInt(population.size());
            } while (!isUnique(i, candidates, j));
        }

        int dim = ind.getGenome().array().length;

        // ToDo: Dither and Jitter for stepsize
        for (int j = 0; j < dim; j++) {
            ind.getGenome().array()[j] = population.get(candidates[0]).getGenome().array()[j] + differentialAdditionAllele(stepsize, candidates[1], candidates[2], j);
            if (numDA == 2) {
                ind.getGenome().array()[j] = ind.getGenome().array()[j] + differentialAdditionAllele(stepsize, candidates[3], candidates[4], j);
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
