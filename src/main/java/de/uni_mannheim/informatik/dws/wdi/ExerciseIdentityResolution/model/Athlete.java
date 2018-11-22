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
import java.util.LinkedList;
import java.util.List;
import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

/**
 * A {@link AbstractRecord} representing an athlete.
 * 
 * @author Jasmin Weimueller & Marius Bock
 * 
 */

public class Athlete implements Matchable {
	
	protected String id;
	protected String provenance;
	private String Name;
	private LocalDateTime Birthday;
	private String PlaceOfBirth;
	private String Sex;
	private String Nationality;
	private float Weight;
	private float Height;
	private List<OlympicParticipation> OlympicParticipations;
	
	public Athlete(String identifier, String provenance) {
		id = identifier;
		this.provenance = provenance;
		OlympicParticipations = new LinkedList<>();
	}

	public String getIdentifier() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public LocalDateTime getBirthday() {
		return Birthday;
	}

	public void setBirthday(LocalDateTime birthday) {
		Birthday = birthday;
	}

	public String getPlaceOfBirth() {
		return PlaceOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		PlaceOfBirth = placeOfBirth;
	}

	public String getSex() {
		return Sex;
	}

	public void setSex(String sex) {
		Sex = sex;
	}

	public String getNationality() {
		return Nationality;
	}

	public void setNationality(String nationality) {
		Nationality = nationality;
	}

	public float getWeight() {
		return Weight;
	}

	public void setWeight(float weight) {
		Weight = weight;
	}

	public float getHeight() {
		return Height;
	}

	public void setHeight(float height) {
		Height = height;
	}

	public List<OlympicParticipation> getOlympicParticipations() {
		return OlympicParticipations;
	}

	public void setOlympicParticipations(List<OlympicParticipation> olympicParticipations) {
		OlympicParticipations = olympicParticipations;
	}

	
//	@Override public String toString() { return
//	String.format("[Athlete %s: %s / %s / %s]", getIdentifier(), getTitle(),getDirector(), getDate().toString()); }
//	
//	@Override public int hashCode() { return getIdentifier().hashCode(); }
//	 
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Athlete) {
			return this.getIdentifier().equals(((Athlete) obj).getIdentifier());
		} else
			return false;
	}

}
