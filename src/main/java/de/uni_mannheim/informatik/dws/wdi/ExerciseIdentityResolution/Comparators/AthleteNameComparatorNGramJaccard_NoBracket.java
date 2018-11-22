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
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;

/**
* {@link Comparator} for {@link Athlete}s based on the {@link Athlete#getName()}
* value and their {@link TokenizingJaccardSimilarity} value on n-grams with the no-brackets preprocessing.
* 
* @author Jasmin Weimueller
* 
*/

public class AthleteNameComparatorNGramJaccard_NoBracket extends SimilarityMeasure<String> implements Comparator<Athlete, Attribute>  {

    private static final long serialVersionUID = 1L;
    private int gramSize = 3;
    private ComparatorLogger comparisonLog;
    
    public AthleteNameComparatorNGramJaccard_NoBracket(int n) {
        gramSize = n;
    }
    
    @Override
    public double calculate(String first, String second) {
        if(first == null || second == null) {
            return 0.0;
        }
        
        NGramTokenizer tok = new NGramTokenizer(gramSize, gramSize, false, SimpleTokenizer.DEFAULT_TOKENIZER);
        Jaccard j = new Jaccard(tok);
        return j.score(first, second);
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