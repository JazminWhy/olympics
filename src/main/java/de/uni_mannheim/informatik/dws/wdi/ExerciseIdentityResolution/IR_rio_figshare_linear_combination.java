package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByNationality;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyForRio;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyForRio_NoParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteBirthdayComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteDBPediaBirthdayComparator5Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteHeightcomparatorsRange;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorEqual;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorEqual_NoBrackets;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorMongeElkan;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorMongeElkan_NoBrackets;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorNGramJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorNGramJaccard_NoBracket;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNationalityComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteSexComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteWeightcomparatorsRange;
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
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
* Identity resolution using a linear combination matching rule for rio and figshare dataset.
* 
* @author Jasmin Weimueller
* 
*/

@SuppressWarnings("unused")
public class IR_rio_figshare_linear_combination 
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
		

		// load the training set
		MatchingGoldStandard riofTraining = new MatchingGoldStandard();
		riofTraining.loadFromCSVFile(new File("data/goldstandard/gs_figshare_rio.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.6);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, riofTraining);
		
		
		// Name-MongeElkan; Name Jaccard
		
		//matchingRule.addComparator(new AthleteNameComparatorEqual(), 0.5);
		//matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), 0.5);
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(2), 0.4);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), 0.6); 
		

		// COMPLEX
		/*
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(2), 0.2);
		matchingRule.addComparator(new AthleteNameComparatorEqual_NoBrackets(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(), 0.2);
		matchingRule.addComparator(new AthleteDBPediaBirthdayComparator5Years(), 0.2);
		matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein(),0.1);
		matchingRule.addComparator(new AthleteHeightcomparatorsRange(0.1), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(new JaccardSimilarity()), 0.1);
		*/
		
		
		// other test
		/*
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(2), 0.4);
		matchingRule.addComparator(new AthleteNameComparatorEqual(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), 0.3);
		matchingRule.addComparator(new AthleteBirthdayComparator2Years(), 0.2);
		*/
		
		
		// other test
		/*
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard_NoBracket(2), 0.2);
		matchingRule.addComparator(new AthleteNameComparatorEqual_NoBracket(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(), 0.2);
		matchingRule.addComparator(new AthleteBirthdayComparator2Years(), 0.2);
		//matchingRule.addComparator(new AthleteDBPediaBirthdayComparator2Years(), 0.2);
		matchingRule.addComparator(new AthleteSexComparator(), 0.1);
		matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein(), 0.1);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan_NoBrackets(new Jaro()), 0.1);
		*/
		
		// create a blocker (blocking strategy)
		// BLOCKER 1
		//StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyForRio_NoParticipation());
		
		// BLOCKER 2
		//StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyByNationality());
		
		// BLOCKER 3
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyForRio());
		blocker.setMeasureBlockSizes(true);
		
		// write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlockingTest.csv", 100);
		
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
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/rio_figshare_LC_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_rio.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

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