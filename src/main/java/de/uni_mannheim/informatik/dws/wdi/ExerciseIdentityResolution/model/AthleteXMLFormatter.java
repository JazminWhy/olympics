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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Movie}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AthleteXMLFormatter extends XMLFormatter<Athlete> {

	OlympicParticipationsXMLFormatter OlympicParticipationsFormatter = new OlympicParticipationsXMLFormatter();

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("WinningAthletes");
	}

//	protected String id;
//	protected String provenance;
//	private String Name;
//	private LocalDateTime Birthday;
//	private String PlaceOfBirth;
//	private String Sex;
//	private String Nationality;
//	private float Weight;
//	private float Height;
//	private List<OlympicParticipation> OlympicParticipations;

	@Override
	public Element createElementFromRecord(Athlete record, Document doc) {
		Element Athlete = doc.createElement("Athlete");

		Athlete.appendChild(createTextElement("id", record.getIdentifier(), doc));
		Athlete.appendChild(createTextElement("Name", record.getName(), doc));
		Athlete.appendChild(createTextElement("Birthday", record.getBirthday().toString(), doc));
		Athlete.appendChild(createTextElement("PlaceOfBirth", record.getPlaceOfBirth(), doc));
		Athlete.appendChild(createTextElement("Sex", record.getSex(), doc));
		Athlete.appendChild(createTextElement("Nationality", record.getNationality(), doc));
		// Athlete.appendChild(createTextElement("Weight", record.getWeight(), doc));
		// Athlete.appendChild(createTextElement("Height", record.getHeight(), doc));

		Athlete.appendChild(createOlympicParticipationsElement(record, doc));

		return Athlete;
	}

	protected Element createTextElementWithProvenance(String name, String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createOlympicParticipationsElement(Athlete record, Document doc) {
		Element OlympicParticipationsRoot = OlympicParticipationsFormatter.createRootElement(doc);

		for (OlympicParticipation o : record.getOlympicParticipations()) {
			OlympicParticipationsRoot.appendChild(OlympicParticipationsFormatter.createElementFromRecord(o, doc));
		}

		return OlympicParticipationsRoot;
	}

}
