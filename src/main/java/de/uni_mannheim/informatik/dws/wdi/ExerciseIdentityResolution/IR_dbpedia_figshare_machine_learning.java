package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import org.apache.logging.log4j.Logger;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyDBPedia;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AthleteXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

/**
* Identity resolution using machine learning for DBpedia and figshare dataset.
* 
* @author Maximilian Philipp Barth
* 
*/

public class IR_dbpedia_figshare_machine_learning {
	
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
		HashedDataSet<Athlete, Attribute> dataAthletesDBpedia = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181025_DBpedia_Final.xml"), "/WinningAthletes/Athlete", dataAthletesDBpedia);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);
		
		// create a matching rule
		String options[] = new String[] { "" };
		// choose from list: NaiveBayes, SimpleLogistic, DecisionTable, ...
		String modelType = "NaiveBayes" + "";
		WekaMatchingRule<Athlete, Attribute> matchingRule = new WekaMatchingRule<>(0.6005, modelType, options);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000);
		matchingRule.setBackwardSelection(true);
		
		// add comparators
		matchingRule.addComparator(new AthleteDBPediaBirthdayComparator5Years());
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(2));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(3));
		matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(4));
		matchingRule.addComparator(new AthleteNameComparatorJaccard());
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan());
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(new JaroWinkler()));
		matchingRule.addComparator(new AthleteDBPediaNationalityComparatorMongeElkan());
		matchingRule.addComparator(new AthleteNationalityComparatorLevenshtein());
		
		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/gs_figshare_dbpedia_balanced.csv"));

		// train the matching rule's model
		System.out.println("*\n*\tLearning matching rule\n*");
		RuleLearner<Athlete, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dataAthletesDBpedia, dataAthletesFigshare, null, matchingRule, gsTraining);
		System.out.println(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(new AthleteBlockingKeyDBPedia());
		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();

		// execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine.runIdentityResolution(
				dataAthletesDBpedia, dataAthletesFigshare, null, matchingRule,
				blocker);
		
		// global matching: create a maximum-weight, bipartite matching
		MaximumBipartiteMatchingAlgorithm<Athlete,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		maxWeight.run();
		correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/DBpedia_figshare_ML_correspondences.csv"), correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_dbpedia.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);
		
		// print the evaluation result
		System.out.println("DBpedia <-> figshare");
		System.out.println(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f",perfTest.getF1()));
		
		// print error analysis
		IR_ErrorAnalysis eAnalysis = new IR_ErrorAnalysis();
		eAnalysis.printFalseNegatives(dataAthletesDBpedia,dataAthletesFigshare,correspondences,gsTest);
		eAnalysis.printFalsePositives(correspondences, gsTest);
    }
}