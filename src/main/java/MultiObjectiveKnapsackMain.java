import dao.KnapsackConstraint;
import org.json.simple.parser.ParseException;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;

import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import problem.MultiobjectiveKnapsackProblem;
import utils.semantic.JSONUtils;
import utils.semantic.OWLUtils;

import java.io.IOException;
import java.util.List;


public class MultiObjectiveKnapsackMain {
    public static void main(String[] args) throws IOException, ParseException {
      //  int[] values = {7, 10, 6, 2, 1};
      //  int[] weights = {3, 4, 2, 1, 1};
     //   int capacity = 8;


        OWLUtils owlUtils = new OWLUtils("src/main/resources/ontology/knapsack-problem.owl");
        owlUtils.generateJSONConstrainsKnapsack("knapsack.json");
        Problem<IntegerSolution> problem = new MultiobjectiveKnapsackProblem("Knapsack_instances/KnapsackInstance_50_0_0.kp");
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(crossoverProbability,crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.numberOfVariables();
        double mutationDistributionIndex = 20.0;
        MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(mutationProbability,mutationDistributionIndex);

        SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<>(
                new RankingAndCrowdingDistanceComparator<>());
        int populationSize = 100;
        NSGAII<IntegerSolution> algorithm =
                new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(25000)
                        .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();
        List<IntegerSolution> solutions = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(solutions)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");


    }

}
