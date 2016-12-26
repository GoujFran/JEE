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

		/** Validation du statut d'administrateur correspondant au login */
		if (!utilisateur.getMotDePasse().equals(motDePasse)) {
			throw new AuthentificationException(ErreurAuthentification.MAUVAIS_MDP);
		}
	}

}
