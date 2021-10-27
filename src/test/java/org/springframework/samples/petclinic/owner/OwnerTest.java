package org.springframework.samples.petclinic.owner;

import org.junit.Before;
import org.junit.Test;
import org.springframework.samples.petclinic.utility.DummyPetStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OwnerTest {
	private Owner owner;

	@Before
	public void setup() {
		owner = new Owner();
	}

	@Test
	public void addPetTestStateVerification() {
		Pet newPet = DummyPetStore.getNewPet();

		owner.addPet(newPet);

		assertEquals(newPet.getOwner(), owner);
		assertTrue(owner.getPets().contains(newPet));
	}

	@Test
	public void addPetTestBehaviorVerification() {
		Pet mockPet = mock(Pet.class);

		when(mockPet.isNew()).thenReturn(true);

		owner.addPet(mockPet);

		verify(mockPet).setOwner(owner);
		assertTrue(owner.getPets().contains(mockPet));
	}
}
