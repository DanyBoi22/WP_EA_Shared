package org.example;

import io.jenetics.Genotype;
import io.jenetics.MultiPointCrossover;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.util.ISeq;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LocomotiveControl {

    static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
            MathOp.ADD,
            MathOp.SUB,
            MathOp.MUL
    );

    static final ISeq<Op<Double>> TERMINALS = ISeq.of(
            //Speed
            Var.of("v", 0),
            //Distance Traveled
            Var.of("dt", 1),
            //Distance to station
            Var.of("ds", 2)
    );

    static final ProgramChromosome<Double> PROGRAM =
            ProgramChromosome.of(5, OPERATIONS, TERMINALS);

    static final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC = Codec.of(
            Genotype.of(PROGRAM),
            Genotype::gene
    );

    // The lookup table where the data points are stored.
    static final double[][] SAMPLES = new double[][]{
            {-1.0, -8.0000},
            {-0.9, -6.2460}
    };

    static double simulateTrain(final ProgramGene<Double> program, boolean printProgress) {
        if (printProgress) {
            System.out.println("Simulating train...");
        }

        double MAX_VELOCITY = 10;
        final int MAX_SIM_STEPS = 100;
        final double MAX_ENERGY = 1574800; //Math.pow(MAX_VELOCITY * MAX_SIM_STEPS, 2);

        //TODO LocomotiveControl loop
        final double stationDistance = 500;
        double drivenDistance = 0;
        double energy = 0;
        double velocity = 0;
        int steps = 0;

        for (int simStep = 0; simStep < MAX_SIM_STEPS; simStep++) {
            double new_speed = program.eval(velocity, drivenDistance, stationDistance);

            energy += new_speed * new_speed;

            if (new_speed > MAX_VELOCITY) {
                new_speed = MAX_VELOCITY;
            }

            velocity += new_speed;

            drivenDistance += velocity;

            steps++;

            if (printProgress) {
                System.out.println("v: " + velocity);
            }


            if (drivenDistance >= stationDistance) {
                if (printProgress) {
                    System.out.println("Train arrived");
                }
                break;
            }
        }

        double error =
                Math.pow(((drivenDistance - stationDistance) / stationDistance)*1.0, 2) +
                Math.pow(((velocity / MAX_VELOCITY) * 2.0), 2) +
                Math.pow(energy/MAX_ENERGY, 2) * 0.0000000000 +
                steps * 10.0 +
                program.size() * 0.001;

        if (printProgress) {
            System.out.println("dist to station: " + (drivenDistance - stationDistance) + " steps: " + steps + " energy: " + energy + " error: " + error);
        }


        return error;
    }

    static double error(final ProgramGene<Double> program) {

        double error = simulateTrain(program, false);

        return error;
    }

    public static void main(final String[] args) {
        final Engine<ProgramGene<Double>, Double> engine = Engine
                .builder(
                        LocomotiveControl::error,
                        CODEC
                )
                .minimizing()
                .alterers(
                        new MultiPointCrossover<>(),
                        new Mutator<>())
                .build();

        final EvolutionResult<ProgramGene<Double>, Double> result = engine
                .stream()
                //.limit(Limits.byFitnessThreshold(0.01))
                .limit(1000)
                .collect(EvolutionResult.toBestEvolutionResult());

        final ProgramGene<Double> program = result.bestPhenotype()
                .genotype()
                .gene();

        final TreeNode<Op<Double>> tree = program.toTreeNode();
        MathExpr.rewrite(tree); // Simplify result program.
        System.out.println("Generations: " + result.totalGenerations());
        System.out.println("Function:    " + new MathExpr(tree));

        simulateTrain(program, true);
    }
}