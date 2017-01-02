package fr.sgr.formation.voteapp.utilisateurs.services;

import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.AuthentificationException.ErreurAuthentification;

/**
 * Bean mettant à disposition les services permettant de vérifier le profil d'un
 * utilisateur (administrateur / gerant / utilisateur)
 */
@Service
public class UtilisateurProfil {

	public boolean isAdministrateur(Utilisateur utilisateur) throws AuthentificationException {

		/** Validation de l'existence de l'utilisateur. */
		if (utilisateur == null) {
			throw new AuthentificationException(ErreurAuthentification.UTILISATEUR_INEXISTANT);
		}

		if (utilisateur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			return true;
		}
		return false;
	}

	public boolean isGerant(Utilisateur utilisateur) throws AuthentificationException {

		/** Validation de l'existence de l'utilisateur. */
		if (utilisateur == null) {
			throw new AuthentificationException(ErreurAuthentification.UTILISATEUR_INEXISTANT);
		}

		if (utilisateur.getProfils().contains(ProfilsUtilisateur.GERANT)) {
			return true;
		}
		return false;
	}

	public boolean isUtilisateur(Utilisateur utilisateur) throws AuthentificationException {

		/** Validation de l'existence de l'utilisateur. */
		if (utilisateur == null) {
			throw new AuthentificationException(ErreurAuthentification.UTILISATEUR_INEXISTANT);
		}

		if (utilisateur.getProfils().contains(ProfilsUtilisateur.UTILISATEUR)) {
			return true;
		}
		return false;
	}

}
