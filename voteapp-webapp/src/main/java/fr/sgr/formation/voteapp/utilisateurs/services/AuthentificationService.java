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

		/** Validation du statut d'administrateur correspondant au login */
		if (!utilisateurProfil.isAdministrateur(utilisateur)) {
			// TODO : cette exception ne fonctionne pas
			throw new AuthentificationException(ErreurAuthentification.ADMINISTRATEUR_OBLIGATOIRE);
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

		/** Validation du statut d'administrateur correspondant au login */
		if (!utilisateurProfil.isGerant(utilisateur)) {
			// TODO : cette exception ne fonctionne pas
			throw new AuthentificationException(ErreurAuthentification.GERANT_OBLIGATOIRE);
		}
	}

}
