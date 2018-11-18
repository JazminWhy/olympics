package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.IR_using_machine_learning_kaggle_figshare;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.query.Q;

public class AthleteParticipationMedal_inclYearDiscipline_Comparator implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;

	private ComparatorLogger comparisonLog;  

	private boolean compareDiscipline(String disc1, String disc2) {
		boolean toBeReturned = false;

		try {
			// CSVReader reader = new CSVReader(new
			// FileReader("data/input/20181025_discipline mapping_final.csv"));
			// List<String[]> DisciplineMapping = reader.readAll();
			for (String[] string : IR_using_machine_learning_kaggle_figshare.DisciplineMapping) {
				// System.out.println(string[0] + " - " + disc1);
				// System.out.println(string[1] + " - " + disc2);
				if (string[0].equalsIgnoreCase(disc1)) {
					// System.out.println(string[0] + " - " + disc1);
					// System.out.println(string[1] + " - " + disc2);
					// System.out.println(string[1].equalsIgnoreCase(disc2));
					toBeReturned = string[1].equalsIgnoreCase(disc2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return toBeReturned;
		}
	}

	@Override
	public double compare(Athlete record1, Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {

//		Set<String> participationMedals1 = new HashSet<>();
//		Set<String> participationMedals2 = new HashSet<>();
//		
		List<OlympicParticipation> a_list = record1.getOlympicParticipations();
		List<OlympicParticipation> b_list_original = record2.getOlympicParticipations();

		// do not take into account games that are only in figshare
		List<OlympicParticipation> b_list = new ArrayList<OlympicParticipation>();
		for (OlympicParticipation i : b_list_original) {
			if ((i.getYear() != 2016) && (i.getYear() != 1906)) {
				b_list.add(i);
			}
		}

		Collections.sort(a_list, new ParticipationSortingComparer());
		Collections.sort(b_list, new ParticipationSortingComparer());

		int same = 0;

		int total = Math.max(a_list.size(), b_list.size());
		for (OlympicParticipation a : a_list) {
			String medal_a = a.getMedal();
			int year_a = a.getYear();
			String discipline_a = a.getDisciplines();
			String participation_a = medal_a + year_a;
			for (OlympicParticipation b : b_list) {
				String medal_b = b.getMedal();
				int year_b = b.getYear();
				String discipline_b = b.getDisciplines();
				String participation_b = medal_b + year_b;
				if (participation_a.equals(participation_b)) {
					if (compareDiscipline(discipline_a, discipline_b)) {
						same++;
						b_list.remove(b);
						break;
					}
				}
			}
		}

		double similarity = same / total;

		if (this.comparisonLog != null) {
			this.comparisonLog.setComparatorName(getClass().getName());

			// this.comparisonLog.setRecord1Value(Integer.toString(difGold) + "-"
			// + Integer.toString(difSilver) + "-" + Integer.toString(difBronze) + "-");
			// this.comparisonLog.setRecord2Value(Integer.toString(difGold) + "-"
			// + Integer.toString(difSilver) + "-" + Integer.toString(difBronze) + "-");

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
