package org.springframework.samples.petclinic.owner;

import org.junit.BeforeClass;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Theory class for {@link Owner}
 */
@RunWith(Theories.class)
public class OwnerTheories {
	private static Owner owner;

	@DataPoints("persistedPets")
	public static String[] persistedPetNames = {"pet0", "pet2", "pet4", "pet6", "pet8"};

	@DataPoints("newPets")
	public static String[] newPetNames = {"pet1", "pet3", "pet5", "pet7", "pet9"};

	@BeforeClass
	public static void setupOwner() {
		owner = new Owner();
		for (int i = 0; i < 10; i++) {
			Pet pet = new Pet();
			pet.setName("pet" + i);
			owner.addPet(pet);
			if (i % 2 == 0)
				pet.setId(i);
		}
	}

	@Theory
	public void getPetShouldNotCareAboutPetIdsByDefault(String petName) {
		assertEquals(owner.getPet(petName).getName(), petName);
	}

	@Theory
	public void getPetShouldWithIgnoreNewShouldIgnoreNewPets(@FromDataPoints("newPets") String newPet) {
		assertNull(owner.getPet(newPet, true));
	}

	@Theory
	public void getPetShouldWithIgnoreNewShouldNotIgnorePersistedPets(@FromDataPoints("persistedPets") String persistedPet) {
		assertEquals(owner.getPet(persistedPet, true).getName(), persistedPet);
	}
}
