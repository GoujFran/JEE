package fr.sgr.formation.voteapp.elections.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.elections.services.ElectionService;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("elections/{id}")
@Slf4j
public class ElectionRest {

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
		log.info("=====> Création ou modification de l'utilisateur {}.", election);
		authentificationService.verificationGerant(id);
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
}
