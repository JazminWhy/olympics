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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.tokenisers.InterfaceTokeniser;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;

/**
 * {@link Comparator} for {@link Athlete}s based on the {@link Athlete#getDate()}
 * value, with a maximal difference of 5 years. In case of missing values, we compare names using MongeElkan.
 * 
 * @author Max
 * 
 */
public class AthleteDBPediaBirthdayComparator5Years implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;
	private YearSimilarity sim = new YearSimilarity(5);
    private MongeElkan me;
    
    public AthleteDBPediaBirthdayComparator5Years() {
    	me = new MongeElkan();
    }
    
    public AthleteDBPediaBirthdayComparator5Years(AbstractStringMetric metricToUse) {
    	me = new MongeElkan(metricToUse);
    }
    
    public AthleteDBPediaBirthdayComparator5Years(InterfaceTokeniser tokeniserToUse, AbstractStringMetric metricToUse) {
    	me = new MongeElkan(tokeniserToUse, metricToUse);
    }
   
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Athlete record1, Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		double similarity = 0.0;
		
		// Check whether records have missing values for Birthday
		if ((record1.getBirthday() != null) && (record2.getBirthday() != null)) {

			similarity = sim.calculate(record1.getBirthday(), record2.getBirthday());

			if (this.comparisonLog != null) {
				this.comparisonLog.setComparatorName(getClass().getName());

				this.comparisonLog.setRecord1Value(record1.getBirthday().toString());
				this.comparisonLog.setRecord2Value(record2.getBirthday().toString());

				this.comparisonLog.setSimilarity(Double.toString(similarity));
			}
			// If records have missing values for Birthday, use MongeElkan for Name comparator
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

    public double calculate(String first, String second) {
        if(first == null || second == null) {
            return 0.0;
        }
        
        return me.getSimilarity(first, second);
    }
}
