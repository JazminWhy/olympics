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
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.tokenisers.InterfaceTokeniser;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.api.SourcedStringWrapper;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;

/**
 * 
 * Calculates Jaccard similarity on n-grams, which are created from the values
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 */
public class AthleteNameComparatorMongeElkan extends SimilarityMeasure<String> implements Comparator<Athlete, Attribute>  {

    private static final long serialVersionUID = 1L;
    private ComparatorLogger comparisonLog;
    private MongeElkan me;
    
    public AthleteNameComparatorMongeElkan() {
    	me = new MongeElkan();
    }
    
    public AthleteNameComparatorMongeElkan(AbstractStringMetric metricToUse) {
    	me = new MongeElkan(metricToUse);
    }
    
    public AthleteNameComparatorMongeElkan(InterfaceTokeniser tokeniserToUse, AbstractStringMetric metricToUse) {
    	me = new MongeElkan(tokeniserToUse, metricToUse);
    }
    
    @Override
    public double calculate(String first, String second) {
    	// Added these two lines to remove commas from names as Standard Tokenizer does not seem to do this
        //first = first.replace(",", " ");
    	//second = second.replace(",", " ");
        
        if(first == null || second == null) {
            return 0.0;
        }

        return me.getSimilarity(first, second);
    }

    @Override
    public double compare(Athlete record1, Athlete record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        
        double similarity = calculate(record1.getName(), record2.getName());
        
        if(this.comparisonLog != null){
            this.comparisonLog.setComparatorName(getClass().getName());
        
            this.comparisonLog.setRecord1Value(record1.getName());
            this.comparisonLog.setRecord2Value(record2.getName());
        
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