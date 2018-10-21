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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

/**
 * A {@link XMLMatchableReader} for {@link Movie}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AthleteXMLReader extends XMLMatchableReader<Athlete, Attribute>  {

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Athlete, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
	}
	
	@Override
	public Athlete createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Athlete athlete = new Athlete(id, provenanceInfo);

		// fill the attributes
		athlete.setWeight(Float.parseFloat(getValueFromChildElement(node, "Weight")));
		athlete.setHeight(Float.parseFloat(getValueFromChildElement(node, "Height")));
		athlete.setName(getValueFromChildElement(node, "Name"));
		athlete.setNationality(getValueFromChildElement(node, "Nationality"));
		athlete.setPlaceOfBirth(getValueFromChildElement(node, "PlaceOfBirth"));
		athlete.setSex(getValueFromChildElement(node, "Sex"));
		
		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "Birthdate");
			if (date != null && !date.isEmpty()) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				        .appendPattern("yyyy-MM-dd")
				        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
				        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
				        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
				        .toFormatter(Locale.ENGLISH);
				LocalDateTime dt = LocalDateTime.parse(date, formatter);
				athlete.setBirthday(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// load the list of actors
		List<OlympicParticipation> olympicParticipations = getObjectListFromChildElement(node, "OlympicParticipations",
				"OlympicParticipation", new OlympicParticipationXMLReader(), provenanceInfo);
		athlete.setOlympicParticipations(olympicParticipations);

		return athlete;
	}

}
