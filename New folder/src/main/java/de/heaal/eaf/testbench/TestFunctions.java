package de.heaal.eaf.testbench;

import de.heaal.eaf.base.Individual;

import java.util.function.Function;

public class TestFunctions {

    // Sphere Function n=2
    public Function<Individual,Float> evalSphereFunc2D =
            (ind) -> {
                var x0 = ind.getGenome().array()[0];
                var x1 = ind.getGenome().array()[1];
                return x0*x0 + x1*x1;
            };

    // Ackley Function n=2
    public Function<Individual,Float> evalAckleyFunc2D =
            (ind) -> {
                var x = ind.getGenome().array();

                var n = x.length;

                double sum1 = 0;
                double sum2 = 0;

                for (float v : x) {
                    sum1 += v * v;
                    sum2 += Math.cos(2 * Math.PI * v);
                }

                var term1 = -20*Math.exp(-0.2*Math.sqrt(sum1/n));
                var term2 = -Math.exp(sum2/n);
                return (float) (term1 + term2 + 20 + Math.exp(1));
            };
}
