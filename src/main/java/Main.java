import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.Cliente;
import dao.Recommendation;
import org.json.simple.parser.ParseException;
import problem.DatasetUtils;
import problem.Electrico;
import problem.GenerateRecommendations;
import problem.ProblemUtils;
import utils.semantic.JSONUtils;
import utils.semantic.OWLUtils;
//import org.uma.jmetal.util.observer.impl.FitnessObserver;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        double[] precio = {0.25, 0.17, 0.30};





        List<Cliente> clientes = DatasetUtils.loadClientsConquense("input/conquense_output_split.json", 5);
        /*for (Cliente cliente:clientes
             ) {
            System.out.println(cliente.getCups());
        }*/
        OWLUtils owlUtils = new OWLUtils();
        owlUtils.generateJSON("electrica.json", clientes);
        for (Cliente c:clientes) {
            c.setContractList(new ArrayList<>());
        }
        JSONUtils jsonUtils = new JSONUtils();
        clientes = jsonUtils.parseJSON("electrica.json",clientes);
        List<Electrico> problems = ProblemUtils.createProblems(clientes, precio);
        List<Recommendation> recommendations = GenerateRecommendations.generate(problems);
        //problem.DatasetUtils.writeRecommendationsToCsv(recommendations, "input/results.csv");

        System.out.println(recommendations);

//        System.out.println("Prueba");
//
//        double[][] consumo = {
//                {18.0, 20.0, 17.0},
//                {21.0, 18.0, 21.0},
//                {18.0, 19.0, 18.0},
//                {19.0, 20.0, 19.0},
//                {21.0, 22.0, 14.0}
//        };
//
//        double[] precio = {0.25, 0.17, 0.30};
//
//        ElectricoMini problem = new ElectricoMini(consumo, precio);
//
//        double crossoverProbability = 0.9;
//        double crossoverDistributionIndex = 20.0;
//        var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
//
//        double mutationProbability = 1.0 / problem.getNumberOfVariables();
//        double mutationDistributionIndex = 20.0;
//        var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
//
//        var selection = new NaryTournamentSelection<DoubleSolution>(2, new ObjectiveComparator<>(0));
//
//        //Termination termination = new TerminationByEvaluations(150000);
//        Termination termination = new TerminationByComputingTime( 5 * 1000);
//
//        int populationSize = 1000;
//        int offspringPopulationSize = 1000;
//
//        var algorithm =
//                new NSGAII<>(
//                        problem,
//                        populationSize,
//                        offspringPopulationSize,
//                        crossover,
//                        mutation,
//                        termination);
//
//        //algorithm.getObservable().register(new FitnessObserver(100));
//
//        algorithm.run();
//
//        List<DoubleSolution> population = algorithm.getResult();
//        JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
//        JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());
//
//        DoubleSolution solution = (DoubleSolution) algorithm.getResult().get(0);
//
//        System.out.println(solution);
//
//        System.out.println(problem.calculaTp(List.of(18.0, 20.96, 20.0)));
//        //System.out.println(problem.calculaTp(List.of(4.36, 3.82, 57.47)));
//        //System.out.println(problem.calculaTp(List.of(87.89, 457.31, 76.78)));
    }
}