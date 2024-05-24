package de.heaal.eaf.mutation;

import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.Population;

import java.util.Random;

public class DifferentialMutation implements Mutation{

    private Random rng;
    private Population population;

    public DifferentialMutation(Random rng) {this.rng = rng;}

    /**
     * Has to be set every iteration anew
     * @param population current population
     */
    public void setPopulation(Population population) { this.population = population; }

    @Override
    public void setRandom(Random rng) {
        this.rng = rng;
    }

    @Override
    public void mutate(Individual ind, MutationOptions opt) {
        float stepsize = opt.get(MutationOptions.KEYS.STEPSIZE, 0.5f);

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

}
