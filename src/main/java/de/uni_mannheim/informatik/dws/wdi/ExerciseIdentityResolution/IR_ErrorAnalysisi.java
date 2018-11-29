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
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
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
public class IR_ErrorAnalysisi {
	
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
		
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		Processable<Correspondence<Athlete, Attribute>> correspondences;
		MatchingGoldStandard correspondencesgs = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_figshare_rio.csv"));
		correspondencesgs.loadFromCSVFile(new File("data/output/rio_figshare_ML_correspondences.csv"));
		correspondences = (Processable<Correspondence<Athlete, Attribute>>) correspondencesgs;
		ErrorAnalysis ea = new ErrorAnalysis();
		ea.printFalseNegatives(dataAthletesRio, dataAthletesFigshare, correspondences, gsTest);
		ea.printFalsePositives(correspondences, gsTest);
		
    }
}