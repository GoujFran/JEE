package fr.sgr.formation.voteapp.ws;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtilisateurService {

	public void enregistrer(String login, Utilisateur utilisateur) {
		if ((login.equals("") || login == null)
				&& (utilisateur.getPrenom().equals("") || utilisateur.getPrenom() == null)
				&& (utilisateur.getNom().equals("") || utilisateur.getNom() == null)) {
			log.info("=====> enregistrer en base");
		} else {
			log.info("=====> Champs non valides");
		}
	}

	public void chercher(String login) {
		log.info("=====> chercher dans la base");
	}

}
