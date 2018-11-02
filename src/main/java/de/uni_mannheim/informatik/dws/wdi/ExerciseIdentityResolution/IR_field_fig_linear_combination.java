package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;


import java.io.File;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyField;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
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


public class IR_field_fig_linear_combination {
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
    	// Test 3
		System.out.println("*\n*\tLoading datasets\n*");
		HashedDataSet<Athlete, Attribute> dataAthletesLeicht = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181102_FieldAthletes_Final.xml"), "/WinningAthletes/Athlete", dataAthletesLeicht);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);

		
		// load the training set
		MatchingGoldStandard kfTraining = new MatchingGoldStandard();
		kfTraining.loadFromCSVFile(new File("data/goldstandard/gs_fig_field_v1.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, kfTraining);
		
		// add comparators
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), .5);
		//matchingRule.addComparator(new AthleteParticipationMedal_inclYearDiscipline_Comparator(), 0.6);
		matchingRule.addComparator(new AthleteNameComparatorLevenshtein(), .5);
		//matchingRule.addComparator(new AthleteSexComparator(), 0.5);
		//matchingRule.addComparator(new AthleteNameComparatorLevenshtein(), 0.5);

		//matchingRule.addComparator(new AthleteBirthdayComparator2Years(), 0.25);
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyField());
		
		blocker.setMeasureBlockSizes(true);
		//Write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlockingTest.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();
		
		// Execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesLeicht, dataAthletesFigshare, null, matchingRule,
				blocker);

		// Create a top-1 global matching
		correspondences = engine.getTopKInstanceCorrespondences(correspondences, 2, 0.0);

		// Alternative: Create a maximum-weight, bipartite matching
		// MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		// maxWeight.run();
		// correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/field_figshare_Athlete_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_fig_field_v1.csv"));
		
		System.out.println("*\n*\tEvaluating result\n*");
		// evaluate your result
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		// print the evaluation result
		System.out.println("fieldathletes <-> figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}
