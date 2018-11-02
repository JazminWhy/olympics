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

import com.wcohen.ss.Jaccard;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;

//
///**
// * {@link Comparator} for {@link Movie}s based on the {@link Movie#getDate()}
// * value, with a maximal difference of 5 years.
// * 
// * @author Max
// * 
// */


// Größe: beide minus 1, kleinere durch größere, ergebnis ^4
// Gewicht: kleinere durch größere, ergebnis ^4


public class AthleteDBPediaBirthdayComparator2Years implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;
	private YearSimilarity sim = new YearSimilarity(5);
//	private int gramSize = 3;
    private MongeElkan me;
    
    public AthleteDBPediaBirthdayComparator2Years() {
    	me = new MongeElkan();
    }
    
    public AthleteDBPediaBirthdayComparator2Years(AbstractStringMetric metricToUse) {
    	me = new MongeElkan(metricToUse);
    }
    
    public AthleteDBPediaBirthdayComparator2Years(InterfaceTokeniser tokeniserToUse, AbstractStringMetric metricToUse) {
    	me = new MongeElkan(tokeniserToUse, metricToUse);
    }
   
	private ComparatorLogger comparisonLog;

//	 public AthleteDBPediaBirthdayComparator2Years(int n) {
//	        gramSize = n;
//	    }
	
	@Override
	public double compare(Athlete record1, Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		double similarity = 0;
		
		if ((record1.getBirthday() != null) && (record2.getBirthday() != null)) {

			similarity = sim.calculate(record1.getBirthday(), record2.getBirthday());

			if (this.comparisonLog != null) {
				this.comparisonLog.setComparatorName(getClass().getName());

				this.comparisonLog.setRecord1Value(record1.getBirthday().toString());
				this.comparisonLog.setRecord2Value(record2.getBirthday().toString());

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

    public double calculate(String first, String second) {
        if(first == null || second == null) {
            return 0.0;
        }
        
        return me.getSimilarity(first, second);
    }
	
	
    
//    public double calculate(String first, String second) {
//        if(first == null || second == null) {
//            return 0.0;
//        }
//        
//        NGramTokenizer tok = new NGramTokenizer(gramSize, gramSize, false, SimpleTokenizer.DEFAULT_TOKENIZER);
//        Jaccard j = new Jaccard(tok);
//        return j.score(first, second);
//    }

}
