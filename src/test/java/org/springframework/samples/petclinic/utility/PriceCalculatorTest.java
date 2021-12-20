package org.springframework.samples.petclinic.utility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.samples.petclinic.utility.PriceCalculator.calcPrice;

/**
 * Test class for the {@link PriceCalculator}
 */
public class PriceCalculatorTest {
	private static final double RARE_INFANCY_COEF = 1.4;
	private static final double BASE_RARE_COEF = 1.2;
	private static final int DISCOUNT_MIN_SCORE = 10;
	private static final int DISCOUNT_PRE_VISIT = 2;
	private static final int BASE_CHARGE = 15000;
	private static final int BASE_PRICE_PER_PET = 20000;
	private static final double DELTA = 0.01;
	private static final int ADULT_AGE = 3;
	private static final int INFANT_AGE = 1;
	private static final int OLD_VISIT_THRESH = 100;
	private static final double SINGLE_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF;
	private static final double SINGLE_INFANT_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF * RARE_INFANCY_COEF;

	private static final Pet pet = mock(Pet.class);
	private static final Pet infantPet = mock(Pet.class);
	private static final Visit newVisit = new Visit();
	private static final List<Visit> newVisits = Arrays.asList(newVisit, newVisit, newVisit);

	@BeforeAll
	public static void setup() {
		newVisit.setDate(LocalDate.now().minusDays(OLD_VISIT_THRESH - 1));

		final LocalDate adultPetBirthDate = LocalDate.now().minusYears(ADULT_AGE);
		final LocalDate infantPetBirthDate = LocalDate.now().minusYears(INFANT_AGE);

		when(pet.getBirthDate()).thenReturn(adultPetBirthDate);
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(Collections.emptyList());

		when(infantPet.getBirthDate()).thenReturn(infantPetBirthDate);
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(Collections.emptyList());
	}

	@Test
	public void calcPriceShouldReturnZeroIfThereAreNoPets() {
		final double actualPrice = calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(0, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldConsiderEveryPetAsRare() {
		final double actualPrice = calcPrice(Collections.singletonList(pet), BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(SINGLE_PET_PRICE, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyRareInfancyCoefForInfantPets() {
		final double actualPrice = calcPrice(Collections.singletonList(infantPet), BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(SINGLE_INFANT_PET_PRICE, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldNotApplyDiscountIfDiscountMinScoreIsNotReached() {
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE - 1; ++i)
			pets.add(pet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double expectedPrice = pets.size() * SINGLE_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountPreVisitAfterDiscountMinScoreIsReachedForNewVisits() {
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(pet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldIncreaseDiscountCounterByTwoForInfantPets() {
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE/2; ++i)
			pets.add(infantPet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (pets.size() - 1) * SINGLE_INFANT_PET_PRICE;
		final double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_INFANT_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountPreVisitForEveryPetAfterDiscountMinScoreIsReachedForNewVisits() {
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE + 2; ++i)
			pets.add(pet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final double priceAfterFirstDiscount = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double priceAfterSecondDiscount = priceAfterFirstDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double expectedPrice = priceAfterSecondDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountBasedOnNumberOfVisitsForOldVisits() {
		final int OLD_VISIT_DAYS = OLD_VISIT_THRESH + 1;
		final Visit oldVisit = new Visit().setDate(LocalDate.now().minusDays(OLD_VISIT_DAYS));
		final List<Visit> visits = Arrays.asList(newVisit, newVisit, oldVisit, oldVisit);
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE/2; ++i)
			pets.add(infantPet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (pets.size() - 1) * SINGLE_INFANT_PET_PRICE;
		final int oldVisitDiscount = OLD_VISIT_DAYS/OLD_VISIT_THRESH + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_INFANT_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountBasedOnDaysFromLastVisitForOldVisits() {
		final int VERY_OLD_VISIT_DAYS = OLD_VISIT_THRESH + 279;
		final Visit veryOldVisit = new Visit().setDate(LocalDate.now().minusDays(VERY_OLD_VISIT_DAYS));
		final List<Visit> visits = Collections.singletonList(veryOldVisit);
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(pet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final int oldVisitDiscount = VERY_OLD_VISIT_DAYS/OLD_VISIT_THRESH + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_PET_PRICE;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}
}
