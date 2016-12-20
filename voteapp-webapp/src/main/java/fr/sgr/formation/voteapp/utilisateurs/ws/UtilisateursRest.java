package fr.sgr.formation.voteapp.utilisateurs.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationService;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("utilisateurs/{login}")
@Slf4j
public class UtilisateursRest {
	@Autowired
	private UtilisateursServices utilisateursServices;
	@Autowired
	private AuthentificationService authentificationService;

	/**
	 * methode pour creer un utilisateur dans le systeme / le login figurant
	 * dans l'URL est celui de l'admin qui cree l'utilisateur / le corps de la
	 * requete est l'utiliateur a creer
	 * 
	 * @param login
	 * @param utilisateur
	 * @throws UtilisateurInvalideException
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void creer(@PathVariable String login, @RequestBody Utilisateur utilisateur)
			throws UtilisateurInvalideException, AuthentificationException {
		log.info("=====> Création ou modification de l'utilisateur {}.", utilisateur);
		authentificationService.verificationAdministrateur(login);
		utilisateursServices.creer(utilisateur);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void supprimer(@PathVariable String login) {
		log.info("=====> Suppression de l'utilisateur de login {}.", login);

	}

	@RequestMapping(method = RequestMethod.GET)
	public Utilisateur lire(@PathVariable String login) {
		log.info("=====> Récupération de l'utilisateur de login {}.", login);

		return utilisateursServices.rechercherParLogin(login);
	}

	@ExceptionHandler({ UtilisateurInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(UtilisateurInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}
}
