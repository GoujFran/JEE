package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;

@Service
public class ProfilsServices {

	public void attribuerProfils(Utilisateur user, String[] profils) {
		List<ProfilsUtilisateur> nouveauxProfils = new ArrayList<ProfilsUtilisateur>();
		for (int i = 0; i < profils.length; i++) {
			if (profils[i].toLowerCase().equals("administrateur")) {
				nouveauxProfils.add(ProfilsUtilisateur.ADMINISTRATEUR);
			}
			if (profils[i].toLowerCase().equals("utilisateur")) {
				nouveauxProfils.add(ProfilsUtilisateur.UTILISATEUR);
			}
			if (profils[i].toLowerCase().equals("gerant")) {
				nouveauxProfils.add(ProfilsUtilisateur.GERANT);
			}
		}
		user.setProfils(nouveauxProfils);
	}

}
