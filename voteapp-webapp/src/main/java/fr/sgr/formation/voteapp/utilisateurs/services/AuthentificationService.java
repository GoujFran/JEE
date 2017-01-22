package fr.sgr.formation.voteapp.utilisateurs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException.ErreurAuthentification;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Vérification du profil de l'tilisateur identifié par son login
 *
 */
@Slf4j
@Service
public class AuthentificationService {
	@Autowired
	private UtilisateursServices utilisateursServices;
	@Autowired
	private UtilisateurProfil utilisateurProfil;
	@Autowired
	private TraceService traceService;

	/**
	 * lève une exception si l'utilisateur identifié par son login n'existe pas
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @throws AuthentificationException
	 */
	public void verificationExistence(Utilisateur utilisateur) throws AuthentificationException {
		log.info("=====> Vérification de l'existence de l'utilisateur {}.", utilisateur);

		/** Validation de l'existence de l'utilisateur. */
		if (utilisateur == null) {
			traceService.creerTraceErreur("UTILISATEUR_INEXISTANT");
			throw new AuthentificationException(ErreurAuthentification.UTILISATEUR_INEXISTANT);
		}
	}

	/**
	 * lève une exception si l'utilisateur identifié par son login n'est pas
	 * administrateur
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @throws AuthentificationException
	 */
	public void verificationAdministrateur(String login) throws AuthentificationException {
		log.info("=====> Vérification du statut d'aministrateur de l'utilisateur de login {}.", login);
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);

		verificationExistence(utilisateur);

		/** Validation du statut d'administrateur correspondant au login */
		if (!utilisateurProfil.isAdministrateur(utilisateur)) {
			traceService.creerTraceErreur("ADMINISTRATEUR_OBLIGATOIRE");
			throw new AuthentificationException(ErreurAuthentification.ADMINISTRATEUR_OBLIGATOIRE);
		}
	}

	/**
	 * lève une exception si le mot de passe ne correspond pas à celui de
	 * l'utilisateur
	 * 
	 * @param motDePasse
	 *            mot de passe à tester.
	 * @throws AuthentificationException
	 */
	public void verificationMotdePasse(Utilisateur utilisateur, String motDePasse) throws AuthentificationException {
		log.info("=====> Vérification du mot de passe {}.", motDePasse);

		verificationExistence(utilisateur);

		/** Validité du mot de passe */
		if (!utilisateur.getMotDePasse().equals(motDePasse)) {
			traceService.creerTraceErreur("MAUVAIS_MDP");
			throw new AuthentificationException(ErreurAuthentification.MAUVAIS_MDP);
		}
	}

	/**
	 * lève une exception si l'utilisateur identifié par son login n'est pas
	 * gérant
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @throws AuthentificationException
	 */
	public void verificationGerant(String login) throws AuthentificationException {
		log.info("=====> Vérification du statut de gérant de l'utilisateur de login {}.", login);
		Utilisateur utilisateur = utilisateursServices.rechercherParLogin(login);

		verificationExistence(utilisateur);

		/** Validation du statut d'administrateur correspondant au login */
		if (!utilisateurProfil.isGerant(utilisateur)) {
			traceService.creerTraceErreur("GERANT_OBLIGATOIRE");
			throw new AuthentificationException(ErreurAuthentification.GERANT_OBLIGATOIRE);
		}
	}

}
