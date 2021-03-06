package fr.sgr.formation.voteapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		try {
			dateNaiss = new SimpleDateFormat("dd/MM/yyyy").parse("07/08/1994");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
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
		try {
			dateNaiss2 = new SimpleDateFormat("dd/MM/yyyy").parse("12/02/1994");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Utilisateur francoise = new Utilisateur("456", "goujard", "francoise", "pw", dateNaiss2,
				"francoise.goujard@eleve.ensai.fr", "http://rancoise_goujard.jpg", liste2, adresse2);

		try {
			utilisateursServices.creer(francoise);
		} catch (UtilisateurInvalideException e) {
			e.printStackTrace();
		}

		Election election = new Election("01", francoise, "Election", "Une election se prépare");
		Election election2 = new Election("02", francoise, "Election2", "Une election se prépare2");
		Election election3 = new Election("03", francoise, "Election3", "Une election se prépare3");

		try {
			electionService.creerElection(election);
			electionService.creerElection(election2);
			electionService.creerElection(election3);
			electionService.voter(election, laure, "oui");
			electionService.voter(election, francoise, "non");
		} catch (ElectionInvalideException e) {
			e.printStackTrace();
		}

		// initialisation de plusieurs utilisateurs pour des tests
		List<ProfilsUtilisateur> profilUtilisateur = new ArrayList<ProfilsUtilisateur>();
		profilUtilisateur.add(ProfilsUtilisateur.UTILISATEUR);
		Adresse adr = new Adresse();
		Date dNaiss = new Date();
		for (int i = 1; i < 30; i++) {
			adr.setRue("rue n°" + i);
			adr.setVille(rennes);
			try {
				dNaiss = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1987");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			try {
				utilisateursServices
						.creer(new Utilisateur(String.valueOf(i), "nom" + i, "prenom" + i, "mdp" + i, dNaiss,
								"mail" + i + "@blabla.fr", "http://photo" + i + "delutilisateur.jpg", profilUtilisateur,
								adr));
			} catch (UtilisateurInvalideException e) {
				e.printStackTrace();
			}
		}

	}

}
