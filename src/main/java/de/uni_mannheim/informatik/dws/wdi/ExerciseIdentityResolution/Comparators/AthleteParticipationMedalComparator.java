package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.query.Q;

public class AthleteParticipationMedalComparator  implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Athlete record1,
			Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		Set<String> participationMedals1 = new HashSet<>();
		Set<String> participationMedals2 = new HashSet<>();
		
		for(OlympicParticipation a : record1.getOlympicParticipations()) {
			participationMedals1.add(a.getMedal() + a.getYear());
		}
		for(OlympicParticipation a : record2.getOlympicParticipations()) {
			participationMedals2.add(a.getMedal() + a.getYear());
		}
		
		double similarity = Q.intersection(participationMedals1, participationMedals2).size() / (double)Math.max(participationMedals1.size(), participationMedals2.size());
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(participationMedals1.toString());
			this.comparisonLog.setRecord2Value(participationMedals2.toString());
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		
		return similarity;
	}
	
	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
