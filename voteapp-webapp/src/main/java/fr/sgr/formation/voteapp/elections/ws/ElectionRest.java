package fr.sgr.formation.voteapp.elections.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.elections.services.ElectionService;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationService;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
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
	 * méthode pour récupérer un utilisateur dans le systeme
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
	 * méthode pour cloturer l'élection par le créateur
	 * 
	 * @param id
	 * @param login
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void cloturer(@PathVariable String id, @RequestParam String login, @RequestParam String motDePasse)
			throws AuthentificationException {
		log.info("=====> Cloture de l'élection {} par {}.", id, login);

		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		Election election = electionService.recupererElection(id);

		if (login.equals(election.getProprietaire().getLogin())) {
			electionService.fermerElection(election);
		}
	}
}
