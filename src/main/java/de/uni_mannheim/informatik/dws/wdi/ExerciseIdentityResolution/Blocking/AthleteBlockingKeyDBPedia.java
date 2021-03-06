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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.sparql.pfunction.library.listIndex;
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
 * {@link BlockingKeyGenerator} for the DBpedia athlete dataset, which generates a blocking
 * key based on name (get first two letters of first and last token, ordered alphabetically) and gender
 * 
 * @author Maximilian Philipp Barth 
 * 
 */

@SuppressWarnings("unused")
public class AthleteBlockingKeyDBPedia extends
		RecordBlockingKeyGenerator<Athlete, Attribute> {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.matching.blocking.generators.BlockingKeyGenerator#generateBlockingKeys(de.uni_mannheim.informatik.wdi.model.Matchable, de.uni_mannheim.informatik.wdi.model.Result, de.uni_mannheim.informatik.wdi.processing.DatasetIterator)
	 */
	@Override
	public void generateBlockingKeys(Athlete record, Processable<Correspondence<Attribute, Matchable>> correspondences,
			DataIterator<Pair<String, Athlete>> resultCollector) {
		
		String name_preprocessed = record.getName();
		name_preprocessed = name_preprocessed.replaceAll("\\(.*\\)","");
		String[] tokens  = name_preprocessed.split(" ");
		int tokenLength = 0;
		String firstToken, lastToken ="";
		String blockingKeyValue = "";
		List<String> tokensOrdered = new ArrayList<>();
		
				tokenLength = tokens.length;
				firstToken = tokens[0];
				lastToken = tokens[tokenLength-1];
				tokensOrdered.add(firstToken);
				tokensOrdered.add(lastToken);
				Collections.sort(tokensOrdered);

				for(int i = 0; i < 1; i++) {
					blockingKeyValue += tokensOrdered.get(i).substring(0, Math.min(3,tokensOrdered.get(i).length())).toUpperCase();
				}
	
				// Block By Gender
				if (record.getSex().equals("female")) {
					blockingKeyValue = blockingKeyValue + "f";
				}
				else {
					blockingKeyValue = blockingKeyValue + "m";
				}
				resultCollector.next(new Pair<>(blockingKeyValue, record));
		}
}