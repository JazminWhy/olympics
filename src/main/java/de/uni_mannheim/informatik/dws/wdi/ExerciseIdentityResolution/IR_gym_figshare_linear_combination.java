package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import org.apache.logging.log4j.Logger;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyGymnast_NoPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
* Identity resolution using linear combination for gymnasts and figshare dataset.
* 
* @author Marius Bock
* 
*/

public class IR_gym_figshare_linear_combination {
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
		HashedDataSet<Athlete, Attribute> dataAthletesGymnast = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181027_Gymnasts_Final.xml"), "/WinningAthletes/Athlete", dataAthletesGymnast);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);
		

		// load the training set
		MatchingGoldStandard kfTraining = new MatchingGoldStandard();
		kfTraining.loadFromCSVFile(new File("data/goldstandard/gs_figshare_gymnasts.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.6);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, kfTraining);
		
		// add comparators
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(2), 0.4);
		matchingRule.addComparator(new AthleteNameComparatorEqual_NoBrackets(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(), 0.4);
		matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein(), 0.1);
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyGymnast_NoPreprocessing());
	
		blocker.setMeasureBlockSizes(true);
		// write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlockingTest.csv", 100);
		
		// initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();
		
		// execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesGymnast, dataAthletesFigshare, null, matchingRule,
				blocker);

		// global matching: create a maximum-weight, bipartite matching
		MaximumBipartiteMatchingAlgorithm<Athlete,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		maxWeight.run();
		correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/gymnasts_figshare_LC_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_gymnasts.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

		// print the evaluation result
		System.out.println("Gymnasts <-> figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
		
		// perform error analysis
		IR_ErrorAnalysis ea = new IR_ErrorAnalysis();
		ea.printFalseNegatives(dataAthletesGymnast, dataAthletesFigshare, correspondences, gsTest);
		ea.printFalsePositives(correspondences, gsTest);
    }
}
