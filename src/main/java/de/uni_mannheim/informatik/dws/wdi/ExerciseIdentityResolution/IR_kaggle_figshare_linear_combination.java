package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import org.apache.logging.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AthleteBlockingKeyByEarliestParticipationYearGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorMongeElkan;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteNameComparatorNGramJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedalComparator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AthleteParticipationMedalYearDisciplineTeamComparator;
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
* Identity resolution using linear combination for Kaggle and figshare dataset.
* 
* @author Hendrik Roeder & Tido Felix Marschall
* 
*/

@SuppressWarnings("unused")
public class IR_kaggle_figshare_linear_combination {
	/*
	 * Logging Options: default: level INFO - console trace: level TRACE - console
	 * infoFile: level INFO - console/file traceFile: level TRACE - console/file
	 * 
	 * To set the log level to trace and write the log to winter.log and console,
	 * activate the "traceFile" logger as follows: private static final Logger
	 * logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	@SuppressWarnings("unused")
	private static final Logger logger = WinterLogManager.activateLogger("default");

	public static List<String[]> DisciplineMapping;

	public static void main(String[] args) throws Exception {
		// loading data
		System.out.println("*\n*\tLoading datasets\n*");
		HashedDataSet<Athlete, Attribute> dataAthletesKaggle = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181027_Kaggle_Final.xml"), "/WinningAthletes/Athlete", dataAthletesKaggle);
		HashedDataSet<Athlete, Attribute> dataAthletesFigshare = new HashedDataSet<>();
		new AthleteXMLReader().loadFromXML(new File("data/input/20181029_figshare_Final.xml"), "/WinningAthletes/Athlete", dataAthletesFigshare);

		// Read discipline mapping
		CSVReader reader = new CSVReader(new FileReader("data/input/20181025_DisciplineMapping_final.csv"));
		DisciplineMapping = reader.readAll();
		reader.close();

		// load the training set
		MatchingGoldStandard kfTraining = new MatchingGoldStandard();
		kfTraining.loadFromCSVFile(new File("data/goldstandard/gs_figshare_kaggle.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Athlete, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.75);
		// 0.75 if MongeElkan is included, otherwise 0.57
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", -1, kfTraining);

		// add comparators
		//matchingRule.addComparator(new AthleteNameComparatorJaccard(), 0.6);
		//matchingRule.addComparator(new AthleteNameComparatorNGramJaccard(3), 0.6);
		matchingRule.addComparator(new AthleteNameComparatorMongeElkan(), 0.6);
		matchingRule.addComparator(new AthleteParticipationMedalYearDisciplineTeamComparator(), 0.4);
		//matchingRule.addComparator(new AthleteParticipationMedalComparator(), 0.4);

		// create a blocker (blocking strategy)
		StandardRecordBlocker<Athlete, Attribute> blocker = new StandardRecordBlocker<Athlete, Attribute>(
				new AthleteBlockingKeyByEarliestParticipationYearGenerator());
		blocker.setMeasureBlockSizes(true);
		
		// write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlockingTest.csv", 100);

		// initialize Matching Engine
		MatchingEngine<Athlete, Attribute> engine = new MatchingEngine<>();

		// execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Athlete, Attribute>> correspondences = engine
				.runIdentityResolution(dataAthletesKaggle, dataAthletesFigshare, null, matchingRule, blocker);

		// global matching: create a maximum-weight, bipartite matching
		MaximumBipartiteMatchingAlgorithm<Athlete, Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(
				correspondences);
		maxWeight.run();
		correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/kaggle_figshare_LC_correspondences.csv"),
				correspondences);

		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_kaggle.csv"));
		
		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");	
		MatchingEvaluator<Athlete, Attribute> evaluator = new MatchingEvaluator<Athlete, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

		// print the evaluation result
		System.out.println("Kaggle <-> figshare");
		System.out.println(String.format("Precision: %.4f", perfTest.getPrecision()));
		System.out.println(String.format("Recall: %.4f", perfTest.getRecall()));
		System.out.println(String.format("F1: %.4f", perfTest.getF1()));
		
		IR_ErrorAnalysis ea = new IR_ErrorAnalysis();
		ea.printFalseNegatives(dataAthletesKaggle, dataAthletesFigshare, correspondences, gsTest);
		ea.printFalsePositives(correspondences, gsTest);
	}
}
