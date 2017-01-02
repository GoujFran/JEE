package fr.sgr.formation.voteapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.services.VilleService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class Initialisation {

	@Autowired
	private VilleService villeService;

	@Autowired
	private UtilisateursServices utilisateursServices;

	@SuppressWarnings("deprecation")
	@PostConstruct
	@Transactional(propagation = Propagation.REQUIRED)
	public void init() {
		log.info("Initialisation des villes par défaut dans la base...");
		Ville rennes = new Ville();
		rennes.setCodePostal("35000");
		rennes.setNom("Rennes");

		villeService.creer(rennes);

		log.info("Initialisation d'un utilisateur et administrateur par défaut dans la base...");
		List<ProfilsUtilisateur> liste = new ArrayList<ProfilsUtilisateur>();
		liste.add(ProfilsUtilisateur.ADMINISTRATEUR);
		liste.add(ProfilsUtilisateur.UTILISATEUR);
		Adresse adresse = new Adresse();
		adresse.setRue("rue truc bidule");
		adresse.setVille(rennes);
		Date dateNaiss = new Date();
		dateNaiss.setYear(1994);
		dateNaiss.setMonth(8);
		dateNaiss.setDate(7);
		Utilisateur laure = new Utilisateur("123", "nicollet", "laure", "mdp", dateNaiss,
				"laure.nicollet@eleve.ensai.fr", "http://photodelutilisateur.jpg", liste, adresse);

		try {
			utilisateursServices.creer(laure);
		} catch (UtilisateurInvalideException e) {
			e.printStackTrace();
		}
	}

}
