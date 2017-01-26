package fr.sgr.formation.voteapp.utilisateurs.ws;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.fonctionnementInterne.RetourPagine;
import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
import fr.sgr.formation.voteapp.utilisateurs.modele.Trace;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationService;
import fr.sgr.formation.voteapp.utilisateurs.services.TraceService;
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
	@Autowired
	private TraceService traceService;
	@Autowired
	public static Trace traceStatic = new Trace();

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
		log.info("=====> Création de l'utilisateur {}.", utilisateur);

		traceStatic.setTypeAction("Création d'un utilisateur");
		traceStatic.setDescription("Création");
		traceStatic.setUtilisateur(utilisateursServices.rechercherParLogin(login));

		authentificationService.verificationAdministrateur(login);
		utilisateursServices.creer(utilisateur);

		traceService.creerTraceOK();
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
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date date,
			@RequestBody(required = false) Adresse adresse, @RequestParam(required = false) String loginUtilisateur,
			@RequestParam(required = false) String[] profil)
					throws UtilisateurInvalideException, AuthentificationException {
		traceStatic.setTypeAction("Modification d'un utilisateur");
		traceStatic.setDescription("Modification");
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		traceStatic.setUtilisateur(utilisateur);

		/** Validation de l'existence des utilisateurs. */
		authentificationService.verificationExistence(utilisateur);

		/** modification de l'utilisateur */
		utilisateursServices.modifier(utilisateur, loginUtilisateur, nom, prenom, email, motDePasse, date, adresse,
				profil);

		traceService.creerTraceOK();

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
		traceStatic.setTypeAction("Récupération d'un utilisateur");
		traceStatic.setDescription("Récupération");
		log.info("=====> Récupération de l'utilisateur de login {}.", login);
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		traceStatic.setUtilisateur(utilisateur);

		/** Validation de l'existence de l'utilisateur. */
		authentificationService.verificationExistence(utilisateur);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);

		traceService.creerTraceOK();
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
	public RetourPagine lister(@PathVariable String login, @RequestParam(required = false) String nom,
			@RequestParam(required = false) String prenom, @RequestParam(required = false) String ville,
			@RequestParam(required = false) String profil, @RequestParam(required = false) Integer nbItems,
			@RequestParam(required = false) Integer numeroPage) throws AuthentificationException {
		log.info("=====> Récupération de la liste des utilisateurs.");
		traceStatic.setTypeAction("Récupération de la liste des utilisateurs.");
		traceStatic.setDescription("Récupération utilisateurs");
		traceStatic.setUtilisateur(utilisateursServices.rechercherParLogin(login));
		authentificationService.verificationAdministrateur(login);
		List<Utilisateur> listUsers;
		listUsers = utilisateursServices.getListe(nom, prenom, ville, profil);
		RetourPagine res = new RetourPagine(listUsers, nbItems, numeroPage);
		traceService.creerTraceOK();
		return res;
	}

	/**
	 * methode pour récupérer la liste des traces du systeme
	 * 
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "traces")
	public RetourPagine listerTraces(@PathVariable String login,
			@RequestParam(required = false) String loginUtilisateur,
			@RequestParam(required = false) String nomUtilisateur, @RequestParam(required = false) String typeAction,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateDebut,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateFin,
			@RequestParam(required = false) Integer nbItems, @RequestParam(required = false) Integer numeroPage)
					throws AuthentificationException {
		log.info("=====> Récupération de la liste des traces.");
		traceStatic.setTypeAction("Récupération de la liste des traces.");
		traceStatic.setDescription("Récupération traces");
		traceStatic.setUtilisateur(utilisateursServices.rechercherParLogin(login));

		authentificationService.verificationAdministrateur(login);
		List<Trace> listTraces;
		listTraces = traceService.getListe(loginUtilisateur, nomUtilisateur, typeAction, dateDebut, dateFin);
		RetourPagine res = new RetourPagine(listTraces, nbItems, numeroPage);

		traceService.creerTraceOK();
		return res;
	}

	// Pour tester : http://localhost:8080/utilisateurs/123/newMDP
	/**
	 * methode pour demander à changer de mot de passe
	 * 
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "newMDP")
	public String demanderNouveauMDP(@PathVariable String login) throws AuthentificationException {
		log.info("=====> Nouveau mot de passe.");
		traceStatic.setTypeAction("Nouveau mot de passe");
		traceStatic.setDescription("Nouveau mot de passe");
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
		traceStatic.setUtilisateur(utilisateursServices.rechercherParLogin(login));

		authentificationService.verificationExistence(utilisateur);
		utilisateursServices.nouveauMotDePasse(utilisateur);
		String notifications = "Le changement de mot de passe a bien été effectué.";

		traceService.creerTraceOK();
		return notifications;

	}

	/**
	 * Lister les élections
	 * 
	 * @param login
	 * @param motDePasse
	 * @param titre
	 * @param cloture
	 * @param createurLogin
	 * @return
	 * @throws AuthentificationException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "listerElections")
	public List<Election> listerElection(@PathVariable String login, @RequestParam String motDePasse,
			@RequestParam(required = false) String titre, @RequestParam(required = false) String cloture,@RequestParam(required = false) String createurLogin)
            throws AuthentificationException {
        log.info("=====> Lister les élections par {}.", login);

        Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);
        
        /** Validation de l'existence de l'utilisateur. */
		authentificationService.verificationExistence(utilisateur);
		authentificationService.verificationMotdePasse(utilisateur, motDePasse);
        
		Utilisateur createur = null;
		
		if (createurLogin != null) {
			createur = utilisateursServices.rechercherParLogin(createurLogin);
        
			authentificationService.verificationExistence(createur);
		} 

        List<Election> listeElection = utilisateursServices.listerElection(titre,cloture,createur);
        return listeElection;
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
