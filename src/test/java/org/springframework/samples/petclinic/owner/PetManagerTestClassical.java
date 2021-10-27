package org.springframework.samples.petclinic.owner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.DummyPetStore;
import org.springframework.samples.petclinic.utility.DummyVisitFactory;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link PetManager}
 */
@RunWith(MockitoJUnitRunner.class)
public class PetManagerTestClassical {
	private static final Integer TEST_OWNER_ID = 1;
	private static final Integer TEST_PET_ID = 1;

	@Mock
	private PetTimedCache pets;

	@Mock
	private OwnerRepository owners;

	@Mock
	private Logger log;

	@InjectMocks
	private PetManager petManager;

	private static Pet dummyPet;
	private Owner testOwner;

	@BeforeClass
	public static void petSetup() {
		dummyPet = DummyPetStore.getDummyPet();
	}

	@Before
	public void setup() {
		testOwner = new Owner();
		testOwner.setId(TEST_OWNER_ID);
		testOwner.addPet(DummyPetStore.getNewPet());
		testOwner.addPet(DummyPetStore.getNewPet());
		testOwner.addPet(DummyPetStore.getNewPet());
	}

	/*
	 * Test doubles: mock
	 * Verification method: behavior
	 */
	@Test
	public void findOwnerTest() {
		when(owners.findById(TEST_OWNER_ID)).thenReturn(testOwner);

		Owner owner = petManager.findOwner(TEST_OWNER_ID);

		assertEquals(owner, testOwner);
	}

	/*
	 * Test doubles: none
	 * Verification method: state
	 */
	@Test
	public void newPetTest() {
		Pet newPet = petManager.newPet(testOwner);

		assertEquals(newPet.getOwner(), testOwner);
		assertTrue(testOwner.getPets().contains(newPet));
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: behavior
	 */
	@Test
	public void findPetTest() {
		when(pets.get(TEST_PET_ID)).thenReturn(dummyPet);

		Pet pet = petManager.findPet(TEST_PET_ID);

		assertEquals(pet, dummyPet);
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: state
	 */
	@Test
	public void savePetTest() {
		petManager.savePet(dummyPet, testOwner);

		verify(pets).save(dummyPet);
		assertEquals(dummyPet.getOwner(), testOwner);
	}

	/*
	 * Test doubles: mock
	 * Verification method: behavior
	 */
	@Test
	public void getOwnerPetsTest() {
		when(owners.findById(TEST_OWNER_ID)).thenReturn(testOwner);

		List<Pet> pets = petManager.getOwnerPets(TEST_OWNER_ID);

		assertEquals(pets, testOwner.getPets());
	}

	/*
	 * Test doubles: mock
	 * Verification method: behavior
	 */
	@Test
	public void getOwnerPetTypes() {
		when(owners.findById(TEST_OWNER_ID)).thenReturn(testOwner);

		Set<PetType> petTypes = petManager.getOwnerPetTypes(TEST_OWNER_ID);

		assertEquals(petTypes, testOwner.getPets().stream().map(Pet::getType).collect(Collectors.toSet()));
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: behavior
	 */
	@Test
	public void getVisitsBetweenTest() {
		List<Visit> dummyVisits = Arrays.asList(DummyVisitFactory.getDummyVisit(), DummyVisitFactory.getDummyVisit());
		LocalDate start = LocalDate.MIN;
		LocalDate end = LocalDate.MAX;

		Pet pet = new Pet();
		dummyVisits.forEach(pet::addVisit);

		when(pets.get(TEST_PET_ID)).thenReturn(pet);

		List<Visit> visits = petManager.getVisitsBetween(TEST_PET_ID, start, end);

		assertEquals(visits, pet.getVisitsBetween(start, end));
	}
}
