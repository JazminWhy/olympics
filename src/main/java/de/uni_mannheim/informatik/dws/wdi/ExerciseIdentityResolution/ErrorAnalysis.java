package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class ErrorAnalysis {
	
	private static final Logger logger = WinterLogManager.getLogger();

	public void printFalsePositives(Processable<Correspondence<Athlete, Attribute>> correspondences, MatchingGoldStandard gs) {
		
		// go through the correspondences and check if they are incorrect
		for(Correspondence<Athlete, Attribute> c : correspondences.get()) {
			
			// is the match incorrect?
			if(gs.containsNegative(c.getFirstRecord(), c.getSecondRecord())) {
				
				// if yes, print the records to the console
				Athlete a1 = c.getFirstRecord();
				Athlete a2 = c.getSecondRecord();
				
				// print both records to the console
				logger.info("[Incorrect Correspondence]");
				logger.info(String.format("\t%s", a1.getName()));	
				logger.info(String.format("\t%s", a2.getName()));	
			}
		}		
	}
	
	public void printFalseNegatives(DataSet<Athlete, Attribute> ds1, DataSet<Athlete, Attribute> ds2, Processable<Correspondence<Athlete, Attribute>> correspondences, MatchingGoldStandard gs) {
		
		// first generate a set of all correct correspondences in the gold standard
		// (if a pair is not in the gold standard, we cannot say if its correct or not)
		Set<Pair<String,String>> allPairs = new HashSet<>();
		allPairs.addAll(gs.getPositiveExamples());
		
		// then go through the correspondences and remove all correct matches from the set
		for(Correspondence<Athlete, Attribute> c : correspondences.get()) {
			
			// create a pair of both record ids
			Pair<String, String> p1 = new Pair<>(c.getFirstRecord().getIdentifier(), c.getSecondRecord().getIdentifier());
			
			// create a second pair where record1 and record2 are switched 
			// (we don't know in which direction the ids were entered in the gold standard 
			Pair<String, String> p2 = new Pair<>(c.getSecondRecord().getIdentifier(), c.getFirstRecord().getIdentifier());
			
			// check if one of the pairs is in the set of correct matches
			if(allPairs.contains(p1) || allPairs.contains(p2)) {
				// if so, remove it
				allPairs.remove(p1);
				allPairs.remove(p2);
			}
		}
		
		// now, the remaining pairs in the set are those that were not found by the matching rule
		// we go through them and print them to the console
		for(Pair<String, String> p : allPairs) {
			
			// get the first record
			Athlete a1 = ds1.getRecord(p.getFirst());
			if(a1==null) {
				a1 = ds2.getRecord(p.getFirst());
			}
			
			// get the second record
			Athlete a2 = ds1.getRecord(p.getSecond());
			if(a2==null) {
				a2 = ds2.getRecord(p.getSecond());
			}
			
			// print both records to the console
			logger.info("[Missing Correspondence]");
			logger.info(String.format("\t%s", a1.getName()));	
			logger.info(String.format("\t%s", a2.getName()));	
		}
	}
}
