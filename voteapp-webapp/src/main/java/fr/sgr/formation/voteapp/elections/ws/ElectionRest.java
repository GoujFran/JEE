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
	 * methode pour creer une éléction dans le systeme / le login figurant dans
	 * l'URL est celui du gérant qui cree l'élection / le corps de la requete
	 * est l'élection a creer
	 * 
	 * @param login
	 * @param election
	 * @throws ElectionInvalideException
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void creer(@PathVariable String login, @RequestBody Election election)
			throws AuthentificationException, ElectionInvalideException {
		log.info("=====> Création ou modification de l'utilisateur {}.", election);
		authentificationService.verificationGerant(login);
		electionService.creerElection(election);
	}
}
