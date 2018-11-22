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

import java.io.Serializable;
import java.time.LocalDateTime;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * A {@link AbstractRecord} which represents a olympic participation
 * 
 * @author Jasmin Weimueller & Marius Bock
 * 
 */

@SuppressWarnings("unused")
public class OlympicParticipation extends AbstractRecord<Attribute> implements Serializable {

    private static final long serialVersionUID = 1L;
	private String id;
	private int year;
	private String season;
	private String city;
	private String olympicTeam;
	private String disciplines;
	private String event;
	private String medal;

	public OlympicParticipation(String identifier, String provenance) {
		super(identifier, provenance);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOlympicTeam() {
		return olympicTeam;
	}

	public void setOlympicTeam(String olympicTeam) {
		this.olympicTeam = olympicTeam;
	}

	public String getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(String disciplines) {
		this.disciplines = disciplines;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getMedal() {
		return medal;
	}

	public void setMedal(String medal) {
		this.medal = medal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 31 + ((season == null) ? 0 : season.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OlympicParticipation other = (OlympicParticipation) obj;
		if (season == null) {
			if (other.season != null)
				return false;
		} else if (!season.equals(other.season))
			return false;
		return true;
	}
	
	public static final Attribute ID = new Attribute("ID");
	public static final Attribute YEAR = new Attribute("Year");
	public static final Attribute SEASON = new Attribute("Season");
	public static final Attribute CITY = new Attribute("City");
	public static final Attribute OLYMPICTEAM = new Attribute("OlympicTeam");
	public static final Attribute DISCIPLINES = new Attribute("Disciplines");
	public static final Attribute EVENT = new Attribute("Event");
	public static final Attribute MEDAL = new Attribute("Medal");

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_mannheim.informatik.wdi.model.Record#hasValue(java.lang.Object)
	 */
	public boolean hasValue(Attribute attribute) {
		if (attribute == ID)
			return false;
		else if (attribute == YEAR)
			return false; // Year != null;
		else if (attribute == SEASON)
			return season != null;
		else if (attribute == CITY)
			return city != null;
		else if (attribute == OLYMPICTEAM)
			return olympicTeam != null;
		else if (attribute == DISCIPLINES)
			return disciplines != null;
		else if (attribute == EVENT)
			return event != null;
		else if (attribute == MEDAL)
			return medal != null;
		return false;
	}
}
