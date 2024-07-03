package org.example;

import io.jenetics.*;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.*;
import io.jenetics.util.ISeq;

import static java.lang.Math.abs;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LocomotiveControl {
    static final int MAX_SIM_STEPS = 1000;
    static final double SEGMENT_LENGTH = 1.0;

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
    static final double[][] SAMPLES = new double[][] {
            {-1.0, -8.0000},
            {-0.9, -6.2460}
    };

    static double simulateTrain(final ProgramGene<Double> program) {
        //TODO LocomotiveControl loop
        final double stationDistance = 1000;
        double drivenDistance = 0;
        double energy = 0;
        double speed = 0;
        double time = 0;

        for (int simStep = 0; simStep < MAX_SIM_STEPS; simStep++) {
            speed += program.eval(speed, drivenDistance, stationDistance);

            drivenDistance += speed;
            energy += speed * speed;
            time++;

            if (drivenDistance >= stationDistance) {
                break;
            }
        }

        double error = abs(drivenDistance - stationDistance)*0.1 + energy*0.00 + time*0.0000 + program.size()*0.001;
        System.out.println("dist to station: " + (drivenDistance - stationDistance) + " error: " + error);

        return error;
    }

    static double error(final ProgramGene<Double> program) {

        double error = simulateTrain(program);

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
                        new SingleNodeCrossover<>(),
                        new Mutator<>())
                .build();

        final EvolutionResult<ProgramGene<Double>, Double> result = engine
                .stream()
                //.limit(Limits.byFitnessThreshold(0.01))
                .limit(100)
                .collect(EvolutionResult.toBestEvolutionResult());

        final ProgramGene<Double> program = result.bestPhenotype()
                .genotype()
                .gene();


        final TreeNode<Op<Double>> tree = program.toTreeNode();
        MathExpr.rewrite(tree); // Simplify result program.
        System.out.println("Generations: " + result.totalGenerations());
        System.out.println("Function:    " + new MathExpr(tree));
    }
}