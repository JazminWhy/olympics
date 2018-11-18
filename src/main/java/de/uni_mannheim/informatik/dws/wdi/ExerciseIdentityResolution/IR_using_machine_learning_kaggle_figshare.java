package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.io.FileReader;

import org.apache.logging.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByBirthdayYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByEarliestParticipationYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipationXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class IR_using_machine_learning_kaggle_figshare {
	
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

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
	public static List<String[]> DisciplineMapping;
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
    	System.out.println("*\n*\tLoading datasets\n*");
		HashedDataSet<Athlete, Attribute> dataAthletesKaggle = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181027_Kaggle_Final.xml"), "/WinningAthletes/Athlete", dataAthletesKaggle);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);	

		CSVReader reader = new CSVReader(new FileReader("data/input/20181025_discipline mapping_final.csv"));
		DisciplineMapping = reader.readAll();
		
		reader.close();
		
		// create a matching rule
		String options[] = new String[] { "" };
		String modelType = "SimpleLogistic"
				+ ""; // use a logistic regression
		WekaMatchingRule<Athlete, Attribute> matchingRule = new WekaMatchingRule<>(0.4, modelType, options);
		// 0.1 for NaiveBayes, 0.4 for SimpleLogistic, 0.2 for J48
		
		matchingRule.setBackwardSelection(true);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000);
		
		// add comparators
		//TODO
		matchingRule.addComparator(new AthleteParticipationMedal_inclYearDiscipline_Comparator());
		matchingRule.addComparator(new AthleteParticipationMedalYearDisciplineTeamComparatorML());
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(2));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(3));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(4));
		matchingRule.addComparator(new AthleteNameComparatorJaccard());
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan());
		
		
		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/gs_kaggle_figshare_merged_2.csv")); //TODO

		// train the matching rule's model
		System.out.println("*\n*\tLearning matching rule\n*");
		RuleLearner<Athlete, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dataAthletesKaggle, dataAthletesFigshare, null, matchingRule, gsTraining);
		System.out.println(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByEarliestParticipationYearGenerator());
//		SortedNeighbourhoodBlocker<Athlete, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new AthleteBlockingKeyByDecadeGenerator(), 1);
		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesKaggle, dataAthletesFigshare, null, matchingRule,
				blocker);
		
		// Global matching
		// correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);
		
		// Alternative: Create a maximum-weight, bipartite matching
		MaximumBipartiteMatchingAlgorithm<Athlete,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		maxWeight.run();
		correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/kaggle_fingshare_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_kaggle_figshare_merged_2.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);
		
		// print the evaluation result
		System.out.println("Kaggle <-> figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}
