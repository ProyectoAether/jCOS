package utils.semantic;

import Jama.SingularValueDecomposition;
import dao.Cliente;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import problem.DatasetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestElectricas {

  public static void main(String[] args) throws IOException {

      OWLUtils owlUtils = new OWLUtils();
      List<Cliente> clientes = DatasetUtils.loadClientsConquense("input/conquense_output_split.json", 5);

      owlUtils.generateJSON("mijson.json", clientes);



    OWLOntologyManager manager;
    OWLOntology ontology;
    OWLDataFactory factory;
    IRI iri = null;
    String pathOWL = "src/main/resources/ontology/old_electricity-problem.owl";
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    manager
        .getIRIMappers()
        .add(
            new SimpleIRIMapper(
                IRI.create("http://www.ontologies.khaos.uma.es/data-constraint"),
                IRI.create(
                    "file:///home/cbarba/IdeaProjects/jMetalPrueba/src/main/resources/ontology/data-constraint.owl")));
    manager
        .getIRIMappers()
        .add(
            new SimpleIRIMapper(
                IRI.create("http://www.ontologies.khaos.uma.es/bigowl"),
                IRI.create(
                    "file:///home/cbarba/IdeaProjects/jMetalPrueba/src/main/resources/ontology/bigowlv6.owl")));

      manager
              .getIRIMappers()
              .add(
                      new SimpleIRIMapper(
                              IRI.create("http://www.ontologies.khaos.uma.es/electricity-company"),
                              IRI.create(
                                      "file:////home/cbarba/IdeaProjects/jMetalPrueba/src/main/resources/ontology/electricity-company.owl")));

    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);

      if (ontology.getOntologyID().getOntologyIRI().isPresent()) {
        iri = ontology.getOntologyID().getOntologyIRI().get();
      }
      System.out.println("Leida ontologia " + iri.getIRIString());

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        OWLClass constraint =
                factory.getOWLClass(
                        IRI.create("http://www.ontologies.khaos.uma.es/bigowl/Constraint"));

        System.out.println(constraint);

        OWLNamedIndividual cop =
                factory.getOWLNamedIndividual(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/COP"));

        OWLNamedIndividual cliente =
                factory.getOWLNamedIndividual(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/Client"));
//        System.out.println(consumoMenor85);

        OWLObjectProperty lowerThan =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/lowerThan"));

        OWLObjectProperty greaterThan =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/greaterThan"));
        OWLObjectProperty smallerThan =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/smallerThan"));
        OWLObjectProperty appliesTo =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/appliesTo"));

        OWLObjectProperty hasConstraint =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/bigowl/hasConstraint"));

        OWLObjectProperty hasContract =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-company/hasContract"));


        OWLObjectProperty hasContractedPower =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-company/hasContractedPower"));

        OWLDataProperty hasValueConsumption =
                factory.getOWLDataProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/hasValueConsumption"));


        OWLDataProperty isMax =
                factory.getOWLDataProperty(
                        IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/isMax"));




        NodeSet<OWLNamedIndividual> contractClients = reasoner.getObjectPropertyValues(cliente,hasContract);



        NodeSet<OWLNamedIndividual> constraints =
                reasoner.getObjectPropertyValues(cop, hasConstraint);

        List<OWLNamedIndividual> constr = new ArrayList<>(constraints.getFlattened());
        for (Node<OWLNamedIndividual> contra:contractClients) {
            OWLNamedIndividual cli = contra.iterator().next();
            List<OWLNamedIndividual>  contractsPower = getIndividualsByObjectProperty(reasoner,cli,hasContractedPower);
            List<OWLNamedIndividual> prueba = getConstrainsByClientPowerContracted(reasoner,constr,contractsPower,appliesTo);
            int x=0;

        }

        constraints.forEach(
                myConstraint -> {
                    List<OWLNamedIndividual> lowerList = null;
                    List<OWLNamedIndividual> smallerList = null;
                    List<OWLNamedIndividual> greaterList = null;

                    OWLNamedIndividual individual = myConstraint.iterator().next();
                    lowerList = getIndividualsByObjectProperty(reasoner, individual, lowerThan);
                    smallerList = getIndividualsByObjectProperty(reasoner, individual, smallerThan);
                    greaterList = getIndividualsByObjectProperty(reasoner, individual, greaterThan);

                    if(lowerList!=null && !lowerList.isEmpty()){
                        for (OWLNamedIndividual indv:lowerList) {
                            int value = getValueFromConsumption(reasoner,indv,hasValueConsumption);
                            int xx =0;
                        }
                    }


                   // List<OWLLiteral> isMaxs = getDataPropertyByIndividual(reasoner,individual,isMax);

                }
                    );

       /*




        OWLObjectProperty hasLocation =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasLocation"));
        OWLObjectProperty attaches =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#attaches"));
        OWLDataProperty hasX =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasX"));
        OWLDataProperty hasY =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasY"));
        OWLDataProperty hasMinimumCoverage =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasMinimumCoverage"));
        NodeSet<OWLNamedIndividual> antennas =
                reasoner.getObjectPropertyValues(newYorkGridConstraint, hasAntenna);

        List<OWLLiteral> minimumCoverageList= getDataPropertyByIndividual(reasoner, newYorkGridConstraint, hasMinimumCoverage);
        if(minimumCoverageList!=null && !minimumCoverageList.isEmpty()){
            double coverage = Double.valueOf(minimumCoverageList.get(0).getLiteral());
            int xx =0;
        }
      antennas.forEach(
          antenna -> {
            List<OWLNamedIndividual> avoidlist = null;
            List<OWLNamedIndividual> locations = null;
            List<OWLNamedIndividual> attachList = null;
            List<OWLLiteral> x = null;
            List<OWLLiteral> y = null;

            avoidlist = getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), avoids);
            locations =
                getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), hasLocation);
            attachList =
                getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), attaches);
            if (avoidlist != null && !avoidlist.isEmpty()) {
              for (OWLNamedIndividual ind : avoidlist) {
                List<OWLNamedIndividual> locationsAux =
                    getIndividualsByObjectProperty(reasoner, ind, hasLocation);
                if (locationsAux != null && !locationsAux.isEmpty()) {
                  int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                  int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                  int xx = 0;
                }
              }
            }
            if (locations != null && !locations.isEmpty()) {
              int xValue = getValueFromLocation(reasoner, locations.get(0), hasX);
              int yValue = getValueFromLocation(reasoner, locations.get(0), hasY);
                int xx = 0;
            }
            if (attachList != null && !attachList.isEmpty()) {
              for (OWLNamedIndividual ind : attachList) {
                List<OWLNamedIndividual> locationsAux =
                    getIndividualsByObjectProperty(reasoner, ind, hasLocation);
                if (locationsAux != null && !locationsAux.isEmpty()) {
                  int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                  int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                  int xx = 0;
                }
              }
            }

            int xx = 0;
          });*/

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static int getValueFromConsumption(OWLReasoner reasoner,OWLNamedIndividual individual,OWLDataProperty dataProperty){
      int result = 0;
      List<OWLLiteral> aux = getDataPropertyByIndividual(reasoner,individual,dataProperty);
      if(aux!=null && !aux.isEmpty()){
          result= Integer.valueOf(aux.get(0).getLiteral());
      }
      return result;
  }

  public static List<OWLNamedIndividual> getConstrainsByClientPowerContracted(OWLReasoner reasoner,List<OWLNamedIndividual> constraints, List<OWLNamedIndividual> powerContracts,OWLObjectProperty objectProperty){
      List<OWLNamedIndividual> result =new ArrayList<>();
      for (OWLNamedIndividual constraint: constraints) {
          List<OWLNamedIndividual> aux = getIndividualsByObjectProperty(reasoner,constraint,objectProperty);
         if(!aux.stream().filter(indv -> powerContracts.contains(indv)).collect(Collectors.toList()).isEmpty()){
             result.add(constraint);
         }
      }
      if(!result.isEmpty()){
          result= result.stream().distinct().collect(Collectors.toList());
      }
      return result;
  }
    public static List<OWLNamedIndividual> getIndividualsByObjectProperty(
            OWLReasoner reasoner, OWLNamedIndividual individual, OWLObjectProperty objectProperty) {
        List<OWLNamedIndividual> result = new ArrayList<>();
        NodeSet<OWLNamedIndividual> nodes =
                reasoner.getObjectPropertyValues(individual, objectProperty);
        if (nodes != null) {
            nodes.forEach(nodeIndiv -> nodeIndiv.forEach(indiv -> result.add(indiv)));
        }
        return result;
    }

    public static List<OWLNamedIndividual> getIndividualsByOClass(
            OWLReasoner reasoner, OWLClassExpression owlclass) {
        List<OWLNamedIndividual> result = new ArrayList<>();
        NodeSet<OWLNamedIndividual> nodes =
                reasoner.getInstances(owlclass, false);
        result.addAll(nodes.getFlattened());
        return result;
    }

    public static List<OWLLiteral> getDataPropertyByIndividual(
            OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
        List<OWLLiteral> result = new ArrayList<>();
        Set<OWLLiteral> nodes = reasoner.getDataPropertyValues(individual, dataProperty);
        if (nodes != null) {
            nodes.stream().forEach(owlLiteral -> result.add(owlLiteral));
        }
        return result;
    }
}
