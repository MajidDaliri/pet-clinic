package bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.*;

import static org.junit.jupiter.api.Assertions.*;

public class PetServiceFeaturesSteps {
	@Autowired
	PetService petService;

	@Autowired
	OwnerRepository ownerRepository;

	@Autowired
	PetRepository petRepository;

	@Autowired
	PetTypeRepository petTypeRepository;

	private Owner majid;
	private Pet foundPet;
	private Pet newPet;
	private Owner foundOwner;
	private PetType petType;

	@Given("Majid is an owner")
	public void majid() {
		majid = new Owner();
		majid.setFirstName("Haaj");
		majid.setLastName("Majid");
		majid.setAddress("Majidie");
		majid.setCity("Tehran");
		majid.setTelephone("09111111111");
		ownerRepository.save(majid);
	}

	@When("Majid asks for a new pet")
	public void majidAsksForNewPet() {
		newPet = petService.newPet(majid);
	}

	@When("Majid performs save pet service to add a pet to his list")
	public void majidPerformsSavePetService() {
		newPet = new Pet();
		newPet.setType(petType);
		petService.savePet(newPet, majid);
	}

	@Then("The pet is saved")
	public void thePetIsSaved() {
		assertNotNull(petService.findPet(newPet.getId()));
	}

	@Then("A new pet is given to majid")
	public void newPetIsGivenToMajid() {
		assertTrue(majid.getPets().contains(newPet));
	}

	@Given("There is a pet with id {int}")
	public void thereIsAPetWithId(int id) {
		assertNotNull(petRepository.findById(id));
	}

	@Given("There is no pet with id {int}")
	public void thereIsNoPetWithId(int id) {
		assertNull(petRepository.findById(id));
	}

	@When("Someone wants to find the pet with id {int}")
	public void findPetWithId(int id) {
		foundPet = petService.findPet(id);
	}

	@Then("Pet with id {int} is found")
	public void petIdReturned(int id) {
		assertNotNull(foundPet);
		assertEquals(id, foundPet.getId());
	}

	@Then("No pet is found")
	public void noPetIsFound() {
		assertNull(foundPet);
	}

	@Given("There is an owner with id {int}")
	public void thereIsOwnerWithId(int id) {
		assertNotNull(ownerRepository.findById(id));
	}

	@Given("There is no owner with id {int}")
	public void thereIsNoOwnerWithId(int id) {
		assertNull(ownerRepository.findById(id));
	}

	@When("Someone wants to find the account with id {int}")
	public void findAccountWithId(int id) {
		foundOwner = petService.findOwner(id);
	}

	@Then("Account with id {int} is found")
	public void accountIsFound(int id) {
		assertNotNull(foundOwner);
		assertEquals(id, foundOwner.getId());
	}

	@Then("No owner is found")
	public void noOwnerIsFound() {
		assertNull(foundOwner);
	}

	@Given("There is a pet type like {string}")
	public void thereIsAPetTypeLike(String petTypeName) {
		petType = new PetType();
		petType.setName(petTypeName);
		petTypeRepository.save(petType);
	}
}
