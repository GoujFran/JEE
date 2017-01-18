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

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.elections.services.ElectionService;
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

	@Autowired
	private ElectionService electionService;

	@SuppressWarnings("deprecation")
	@PostConstruct
	@Transactional(propagation = Propagation.REQUIRED)
	public void init() {
		log.info("Initialisation des villes par défaut dans la base...");
		Ville rennes = new Ville();
		rennes.setCodePostal("35000");
		rennes.setNom("Rennes");
		villeService.creer(rennes);

		Ville bruz = new Ville();
		bruz.setCodePostal("35170");
		bruz.setNom("Bruz");
		villeService.creer(bruz);

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

		log.info("Initialisation d'un gestionnaire et d'une élection par défault dans la base");

		List<ProfilsUtilisateur> liste2 = new ArrayList<ProfilsUtilisateur>();
		liste2.add(ProfilsUtilisateur.GERANT);
		liste2.add(ProfilsUtilisateur.UTILISATEUR);
		Adresse adresse2 = new Adresse();
		adresse2.setRue("rue Louis Armand");
		adresse2.setVille(bruz);
		Date dateNaiss2 = new Date();
		dateNaiss2.setYear(1994);
		dateNaiss2.setMonth(8);
		dateNaiss2.setDate(7);
		Utilisateur francoise = new Utilisateur("456", "goujard", "francoise", "pw", dateNaiss2,
				"francoise.goujard@eleve.ensai.fr", "http://rancoise_goujard.jpg", liste2, adresse2);

		try {
			utilisateursServices.creer(francoise);
		} catch (UtilisateurInvalideException e) {
			e.printStackTrace();
		}

		Election election = new Election("01", francoise, "Election", "Une election se prépare");

		System.out.println("Election " + election.toString());

		try {
			electionService.creerElection(election);
		} catch (ElectionInvalideException e) {
			e.printStackTrace();
		}

	}

}
