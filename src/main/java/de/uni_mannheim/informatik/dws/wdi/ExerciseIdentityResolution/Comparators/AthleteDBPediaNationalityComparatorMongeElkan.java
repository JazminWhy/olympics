/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.tokenisers.InterfaceTokeniser;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;

/**
 * 
 * Calculates Jaccard similarity on n-grams, which are created from the values
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 */

public class AthleteDBPediaNationalityComparatorMongeElkan extends SimilarityMeasure<String>
		implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;
	private ComparatorLogger comparisonLog;
	private MongeElkan me;

	public AthleteDBPediaNationalityComparatorMongeElkan() {
		me = new MongeElkan();
	}

	public AthleteDBPediaNationalityComparatorMongeElkan(AbstractStringMetric metricToUse) {
		me = new MongeElkan(metricToUse);
	}

	public AthleteDBPediaNationalityComparatorMongeElkan(InterfaceTokeniser tokeniserToUse,
			AbstractStringMetric metricToUse) {
		me = new MongeElkan(tokeniserToUse, metricToUse);
	}

	@Override
	public double calculate(String first, String second) {
		if (first == null || second == null) {
			return 0.0;
		}

		return me.getSimilarity(first, second);
	}

	@Override
	public double compare(Athlete record1, Athlete record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		double similarity = 0.0;
		if ((record1.getNationality() != null) && (record2.getNationality() != null) && (record1.getNationality().length() > 2) && (record2.getNationality().length() > 2)) {
			similarity = calculate(record1.getNationality(), record2.getNationality());

			if (this.comparisonLog != null) {
				this.comparisonLog.setComparatorName(getClass().getName());

				this.comparisonLog.setRecord1Value(record1.getNationality());
				this.comparisonLog.setRecord2Value(record2.getNationality());

				this.comparisonLog.setSimilarity(Double.toString(similarity));
			}
		} else {
			similarity = calculate(record1.getName(), record2.getName());
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