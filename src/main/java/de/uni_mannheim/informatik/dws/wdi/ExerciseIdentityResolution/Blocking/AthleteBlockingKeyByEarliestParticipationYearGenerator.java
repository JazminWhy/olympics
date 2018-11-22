///*
// * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and limitations under the License.
// */
//
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.OlympicParticipation;
//
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.BlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

/**
 * {@link BlockingKeyGenerator} for {@link Athlete}s, which generates a blocking
 * key based on earliest participation year.
 * 
 * @author Hendrik Roeder & Tido Felix Marschall
 * 
 */
public class AthleteBlockingKeyByEarliestParticipationYearGenerator extends
		RecordBlockingKeyGenerator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.matching.blocking.generators.BlockingKeyGenerator#generateBlockingKeys(de.uni_mannheim.informatik.wdi.model.Matchable, de.uni_mannheim.informatik.wdi.model.Result, de.uni_mannheim.informatik.wdi.processing.DatasetIterator)
	 */
	@Override
	public void generateBlockingKeys(Athlete record, Processable<Correspondence<Attribute, Matchable>> correspondences,
			DataIterator<Pair<String, Athlete>> resultCollector) {
		Set<String> OlympicParticipations1 = new HashSet<>();
		for(OlympicParticipation a : record.getOlympicParticipations()) {
			//The games of the year 1906 do not exist in the kaggle dataset, however there might be athletes in the figshare dataset
			//that competed in 1906 and again later on. Using the large value 20.000 > 2016 in combination with later on taking the minimum of year
			//means that if there is a year after 1906 we find this, if not there cannot be a match anyway as the person only competed in 1906
			if (a.getYear() == 1906) {
				OlympicParticipations1.add(20000 + record.getNationality() + record.getSex());
			} else {
				OlympicParticipations1.add(a.getYear() + record.getNationality() + record.getSex());
			}
		}
		
		resultCollector.next(new Pair<>(Collections.min(OlympicParticipations1), record));
	}

}
