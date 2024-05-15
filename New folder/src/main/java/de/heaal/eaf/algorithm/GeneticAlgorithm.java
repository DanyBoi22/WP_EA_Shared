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

import de.heaal.eaf.base.*;
import de.heaal.eaf.crossover.AverageCrossover;
import de.heaal.eaf.crossover.Combination;
import de.heaal.eaf.crossover.SinglePointCrossover;
import de.heaal.eaf.evaluation.ComparatorIndividual;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
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
public class GeneticAlgorithm extends Algorithm {

    private final IndividualFactory indFac;
    private final ComparatorIndividual terminationCriterion;
    private final Combination combination;
    private final int populationSize;
    private final float mutationRate;
    private final boolean useElitism;
    private final int numberElitism;
    private final Function<Individual,Float> fitnessFunction;
    private final String logFile;


    public GeneticAlgorithm(float[] min, float[] max, int populationSize,
                            Combination combination, boolean useElitism,
                            Comparator<Individual> comparator, Mutation mutator,  ComparatorIndividual terminationCriterion)
    {
        super(comparator, mutator);
        this.indFac = new ParticleFactory(min, max);
        this.terminationCriterion = terminationCriterion;
        this.combination = combination;
        if(populationSize <= 1) {
            throw new IllegalArgumentException("Population size must be greater than 1");
        }
        this.populationSize = populationSize;
        this.useElitism = useElitism;

        // Better to be small: [0.05; 0.3], otherwise the algo degenerates to just random search
        this.mutationRate = 0.01f;
        // Better to be small: 1-2
        this.numberElitism = 1;
        // Well... fitness function
        this.fitnessFunction = FitnessPosCoordinates;

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
        name.append(mutationRate);
        if(useElitism) {
            name.append("_");
            name.append(numberElitism).append("_");
            name.append(useElitism);
        }

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

        // Step 1 calculate the fitness of each Parent in the Population
        // and sort the Population in descending order
        //Comparator<Individual> cmp = new MinimizeFunctionComparator(fitnessFunction);
        population.sort(comparator);

        // Log the fitness of the population
        logData(logFile);

        // While |Children| < |Parents|
        List<Individual> children = new ArrayList<>();

        while(children.size() != population.size()) {
            // Step 2 select a pair of different parents
            Individual[] parents = new Individual[2];
            parents[0] = selectNormal(population, new Random(), null);
            parents[1] = selectNormal(population, new Random(), parents[0]);

            // Step 3 mate the parent
            children.add(combination.combine(parents));
        }
        //Loop

        // Step 4 randomly mutate kids
        // Only one feature is allowed to mutate
        MutationOptions opt = new MutationOptions();
        opt.put(MutationOptions.KEYS.MUTATION_PROBABILITY, mutationRate);

        Individual ind;
        for(int i = 0; i < population.size(); i++) {
            ind = children.get(i);
            mutator.mutate(ind, opt);
        }

        // Step 5 set the children as the new population and exterminate the parents

        // Step 5.1 Preserve Elite if useElitism is True
        // Because the parents list is already sorted, all we need to do is choose the first best Individuals
        int startpoint = useElitism ? numberElitism : 0;

        for(int i = startpoint; i < population.size(); i++) {
            population.set(i, children.get(i));
        }
    }

    /**
     * Iterates trough population and writes fitness of each individual to the log file
     *
     * @param logFile
     */
    public void logData(String logFile) {
        Float[] data = new Float[population.size()];
        for(int i = 0; i < population.size(); i++) {
            data[i] = population.get(i).getCache();
            //data[i] = fitnessFunction.apply(population.get(i));
        }
        logLineToCSV(data,logFile);
    }

    /**
     * Fitness Functions.
     * If we are trying to maximize f(x), then we calculate the fitness of each xi
     * by computing f(xi). -> |x| + |y|
     * If we are trying to minimize f(x), then we calculate the fitness of each xi
     * by computing the negative of f(xi). -> -|x| - |y|; alternatively 1/|x| + 1/|x| (watch out for zero division)
     * But because we are using minimising Comparator to sort the population we need to solve a maximization problem
     */
    public static Function<Individual, Float> FitnessPosCoordinates = (x) -> {
        float x1 = x.getGenome().array()[0] < 0 ? -x.getGenome().array()[0] : x.getGenome().array()[0];
        float x2 = x.getGenome().array()[1] < 0 ? -x.getGenome().array()[1] : x.getGenome().array()[1];
        return x1 + x2;};

    public static Function<Individual, Float> FitnessNegCoordinates = (x) -> {
        float x1 = x.getGenome().array()[0] > 0 ? -x.getGenome().array()[0] : x.getGenome().array()[0];
        float x2 = x.getGenome().array()[1] > 0 ? -x.getGenome().array()[1] : x.getGenome().array()[1];
        return x1 + x2;};

    public static Function<Individual, Float> FitnessPosY = (x) -> x.getCache();

    public static Function<Individual, Float> FitnessNegY = (x) -> -x.getCache();
  
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

        //Comparator<Individual> cmp = new MinimizeFunctionComparator(fitnessFunction);
        population.sort(comparator);
        logData(logFile);

        System.out.println("Best Genome: " + population.get(0).getGenome());
        System.out.println("Cache: " + population.get(0).getCache());

    }

}
