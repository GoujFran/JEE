package fr.sgr.formation.voteapp.elections.ws;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.elections.modele.Choix;
import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.elections.services.ElectionService;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationService;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.ws.DescriptionErreur;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("elections/{id}")
@Slf4j
public class ElectionRest {

	@Autowired
	private UtilisateursServices utilisateursServices;
	@Autowired
	private ElectionService electionService;
	@Autowired
	private AuthentificationService authentificationService;

	/**
	 * méthode pour creer une éléction dans le systeme / le id figurant dans
	 * l'URL est celui du gérant qui cree l'élection / le corps de la requete
	 * est l'élection a creer
	 * 
	 * @param id
	 * @param election
	 * @throws ElectionInvalideException
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void creer(@PathVariable String id, @RequestBody Election election)
			throws AuthentificationException, ElectionInvalideException {
		log.info("=====> Création de l'élection {}.", election);
		authentificationService.verificationGerant(election.getProprietaire().getLogin());
		electionService.creerElection(election);
	}

	/**
	 * méthode pour récupérer une élection dans le systeme
	 * 
	 * @param id
	 */
	@RequestMapping(method = RequestMethod.GET)
	public Election lire(@PathVariable String id) {
		log.info("=====> Récupération de l'élection {}.", id);
		Election election = electionService.recupererElection(id);
		return election;
	}

	/**
	 * méthode pour rconsulter les résultats
	 * 
	 * @param id
	 * @param login
	 * @param motDePasse
	 * @return
	 * @throws ElectionInvalideException
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "consulterResultat")
	public HashMap<Choix, Integer> consulterResultats(@PathVariable String id, @RequestParam String login,
			@RequestParam String motDePasse) throws ElectionInvalideException, AuthentificationException {
		log.info("=====> Récupération de l'élection {}.", id);
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		/** Validation de l'existence de l'utilisateur. */
		authentificationService.verificationExistence(utilisateur);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		HashMap<Choix, Integer> resultats = electionService.consulterRésultats(id);
		return resultats;
	}

	/**
	 * méthode pour cloturer l'élection par le créateur
	 * 
	 * @param id
	 * @param login
	 * @throws AuthentificationException
	 * @throws ElectionInvalideException
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "cloturer")
	public void cloturer(@PathVariable String id, @RequestParam String login, @RequestParam String motDePasse)
			throws AuthentificationException, ElectionInvalideException {
		log.info("=====> Cloture de l'élection {} par {}.", id, login);

		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		Election election = electionService.recupererElection(id);

		electionService.verifierProprietaire(election, login);
		electionService.fermerElection(election);
	}

	/**
	 * méthode pour modifier l'élection par le créateur
	 * 
	 * @param id
	 * @param login
	 * @throws AuthentificationException
	 * @throws ElectionInvalideException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void modifier(@PathVariable String id, @RequestParam String login, @RequestParam String motDePasse,
			@RequestParam(required = false) String titre, @RequestParam(required = false) String description,
			@RequestBody(required = false) List<String> images)
					throws AuthentificationException, ElectionInvalideException {
		log.info("=====> Cloture de l'élection {} par {}.", id, login);

		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		Election election = electionService.recupererElection(id);

		electionService.verifierProprietaire(election, login);

		if (titre != null && !titre.isEmpty()) {
			electionService.modifierTitre(election, titre);
		}

		if (description != null && !description.isEmpty()) {
			electionService.modifierDescription(election, description);
		}

		if (images != null && !images.isEmpty()) {
			electionService.modifierImages(election, images);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, path = "voter")
	public void voter(@PathVariable String id, @RequestParam String login, @RequestParam String motDePasse,
			@RequestParam String choix) throws AuthentificationException, ElectionInvalideException {
		log.info("=====> Cloture de l'élection {} par {}.", id, login);

		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		Election election = electionService.recupererElection(id);

		electionService.voter(election, utilisateur, choix);
	}

	@ExceptionHandler({ UtilisateurInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(UtilisateurInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

	@ExceptionHandler({ ElectionInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(ElectionInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

	@ExceptionHandler({ AuthentificationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(AuthentificationException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}
}
