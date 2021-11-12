package org.springframework.samples.petclinic.model;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.petclinic.model.priceCalculators.SimplePriceCalculator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SimplePriceCalculator}
 */
@RunWith(MockitoJUnitRunner.class)
public class SimplePriceCalculatorTest {
	private static final double BASE_CHARGE = 20000;
	private static final double BASE_PRICE_PER_PET = 10000;
	private static final double DELTA = 0.001;
	private static final UserType NEW_USER = UserType.NEW;
	private static final UserType EXISTING_USER = UserType.SILVER;
	private static final double BASE_RARE_COEF = 1.2;

	private static final SimplePriceCalculator simplePriceCalculator = new SimplePriceCalculator();

	private static final Pet rarePet = mock(Pet.class);
	private static final Pet commonPet = mock(Pet.class);
	private static final PetType rarePetType = mock(PetType.class);
	private static final PetType commonPetType = mock(PetType.class);

	private static List<Pet> rarePets;
	private static List<Pet> commonPets;

	@BeforeClass
	public static void setup() {
		when(rarePetType.getRare()).thenReturn(true);
		when(commonPetType.getRare()).thenReturn(false);
		when(rarePet.getType()).thenReturn(rarePetType);
		when(commonPet.getType()).thenReturn(commonPetType);
		rarePets = Arrays.asList(rarePet, rarePet, rarePet);
		commonPets = Arrays.asList(commonPet, commonPet);
	}

	@Test
	public void calcPriceShouldReturnBaseChargeIfThereAreNoPets() {
		double price = simplePriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET, EXISTING_USER);
		double expectedPrice = BASE_CHARGE;
		assertEquals(price, expectedPrice, DELTA);
	}

	@Test
	public void calcPriceShouldReturnDiscountedBaseChargeForNewUserTypeWhenPetsIsEmpty() {
		double price = simplePriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE * NEW_USER.discountRate;
		assertEquals(price, expectedPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyRareCoefForRarePets() {
		double price = simplePriceCalculator.calcPrice(rarePets, BASE_CHARGE, BASE_PRICE_PER_PET, EXISTING_USER);
		double expectedPrice = BASE_CHARGE + rarePets.size() * BASE_PRICE_PER_PET * BASE_RARE_COEF;
		assertEquals(price, expectedPrice, DELTA);
	}

	@Test
	public void calcPriceShouldNotApplyRareCoefForCommonPets() {
		double price = simplePriceCalculator.calcPrice(commonPets, BASE_CHARGE, BASE_PRICE_PER_PET, EXISTING_USER);
		double expectedPrice = BASE_CHARGE +
			commonPets.size() * BASE_PRICE_PER_PET;
		assertEquals(price, expectedPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyNewUserDiscountAfterAllOtherCalculations() {
		List<Pet> allPets = Stream.concat(rarePets.stream(), commonPets.stream()).collect(Collectors.toList());
		double price = simplePriceCalculator.calcPrice(allPets, BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double noDiscountPrice = BASE_CHARGE +
			commonPets.size() * BASE_PRICE_PER_PET +
			rarePets.size() * BASE_PRICE_PER_PET * BASE_RARE_COEF;
		double expectedPrice = noDiscountPrice * NEW_USER.discountRate;
		assertEquals(price, expectedPrice, DELTA);
	}
}
