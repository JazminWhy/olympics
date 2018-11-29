package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.logging.log4j.Logger;

import com.wcohen.ss.JaroWinkler;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByBirthdayYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByEarliestParticipationYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByNameFirstLetters;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByNationality;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyForRio;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyForRioByNameNation_no_preprocessing;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyForRio_NoParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipationXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.ErrorAnalysis;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.pmml.jaxbbindings.NeuralNetwork;

/**
* Identity resolution using machine learning for rio and figshare dataset.
* 
* @author Jasmin Weimueller
* 
*/

@SuppressWarnings("unused")
public class IR_rio_figshare_machine_learning {
	
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	@SuppressWarnings("unused")
	private static final Logger logger = WinterLogManager.activateLogger("default");
	
    public static void main( String[] args ) throws Exception
    {
   
		// loading data
    	System.out.println("*\n*\tLoading datasets\n*");
    	HashedDataSet<Athlete, Attribute> dataAthletesRio = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181025_Rio_Final.xml"), "/WinningAthletes/Athlete", dataAthletesRio);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);
		
		/* we gave MLPs a try
		//TrainingTest
		
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File("data/goldstandard/newGS2.csv"));
		String[] options = new String[1]; 
		options[0] = "-H";
		loader.setOptions(options);
		Instances trainingSet = loader.getDataSet();
		//FileReader trainreaderSplit = new FileReader("data/goldstandard/gs_figshare_rio_balanced.csv");
		//Instances trainingSet = new Instances(trainreaderSplit);
		
		trainingSet.randomize(new java.util.Random(0));
		int trainSize = (int) Math.round(trainingSet.numInstances() * 0.6);
		int testSize = trainingSet.numInstances()-trainSize;
		Instances train = new Instances(trainingSet, 0, trainSize);
		Instances test = new Instances(trainingSet, trainSize, testSize);
		
		//Training
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		 try{
			//Reading training arff or csv file
				//FileReader trainreader = new FileReader(filepath);
				//Instances train = new Instances(trainreader);
			int num = train.numAttributes()-1;
			train.setClassIndex(num);
			//Instance of NN
				mlp = new MultilayerPerceptron();
			//Setting Parameters
				mlp.setLearningRate(0.3);
				mlp.setMomentum(0.2);
				mlp.setTrainingTime(2000);
				mlp.setValidationSetSize(20);
				mlp.setHiddenLayers("5");
				//mlp.buildClassifier(train);
				}
		catch(Exception ex){
				ex.printStackTrace();
		} 
		
		/*
		// Evaluation of Training
		Evaluation eval = new Evaluation(train);
    	eval.evaluateModel(mlp, train);
    	System.out.println(eval.errorRate()); //Printing Training Mean root squared Error
    	System.out.println(eval.toSummaryString()); //Summary of Training
		
    	/*
		//Cross 
    	int kfolds =10;
    	
		 eval.crossValidateModel(mlp, train, kfolds, new Random(1)); 
		 
		Instances datapredict = test;
		datapredict.setClassIndex(datapredict.numAttributes()-1);
		Instances predicteddata = new Instances(datapredict);
		//Predict Part
		for (int i = 0; i < datapredict.numInstances(); i++) {
		double clsLabel = mlp.classifyInstance(datapredict.instance(i));
		predicteddata.instance(i).setClassValue(clsLabel);
		}
		//Storing again in arff
		BufferedWriter writer = new BufferedWriter(
		new FileWriter("data/output/ML_correspondences.csv"));
		writer.write(predicteddata.toString());
		writer.newLine();
		writer.flush();
		writer.close(); 
		*/
		
		// create a matching rule
		String optionsModel[] = new String[] { "" };
		
		String modelType = "J48" //"RandomCommittee"
				+ ""; // use a logistic regression
		WekaMatchingRule<Athlete, Attribute> matchingRule = new WekaMatchingRule<>(0.6, modelType, optionsModel);
		//matchingRule.setClassifier(mlp);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000);
		matchingRule.setForwardSelection(true);
		
		// add comparators
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(2));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(3));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(4));
		matchingRule.addComparator(new AthleteNameComparatorEqual_NoBrackets());
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets());
		matchingRule.addComparator(new AthleteDBPediaBirthdayComparator5Years());
		matchingRule.addComparator(new AthleteHeightcomparatorsRange());
		matchingRule.addComparator(new AthleteWeightcomparatorsRange());
		matchingRule.addComparator(new AthleteSexComparator());
		matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein());
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(new Jaro()));
		
		
		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/gs_figshare_rio_balanced.csv"));

		// train the matching rule's model
		System.out.println("*\n*\tLearning matching rule\n*");
		RuleLearner<Athlete, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dataAthletesRio, dataAthletesFigshare, null, matchingRule, gsTraining);
		System.out.println(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
		
		// create a blocker (blocking strategy)
		// BLOCKER 1
		//StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyForRio_NoParticipation());
		
		// BLOCKER 2
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByNationality());

		// BLOCKER 3
		//StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyForRio());
		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();

		// execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesRio, dataAthletesFigshare, null, matchingRule,
				blocker);
		
		// global matching: create a maximum-weight, bipartite matching
		MaximumBipartiteMatchingAlgorithm<Athlete,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		maxWeight.run();
		correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/rio_figshare_ML_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_rio.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);
		
		// print the evaluation result
		System.out.println("Rio <-> figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
		
		ErrorAnalysis ea = new ErrorAnalysis();
		ea.printFalseNegatives(dataAthletesRio, dataAthletesFigshare, correspondences, gsTest);
		ea.printFalsePositives(correspondences, gsTest);
		
    }
}