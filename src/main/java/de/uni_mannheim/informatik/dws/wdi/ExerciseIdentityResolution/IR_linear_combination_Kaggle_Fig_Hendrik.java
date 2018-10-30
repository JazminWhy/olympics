package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByBirthdayYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByEarliestParticipationYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorNGramJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedalComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedalYearDisciplineTeamComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedal_inclYearDiscipline_Comparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteSexComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipationXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class IR_linear_combination_Kaggle_Fig_Hendrik 
{
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
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
    	// Test 3
		System.out.println("*\n*\tLoading datasets\n*");
		HashedDataSet<Athlete, Attribute> dataAthletesKaggle = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181027_Kaggle_Final.xml"), "/WinningAthletes/Athlete", dataAthletesKaggle);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);		
		
		//for(int i =0; i < op.size(); i++) {
		//	OlympicParticipation op1 = (OlympicParticipation) op.get(0);
		//	System.out.println(op1.getCity());
		//}
//		System.out.println(a.getName());
//		System.out.println(a.getHeight());
//		System.out.println(a.getWeight());
//		System.out.println(a.getBirthday());
//		System.out.println(a.getNationality());
//		System.out.println(a.getSex());
		
		
		
		//System.out.println(dataAthletes.getRecord("G-100001"));
		//System.out.println(dataOlympicParticipation.size());
		//System.out.println(dataAthletes);
		//System.out.println(dataOlympicParticipation.size());
		
		// load the training set
		MatchingGoldStandard kfTraining = new MatchingGoldStandard();
		kfTraining.loadFromCSVFile(new File("data/goldstandard/gs_kaggle_figshare_merged.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.58);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, kfTraining);
		
		// add comparators
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(3), 0.65);
		matchingRule.addComparator(new AthleteParticipationMedalYearDisciplineTeamComparator(), 0.35);
		//matchingRule.addComparator(new AthleteSexComparator(), 0.2);
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByEarliestParticipationYearGenerator());
//		NoBlocker<Movie, Attribute> blocker = new NoBlocker<>();
//		SortedNeighbourhoodBlocker<Athlete, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new AthleteBlockingKeyByEarliestParticipationYearGenerator(), 1500);
		blocker.setMeasureBlockSizes(true);
		//Write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlockingTest.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();
		
		// Execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesKaggle, dataAthletesFigshare, null, matchingRule,
				blocker);

		// Create a top-1 global matching
		correspondences = engine.getTopKInstanceCorrespondences(correspondences, 2, 0.0);

		// Alternative: Create a maximum-weight, bipartite matching
		// MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		// maxWeight.run();
		// correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/kaggle_figshare_Athlete_correspondences_top_2.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_kaggle_figshare_merged.csv"));
		
		System.out.println("*\n*\tEvaluating result\n*");
		// evaluate your result
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
