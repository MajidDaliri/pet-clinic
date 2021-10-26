package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.time.LocalDate;

public class DummyPetStore {
	private static Integer id = 0;
	private static final String NAME_PREFIX = "pet#";
	private static final String TYPE_PREFIX = "type#";

	public static Pet getDummyPet() {
		Pet newPet = new Pet();
		newPet.setId(id);
		newPet.setName(NAME_PREFIX + id);
		PetType type = new PetType();
		type.setName(TYPE_PREFIX + id);
		newPet.setType(type);
		newPet.setBirthDate(LocalDate.now());
		++id;
		return newPet;
	}
}
