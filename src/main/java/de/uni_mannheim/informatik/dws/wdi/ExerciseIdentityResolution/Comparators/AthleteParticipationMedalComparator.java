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

/**
* {@link Comparator} for {@link Athlete}s based on the {@link OlympicParticipation#getMedal()} value.
* This is the first version of the {@link AthleteParticipationMedal_inclYearDiscipline_Comparator}.
* 
* @author Tido Felix Marschall & Hendrik Roeder
* 
*/

@SuppressWarnings("unused")
public class AthleteParticipationMedalComparator implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;

	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Athlete record1, Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {

		int goldA = 0;
		int silverA = 0;
		int bronzeA = 0;

		int goldB = 0;
		int silverB = 0;
		int bronzeB = 0;

		for (OlympicParticipation a : record1.getOlympicParticipations()) {
			switch (a.getMedal()) {
			case "gold":
				goldA++;
				break;

			case "silver":
				silverA++;
				break;

			case "bronze":
				bronzeA++;
				break;

			default:
				break;
			}
		}

		for (OlympicParticipation b : record2.getOlympicParticipations()) {
			switch (b.getMedal()) {
			case "gold":
				goldB++;
				break;

			case "silver":
				silverB++;
				break;

			case "bronze":
				bronzeB++;
				break;

			default:
				break;
			}
		}
		int difGold = Math.abs(goldA - goldB);
		int difSilver = Math.abs(silverA - silverB);
		int difBronze = Math.abs(bronzeA - bronzeB);
		int difSum = difGold + difSilver +difBronze;
		
		double similarity = 1 / (1 + difSum);

		if (this.comparisonLog != null) {
			this.comparisonLog.setComparatorName(getClass().getName());

			this.comparisonLog.setRecord1Value(Integer.toString(difGold) + "-" 
			+ Integer.toString(difSilver) + "-" + Integer.toString(difBronze) + "-");
			this.comparisonLog.setRecord2Value(Integer.toString(difGold) + "-" 
			+ Integer.toString(difSilver) + "-" + Integer.toString(difBronze) + "-");

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
