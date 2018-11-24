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
import de.uni_mannheim.informatik.dws.winter.similarity.numeric.AbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Athlete;

/**
 * {@link Comparator} for {@link Athlete}s based on the {@link Athlete#getHeight()} 
 * with a max default difference of 0.2, but value can be set individually. 
 * 
 * @author Jasmin Weimueller
 * 
 */

public class AthleteHeightcomparatorsRange implements Comparator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;
	private AbsoluteDifferenceSimilarity sim;
	private double absolute = 0.2;
	private ComparatorLogger comparisonLog;
	
	public AthleteHeightcomparatorsRange(double absolute) {
	this.absolute = absolute;
	    }
	public AthleteHeightcomparatorsRange() {
		
		    }
	@Override
	public double compare(
			Athlete record1,
			Athlete record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
    	sim = new AbsoluteDifferenceSimilarity(absolute);
    	double similarity = sim.calculate((double) record1.getHeight(), (double) record2.getHeight());
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(Float.toString(record1.getHeight()));
			this.comparisonLog.setRecord2Value(Float.toString(record2.getHeight()));
    	
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
