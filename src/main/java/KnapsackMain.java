import dao.Cliente;
import dao.Recommendation;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import problem.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import org.uma.jmetal.util.observer.impl.FitnessObserver;


public class KnapsackMain extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws IOException {

        Knapsack problem = new Knapsack("Knapsack_instances/KnapsackInstance_50_0_0.kp");

        Algorithm<BinarySolution> algorithm;
        CrossoverOperator<BinarySolution> crossover;
        MutationOperator<BinarySolution> mutation;
        SelectionOperator<List<BinarySolution>, BinarySolution> selection;


        crossover = new SinglePointCrossover(0.9) ;

        double mutationProbability = 1.0 / problem.bitsFromVariable(0) ;
        mutation = new BitFlipMutation(mutationProbability) ;

        selection = new BinaryTournamentSelection<BinarySolution>();

        algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(25000)
                .setSelectionOperator(selection)
                .build() ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();


        BinarySolution solution = algorithm.getResult() ;
        List<BinarySolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        JMetalLogger.logger.info("Fitness: " + (-1)*solution.objectives()[0]) ;
        JMetalLogger.logger.info("Solution: " + solution.variables().get(0)) ;


    }
}