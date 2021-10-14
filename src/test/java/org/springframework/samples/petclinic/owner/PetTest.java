package org.springframework.samples.petclinic.owner;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(Theories.class)
public class PetTest {
	private Pet pet;

	@DataPoints("visits")
	public static List<List<Visit>> visitLists = new ArrayList<>();

	@BeforeClass
	public static void setupVisits() {
		Visit todayVisit = new Visit();
		todayVisit.setDate(LocalDate.now());
		Visit tomorrowVisit = new Visit();
		tomorrowVisit.setDate(LocalDate.now().minusDays(1));
		Visit yesterdayVisit = new Visit();
		yesterdayVisit.setDate(LocalDate.now().plusDays(1));
		Visit nextYearVisit = new Visit();
		nextYearVisit.setDate(LocalDate.now().plusYears(1));
		Visit lastMonthVisit = new Visit();
		lastMonthVisit.setDate(LocalDate.now().minusMonths(1));

		visitLists.add(Collections.singletonList(nextYearVisit));
		visitLists.add(Arrays.asList(todayVisit, lastMonthVisit, yesterdayVisit));
		visitLists.add(Arrays.asList(tomorrowVisit, lastMonthVisit, nextYearVisit, todayVisit));
	}

	@Before
	public void setup() {
		pet = new Pet();
		pet.setId(1);
	}

	@After
	public void teardown() {
		pet = null;
	}

	@Theory
	public void getVisitsShouldReturnSorted(List<Visit> visitList) {
		visitList.forEach(pet::addVisit);
		PropertyComparator.sort(visitList, new MutableSortDefinition("date", false, false));
		assertEquals(pet.getVisits(), visitList);
	}
}
