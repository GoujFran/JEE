package fr.sgr.formation.voteapp.utilisateurs.services;

import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;

/**
 * Bean mettant à disposition les services permettant de vérifier le profil d'un
 * utilisateur (administrateur / gerant / utilisateur)
 */
@Service
public class UtilisateurProfil {

	public boolean isAdministrateur(Utilisateur utilisateur) {
		// TODO
		return true;
	}

	public boolean isGerant(Utilisateur utilisateur) {
		// TODO
		return true;
	}

	public boolean isUtilisateur(Utilisateur utilisateur) {
		// TODO
		return true;
	}

}
