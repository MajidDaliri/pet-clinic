package org.springframework.samples.petclinic.owner;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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
import static org.mockito.Mockito.*;

/**
 * Test class for {@link PetManager}
 */
@RunWith(MockitoJUnitRunner.class)
public class PetManagerTestMockist {
	private static final Integer TEST_PET_ID = 1;
	private static final Integer TEST_OWNER_ID = 1;

	@Mock
	private PetTimedCache pets;

	@Mock
	private OwnerRepository owners;

	@Mock
	private Logger log;

	@InjectMocks
	private PetManager petManager;

	@Mock
	private Owner mockOwner;

	private static Pet dummyPet;
	private static List<Pet> dummyPets;

	@BeforeClass
	public static void setup() {
		dummyPet = DummyPetStore.getDummyPet();
		dummyPets = Arrays.asList(DummyPetStore.getDummyPet(), DummyPetStore.getDummyPet(), DummyPetStore.getDummyPet());
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: behavior
	 */
	@Test
	public void findOwnerTest() {
		Owner dummyOwner = new Owner();

		when(owners.findById(TEST_OWNER_ID)).thenReturn(dummyOwner);

		Owner owner = petManager.findOwner(TEST_OWNER_ID);

		assertEquals(owner, dummyOwner);
	}

	/*
	 * Test doubles: mock
	 * Verification method: behavior
	 */
	@Test
	public void newPetTest() {
		Pet newPet = petManager.newPet(mockOwner);

		verify(mockOwner).addPet(newPet);
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
	 * Verification method: behavior
	 */
	@Test
	public void savePetTest() {
		petManager.savePet(dummyPet, mockOwner);

		verify(pets).save(dummyPet);
		verify(mockOwner).addPet(dummyPet);
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: behavior
	 */
	@Test
	public void getOwnerPetsTest() {
		when(mockOwner.getPets()).thenReturn(dummyPets);

		when(owners.findById(TEST_OWNER_ID)).thenReturn(mockOwner);

		List<Pet> pets = petManager.getOwnerPets(TEST_OWNER_ID);

		assertEquals(pets, dummyPets);
	}

	/*
	 * Test doubles: mock, dummy
	 * Verification method: behavior
	 */
	@Test
	public void getOwnerPetTypes() {
		when(mockOwner.getPets()).thenReturn(dummyPets);

		when(owners.findById(TEST_OWNER_ID)).thenReturn(mockOwner);

		Set<PetType> types = petManager.getOwnerPetTypes(TEST_OWNER_ID);

		assertEquals(types, dummyPets.stream().map(Pet::getType).collect(Collectors.toSet()));
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

		Pet pet = mock(Pet.class);
		when(pet.getVisitsBetween(start, end)).thenReturn(dummyVisits);

		when(pets.get(TEST_PET_ID)).thenReturn(pet);

		List<Visit> visits = petManager.getVisitsBetween(TEST_PET_ID, start, end);

		assertEquals(visits, dummyVisits);
	}
}
