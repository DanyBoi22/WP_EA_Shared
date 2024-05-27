package de.heaal.eaf.mutation;

import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.Population;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class RngDifferentialMutation implements Mutation{

    private Random rng;
    private Population population;

    public RngDifferentialMutation(Random rng) {this.rng = rng;}

    /**
     * For this algorithm to work the population has to be set every iteration anew
     * @param population current population
     */
    @Override
    public void setPopulation(Population population) { this.population = population; }

    @Override
    public void setRandom(Random rng) {
        this.rng = rng;
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
    @Override
    public void mutate(Individual ind, MutationOptions opt){
        float stepsize = opt.get(MutationOptions.KEYS.STEPSIZE, 0.5f);
        int numDA = opt.get(MutationOptions.KEYS.NUMDA, 1);
        int variation = opt.get(MutationOptions.KEYS.VARIATION, 1);

        int i = population.indexOf(ind);
        int numCandidates = numDA*2+1;
        int[] candidates = new int[numCandidates];
        Arrays.fill(candidates, -1);


        int startpoint = 0;

        if (variation == 2) { // 2 - best
            candidates[0] = 0;
            startpoint = 1;
        } /* else if (variation == 1) { // 1 - rnd
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

}
