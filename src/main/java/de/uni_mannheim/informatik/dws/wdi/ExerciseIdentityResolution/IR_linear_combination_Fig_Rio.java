package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByBirthdayYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByEarliestParticipationYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByNameFirstLetters;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByNationality;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteBirthdayComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorEqual;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorMongeElkan;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorNGramJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNationalityComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNationalityComparatorMongeElkan;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedalComparator;
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

public class IR_linear_combination_Fig_Rio 
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
		HashedDataSet<Athlete, Attribute> dataAthletesRio = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181025_Rio_Final.xml"), "/WinningAthletes/Athlete", dataAthletesRio);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);
		
		Athlete a = dataAthletesRio.getRecord("R-100001");
		Athlete p = dataAthletesFigshare.getRecord("fig_10004");		
		
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
		MatchingGoldStandard riofTraining = new MatchingGoldStandard();
		riofTraining.loadFromCSVFile(new File("data/goldstandard/gs_rio_fig.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.4);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, riofTraining);
		
		// add comparators
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(2), 0.4);
		matchingRule.addComparator(new AthleteNameComparatorEqual(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), 0.3);
		matchingRule.addComparator(new AthleteBirthdayComparator2Years(), 0.2);
		//matchingRule.addComparator(new AthleteNationalityComparatorMongeElkan(), 0.15);
		//matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein(), 0.3);
		
		
		// create a blocker (blocking strategy)
		//StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByNationality());
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByNameFirstLetters());
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
				dataAthletesRio, dataAthletesFigshare, null, matchingRule,
				blocker);

		// Create a top-1 global matching
		correspondences = engine.getTopKInstanceCorrespondences(correspondences, 2, 0.0);

		// Alternative: Create a maximum-weight, bipartite matching
		// MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		// maxWeight.run();
		// correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/rio_figshare_Athlete_correspondences_top_2.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_rio_fig.csv"));
		
		System.out.println("*\n*\tEvaluating result\n*");
		// evaluate your result
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		// print the evaluation result
		System.out.println("Rio <-> Figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}
