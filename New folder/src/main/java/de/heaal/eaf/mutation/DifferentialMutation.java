package de.heaal.eaf.mutation;

import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.Population;

import java.util.Arrays;
import java.util.Random;

public class DifferentialMutation implements Mutation{

    private Random rng;
    private Population population;

    public DifferentialMutation(Random rng) {this.rng = rng;}

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
     * Creates a trial vector out of given individual
     * @param ind individual to mutate
     */
    @Override
    public void mutate(Individual ind, MutationOptions opt){
        float stepsize = opt.get(MutationOptions.KEYS.STEPSIZE, 0.5f);
        int numDA = opt.get(MutationOptions.KEYS.NUMDA, 1);
        int trialVectorVariation = opt.get(MutationOptions.KEYS.TRIAL_VECTOR_VARIATION, 1);
        int scaleFactorVariation = opt.get(MutationOptions.KEYS.TRIAL_VECTOR_VARIATION, 0);

        int posCurrent = population.indexOf(ind);
        int numCandidates = numDA*2+1;
        int[] candidates = new int[numCandidates];
        Arrays.fill(candidates, -1);


        int startpoint = 0;
        // 1 - rnd, 2 - best
        if (trialVectorVariation == 2) {
            candidates[0] = 0;
            startpoint = 1;
        }

        for (int posCandidate = startpoint; posCandidate < numCandidates; posCandidate++) {
            do {
                candidates[posCandidate] = rng.nextInt(population.size());
            } while (!isUnique(posCurrent, candidates, posCandidate));
        }

        int dim = ind.getGenome().array().length;

        if(scaleFactorVariation == 1) {
            stepsize = rndStepsize();
        }

        for (int posGene = 0; posGene < dim; posGene++) {

            if (scaleFactorVariation == 2) {
                stepsize = rndStepsize();
            }

            if (numDA == 2) {
                ind.getGenome().array()[posGene] = doubleDifferentialAddition(stepsize, candidates[0], candidates[1], candidates[2], candidates[3], candidates[4], posGene);
            } else {
                ind.getGenome().array()[posGene] = singleDifferentialAddition(stepsize, candidates[0], candidates[1], candidates[2], posGene);

            }
        }
    }

    private float rndStepsize() {
        Random rng = new Random();
        return 0.4f + (0.9f - 0.4f) * rng.nextFloat();
    }

    /**
     * Performs a single differential addition of the genes A and B to the gene of the base vector
     *
     * @param stepsize stepsize
     * @param posBasis base vector
     * @param posA position of first candidate
     * @param posB position of second candidate
     * @param posGene Position of the gene to add
     * @return float value of the mutated gene
     */
    private float singleDifferentialAddition(float stepsize, int posBasis, int posA, int posB, int posGene) {
        return  getGene(posBasis,posGene) + stepsize * (getGene(posA,posGene) - getGene(posB,posGene));
    }

    /**
     * Performs a double differential addition of the genes A, B, C and D to the gene of the base vector
     *
     * @param stepsize stepsize
     * @param posA position of first candidate
     * @param posB position of second candidate
     * @param posC position of third candidate
     * @param posD position of fourth candidate
     * @param posGene Position of the gene to add
     * @return float value of the mutated gene
     */
    private float doubleDifferentialAddition(float stepsize, int posBasis, int posA, int posB, int posC, int posD, int posGene) {
        return  getGene(posBasis,posGene) + stepsize * (getGene(posA,posGene) + getGene(posB,posGene) - getGene(posC,posGene) - getGene(posD,posGene));
    }

    /**
     * Gives the gene on a specified position from Individual on the specified position in the population
     *
     * @param indPos Position of the Individual in the population
     * @param genePos Position of the interested gene
     * @return float value of the gene
     */
    private float getGene(int indPos, int genePos){
        return population.get(indPos).getGenome().array()[genePos];
    }
}
