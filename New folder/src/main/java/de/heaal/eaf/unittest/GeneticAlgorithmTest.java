package de.heaal.eaf.unittest;

import de.heaal.eaf.algorithm.Particle;
import de.heaal.eaf.base.Individual;
import de.heaal.eaf.base.Population;
import de.heaal.eaf.base.VecN;
import de.heaal.eaf.crossover.AverageCrossover;
import de.heaal.eaf.crossover.Combination;
import de.heaal.eaf.evaluation.MinimizeFunctionComparator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static de.heaal.eaf.algorithm.GeneticAlgorithm.*;
import static org.junit.Assert.*;

public class GeneticAlgorithmTest {

    @Test
    public void testFitnessFunctionPos() {

        Individual ind = new Particle(new VecN(new float[]{1.0f, 2.0f}));
        float res1 = FitnessPosCoordinates.apply(ind);
        assertEquals(3.0f, res1, 0.0001f);

        ind = new Particle(new VecN(new float[]{-1.0f, 3.0f}));
        float res2 = FitnessPosCoordinates.apply(ind);
        assertEquals(4.0f, res2, 0.0001f);

        ind = new Particle(new VecN(new float[]{-2.0f, -3.0f}));
        float res3 = FitnessPosCoordinates.apply(ind);
        assertEquals(5.0f, res3, 0.0001f);

        ind = new Particle(new VecN(new float[]{0.0f, 0.0f}));
        float res4 = FitnessPosCoordinates.apply(ind);
        assertEquals(0.0f, res4, 0.0001f);
    }
    @Test
    public void testFitnessFunctionNeg() {

        Individual ind = new Particle(new VecN(new float[]{1.0f, 2.0f}));
        float res1 = FitnessNegCoordinates.apply(ind);
        assertEquals(-3.0f, res1, 0.0001f);

        ind = new Particle(new VecN(new float[]{-1.0f, 3.0f}));
        float res2 = FitnessNegCoordinates.apply(ind);
        assertEquals(-4.0f, res2, 0.0001f);

        ind = new Particle(new VecN(new float[]{-1.0f, -4.0f}));
        float res3 = FitnessNegCoordinates.apply(ind);
        assertEquals(-5.0f, res3, 0.0001f);

        ind = new Particle(new VecN(new float[]{0.0f, 0.0f}));
        float res4 = FitnessNegCoordinates.apply(ind);
        assertEquals(0.0f, res4, 0.0001f);
    }

    /**
     * Test for ensuring that fitness function and sorting of the population work together as intended.
     * After the sorting the population has to be sorted descending from most to less fit Individual
     */
    @Test
    public void testPopulationSort() {
        Comparator<Individual> cmp = new MinimizeFunctionComparator(FitnessPosCoordinates);
        Population pop = new Population(0);

        Individual ind1 = new Particle(new VecN(new float[]{0.0f, 1.0f}));
        pop.add(ind1);
        Individual ind2 = new Particle(new VecN(new float[]{0.0f, 0.0f}));
        pop.add(ind2);
        Individual ind3 = new Particle(new VecN(new float[]{1.0f, 1.0f}));
        pop.add(ind3);
        Individual ind4 = new Particle(new VecN(new float[]{-2.0f, -2.0f}));
        pop.add(ind4);

        pop.sort(cmp);

        Population popExpected = new Population(0);

        popExpected.add(ind2);
        popExpected.add(ind1);
        popExpected.add(ind3);
        popExpected.add(ind4);

        //  Careful! Individuals with the same genome are not the same objects if using assertEquals()!
        assertEquals(popExpected.get(0), pop.get(0));
        assertEquals(popExpected.get(1), pop.get(1));
        assertEquals(popExpected.get(2), pop.get(2));
        assertEquals(popExpected.get(3), pop.get(3));
    }

    /**
     * Test of the average crossover.
     * The child of 2 parental Individuals becomes average values for each allele from both parents
     */
    @Test
    public void testAverageCrossover() {
        Combination comb = new AverageCrossover();

        Individual[] parents = new Individual[]{
                new Particle(new VecN(new float[]{0.0f, 0.0f})),
                new Particle(new VecN(new float[]{0.0f, 0.0f}))
        };
        Individual ind1 = comb.combine(parents);
        Individual ind1Expected = new Particle(new VecN(new float[]{0.0f, 0.0f}));
        assertArrayEquals(ind1Expected.getGenome().array(), ind1.getGenome().array(), 0.001F);

        parents = new Individual[]{
                new Particle(new VecN(new float[]{2.0f, 4.0f})),
                new Particle(new VecN(new float[]{2.0f, 4.0f}))
        };
        Individual ind2 = comb.combine(parents);
        Individual ind2Expected = new Particle(new VecN(new float[]{2.0f, 4.0f}));
        assertArrayEquals(ind2Expected.getGenome().array(), ind2.getGenome().array(), 0.001F);

        parents = new Individual[]{
                new Particle(new VecN(new float[]{-3.0f, 1.0f})),
                new Particle(new VecN(new float[]{-3.0f, 1.0f}))
        };
        Individual ind3 = comb.combine(parents);
        Individual ind3Expected = new Particle(new VecN(new float[]{-3.0f, 1.0f}));
        assertArrayEquals(ind3Expected.getGenome().array(), ind3.getGenome().array(), 0.001F);

        parents = new Individual[]{
                new Particle(new VecN(new float[]{1.0f, 2.0f})),
                new Particle(new VecN(new float[]{5.0f, -6.0f}))
        };
        Individual ind4 = comb.combine(parents);
        Individual ind4Expected = new Particle(new VecN(new float[]{3.0f, -2.0f}));
        assertArrayEquals(ind4Expected.getGenome().array(), ind4.getGenome().array(), 0.001F);

        parents = new Individual[]{
                new Particle(new VecN(new float[]{2.0f, 4.0f})),
                new Particle(new VecN(new float[]{2.0f, 4.0f}))
        };
        Individual ind5 = comb.combine(parents);
        Individual ind5Expected = new Particle(new VecN(new float[]{1.0f, 5.0f}));
        assertFalse(Arrays.equals(ind5Expected.getGenome().array(), ind5.getGenome().array()));
    }
}
