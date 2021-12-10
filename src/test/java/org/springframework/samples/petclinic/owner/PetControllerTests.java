package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

/**
 * Test class for the {@link PetController}
 */
@WebMvcTest(value = PetController.class,
	includeFilters = {
		@ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = PetService.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = LoggerConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = PetTimedCache.class, type = FilterType.ASSIGNABLE_TYPE),
	}
)
class PetControllerTests {
	private static final int OWNER_ID = 1;
	private static final int MOCK_OWNER_ID = 2;
	private static final int PET_ID = 1;
	private static final String PET_NAME = "Garfield";
	private static final String PET_TYPE = "cat";
	private static final String BIRTHDATE = "2020-03-09";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PetRepository pets;

	@MockBean
	private OwnerRepository owners;

	@Mock
	private Owner mockOwner;

	@BeforeEach
	void setup() {
		PetType petType = new PetType();
		petType.setId(23);
		petType.setName(PET_TYPE);
		Pet pet = new Pet();
		pet.setType(petType);
		pet.setId(PET_ID);
		given(this.pets.findPetTypes()).willReturn(Collections.singletonList(petType));
		given(this.pets.findById(PET_ID)).willReturn(pet);
		given(this.owners.findById(OWNER_ID)).willReturn(new Owner());

		given(this.owners.findById(MOCK_OWNER_ID)).willReturn(mockOwner);
		given(mockOwner.getPet(PET_NAME, true)).willReturn(pet);
	}

	@Test
	void initCreationFormTest() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void processCreationFormTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", OWNER_ID)
				.param("name", PET_NAME)
				.param("type", PET_TYPE)
				.param("birthDate", BIRTHDATE))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processCreationFormWithErrorTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", OWNER_ID)
				.param("name", PET_NAME)
				.param("type", PET_TYPE))
			.andExpect(model().attributeHasNoErrors("owner"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processCreationFormDuplicatePetTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", MOCK_OWNER_ID)
				.param("name", PET_NAME)
				.param("type", PET_TYPE)
				.param("birthDate", BIRTHDATE))
			.andExpect(model().attributeHasNoErrors("owner"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void initUpdateFormTest() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", OWNER_ID, PET_ID))
			.andExpect(model().attributeExists("pet"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateFormSuccessTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", OWNER_ID, PET_ID)
				.param("name", PET_NAME)
				.param("type", PET_TYPE)
				.param("birthDate", BIRTHDATE))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateFormWithErrorTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", OWNER_ID, PET_ID)
				.param("name", PET_NAME))
			.andExpect(model().attributeHasNoErrors("owner"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
			.andExpect(model().attributeHasFieldErrors("pet", "type"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}
}
