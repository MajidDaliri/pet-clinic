package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.visit.Visit;

public class DummyVisitFactory {
	private static Integer id = 0;
	private static final String DESCRIPTION = "description#";

	public static Visit getDummyVisit() {
		Visit newVisit = new Visit();
		newVisit.setDescription(DESCRIPTION);
		newVisit.setId(id);
		++id;
		return newVisit;
	}
}
