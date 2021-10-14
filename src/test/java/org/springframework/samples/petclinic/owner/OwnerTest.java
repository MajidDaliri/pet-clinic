package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Owner}
 *
 * Properties (getters/setters in Java) are good examples of code that usually doesn’t contain any logic,
 * and doesn’t require testing. But watch out: once you add any check inside the property,
 * you’ll want to make sure that logic is being tested.
 */
class OwnerTest {
	private final static String DOG_NAME = "Edgar";
	private final static String CAT_NAME = "Garfield";

	private Owner owner;
	private Pet dog;
	private Pet cat;

	@BeforeEach
	public void setup() {
		owner = new Owner();

		dog = new Pet();
		dog.setName(DOG_NAME);

		cat = new Pet();
		cat.setName(CAT_NAME);
	}

	@Test
	public void addPetShouldIncreaseSizeOfPetsList() {
		assertTrue(owner.getPets().isEmpty());

		owner.addPet(dog);

		assertEquals(owner.getPets().size(), 1);

		owner.addPet(cat);

		assertEquals(owner.getPets().size(), 2);
	}

	@Test
	public void addPetTest() {
		owner.addPet(dog);
		assertEquals(owner.getPets().get(0), dog);
	}

	@Test
	public void addMultiplePetsTest() {
		owner.addPet(dog);
		owner.addPet(cat);

		assertEquals(owner.getPets().size(), 2);
		assertEquals(owner.getPets(), Arrays.asList(dog, cat));
	}

	@Test
	public void removePetTest() {
		owner.addPet(dog);
		owner.addPet(cat);
		assertEquals(owner.getPets(), Arrays.asList(dog, cat));

		owner.removePet(dog);
		assertEquals(owner.getPets(), Collections.singletonList(cat));

		owner.removePet(cat);
		assertTrue(owner.getPets().isEmpty());
	}

	@Test
	public void getPetByNameTest() {
		owner.addPet(dog);

		assertEquals(owner.getPet(DOG_NAME), dog);
	}

	@Test
	public void getPetShouldReturnNullIfPetNotAdded() {
		owner.addPet(cat);

		assertNull(owner.getPet(DOG_NAME));
	}

	@Test
	public void getPetWithIgnoreNewShouldIgnoreNewPets() {
		owner.addPet(dog);

		assertNull(owner.getPet(DOG_NAME, true));
	}

	@Test
	public void getPetsShouldReturnSorted() {
		cat.setName("B");
		owner.addPet(cat);
		dog.setName("A");
		owner.addPet(dog);

		assertEquals(owner.getPets().get(0), dog);
		assertEquals(owner.getPets().get(1), cat);
	}
}
