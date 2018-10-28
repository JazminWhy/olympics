package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import java.util.Comparator;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;

public class ParticipationSortingComparer implements Comparator<OlympicParticipation> {

	@Override
	public int compare(OlympicParticipation a, OlympicParticipation b) {
		
		if (a.getYear() < b.getYear()) {
			return -1;
		} else if (a.getYear() > b.getYear()) {
			return 1;
		} else {
			if (a.getDisciplines().compareTo(b.getDisciplines()) > 0) {
				return -1;
			} else if (a.getDisciplines().compareTo(b.getDisciplines()) < 0) {
				return 1;
			} else {
				if (a.getMedal().compareTo(b.getMedal()) > 0) {
					return -1;
				} else if (a.getMedal().compareTo(b.getMedal()) < 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		
	}

}
