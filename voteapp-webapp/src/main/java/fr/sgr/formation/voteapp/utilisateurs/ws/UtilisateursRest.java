package fr.sgr.formation.voteapp.utilisateurs.ws;

import java.util.ArrayList;
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

import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
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
	@RequestMapping(method = RequestMethod.POST)
	public void creer(@PathVariable String login, @RequestBody Utilisateur utilisateur)
			throws UtilisateurInvalideException, AuthentificationException {
		log.info("=====> Création ou modification de l'utilisateur {}.", utilisateur);
		authentificationService.verificationAdministrateur(login);
		utilisateursServices.creer(utilisateur);
	}

	/**
	 * methode pour modifier un utilisateur dans le systeme
	 * 
	 * @param login
	 * @param utilisateur
	 * @throws UtilisateurInvalideException
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void modifier(@PathVariable String login, @RequestParam(required = false) String nom,
			@RequestParam(required = false) String prenom, @RequestParam(required = false) String email,
			@RequestParam(required = false) String motDePasse,
			@RequestParam(required = false) String date,
			@RequestBody(required = false) Adresse adresse, @RequestParam(required = false) String loginUtilisateur,
			@RequestParam(required = false) String admin)
					throws UtilisateurInvalideException, AuthentificationException {
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);

		if (loginUtilisateur != null && !loginUtilisateur.isEmpty()) {
			authentificationService.verificationAdministrateur(login);
			utilisateur = utilisateursServices.rechercherParLogin(loginUtilisateur);
			if (admin != null && !admin.isEmpty()) {
				utilisateur = utilisateursServices.toAdmin(utilisateur);
			}
		}

		log.info("=====> Modification de l'utilisateur {}.", utilisateur);
		if (nom != null && !nom.isEmpty()) {
			utilisateur = utilisateursServices.modifierNom(utilisateur, nom);
		}

		if (prenom != null && !prenom.isEmpty()) {
			utilisateur = utilisateursServices.modifierPrenom(utilisateur, prenom);
		}

		if (email != null && !email.isEmpty()) {
			utilisateur = utilisateursServices.modifierEmail(utilisateur, email);
		}

		// Exemple test :
		// http://localhost:8080/utilisateurs/123/?motDePasse=mdp1
		if (motDePasse != null && !motDePasse.isEmpty()) {
			utilisateur = utilisateursServices.modifierMDP(utilisateur, motDePasse);
		}

		// La date en string doit être écrite au format JJ/MM/YYYY
		// Attention (TODO ne pas oublier): Les dates doivent être corrigées
		// (YYYY-1900 et MM-1)
		if (date != null && !date.isEmpty()) {
			utilisateur = utilisateursServices.modifierDateNaissance(utilisateur, date);
		}

		if (adresse != null) {
			utilisateur = utilisateursServices.modifierAdresse(utilisateur, adresse);
		}

	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void supprimer(@PathVariable String login) {
		log.info("=====> Suppression de l'utilisateur de login {}.", login);

	}

	// Pour tester le get :
	// http://localhost:8080/utilisateurs/123/?motDePasse=mdp
	// Attention il n'y a pas de guillemet autour de mdp
	/**
	 * methode pour récupérer un utilisateur dans le systeme
	 * 
	 * @param login
	 * @param motDePasse
	 * @param utilisateur
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public Utilisateur lire(@PathVariable String login, @RequestParam String motDePasse)
			throws AuthentificationException {
		log.info("=====> Récupération de l'utilisateur de login {}.", login);
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);
		return utilisateur;
	}

	// pour tester : http://localhost:8080/utilisateurs/123/liste
	// ou http://localhost:8080/utilisateurs/123/liste/?prenom=laure
	// http://localhost:8080/utilisateurs/123/liste/?nom=nicollet&prenom=laure
	/**
	 * methode pour récupérer la liste des utilisateurs du systeme
	 * 
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "liste")
	public List<Utilisateur> lister(@PathVariable String login, @RequestParam(required = false) String nom,
			@RequestParam(required = false) String prenom, @RequestParam(required = false) String ville,
			@RequestParam(required = false) String profil) throws AuthentificationException {
		log.info("=====> Récupération de la liste des utilisateurs.");
		authentificationService.verificationAdministrateur(login);
		List<Utilisateur> res;
		res = utilisateursServices.getListe(nom, prenom, ville, profil);
		// ----------------------------------------------------------------------------------------------------
		res = new ArrayList<>();
		res.add(new Utilisateur("id0516", nom, prenom, null, null, null, null, null, null));
		// ----------------------------------------------------------------------------------------------------
		return res;
	}

	@ExceptionHandler({ UtilisateurInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(UtilisateurInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

	@ExceptionHandler({ AuthentificationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(AuthentificationException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}
}
