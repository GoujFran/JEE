package fr.sgr.formation.voteapp.elections.services;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.elections.modele.Choix;
import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.modele.Vote;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.TraceService;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException.ErreurUtilisateur;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class ElectionService {

	/** Services de validation d'une élection. */
	@Autowired
	private ValidationElectionServices validationServices;
	/** Services de notification des événements. */

	@Autowired
	private NotificationsServices notificationsServices;

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private TraceService traceService;

	/**
	 * créer une élection
	 * 
	 * @param election
	 * @return
	 * @throws ElectionInvalideException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Election creerElection(Election election) throws ElectionInvalideException {
		log.info("=====> Création de l'élection : {}.", election);

		if (election == null) {
			traceService.creerTraceErreur("ELECTION_OBLIGATOIRE");
			throw new ElectionInvalideException(ErreurElection.ELECTION_OBLIGATOIRE);
		}

		if (election.getId() == null || election.getId().equals("")) {
			traceService.creerTraceErreur("ID_OBLIGATOIRE");
			throw new ElectionInvalideException(ErreurElection.ID_OBLIGATOIRE);
		}

		if (election.getProprietaire() == null) {
			traceService.creerTraceErreur("PROPRIETAIRE_OBLIGATOIRE");
			throw new ElectionInvalideException(ErreurElection.PROPRIETAIRE_OBLIGATOIRE);
		}

		if (election.getTitre() == null || election.getTitre().equals("")) {
			traceService.creerTraceErreur("TITRE_OBLIGATOIRE");
			throw new ElectionInvalideException(ErreurElection.TITRE_OBLIGATOIRE);
		}

		if (election.getDescription() == null || election.getDescription().equals("")) {
			traceService.creerTraceErreur("DESCRIPTION_OBLIGATOIRE");
			throw new ElectionInvalideException(ErreurElection.DESCRIPTION_OBLIGATOIRE);
		}
		
		/** Validation de l'existance de l'utilisateur. */
		if (entityManager.find(Election.class, election.getId()) != null) {
			traceService.creerTraceErreur("ELECTION_EXISTANTE");
			throw new ElectionInvalideException(ErreurElection.ELECTION_EXISTANTE);
		}

		/**
		 * Validation de l'élection: lève une exception si l'élection est
		 * invalide.
		 */
		validationServices.validerElection(election);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de l'élection" + election.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(election);

		return election;

	}

	/**
	 * Retourne l'élection identifié par l'id
	 * 
	 * @param id
	 *            Login identifiant l'élection voulu
	 * @return Retourne l'élection identifié par l'id
	 * @throws ElectionInvalideException 
	 */
	public Election recupererElection(String id) throws ElectionInvalideException {
		log.info("=====> Recherche de l'élection d'id {}.", id);
		Election election = null;
		if (!id.isEmpty()) {
			election = entityManager.find(Election.class, id);
		}
		if (election == null) {
			traceService.creerTraceErreur("ELECTION_INEXISTANTE");
			throw new ElectionInvalideException(ErreurElection.ELECTION_INEXISTANTE);
		}
		return election;
	}

	/**
	 * cloturer l'élection par le créateur
	 * 
	 * @param election
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void fermerElection(Election election) {
		log.info("=====> Recherche de l'élection {}.", election);
		Date date = new Date();
		election.setDateCloture(date);
	}

	/**
	 * modifier la description de l'élection par le créateur
	 * 
	 * @param election
	 * @param titre
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void modifierTitre(Election election, String titre) {
		log.info("=====> Modifier le titre de l'élection {} par {}.", election, titre);
		election.setTitre(titre);
	}

	/**
	 * modifier le titre de l'élection par le créateur
	 * 
	 * @param election
	 * @param titre
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void modifierDescription(Election election, String description) {
		log.info("=====> Modifier la description de l'élection {} par {}.", election, description);
		election.setDescription(description);
	}

	/**
	 * modifier les images de l'élection par le créateur
	 * 
	 * @param election
	 * @param images
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void modifierImages(Election election, List<String> images) {
		log.info("=====> Modifier les images de l'élection {} par {}.", election, images);
		election.setImages(images);
	}

	/**
	 * Consulter les résultats d'une élection
	 * 
	 * @param id
	 * @return
	 * @throws ElectionInvalideException
	 */
	public HashMap<Choix, Integer> consulterRésultats(String id) throws ElectionInvalideException {
		Election election = recupererElection(id);
		if (election.getDateCloture() == null) {
			traceService.creerTraceErreur("ELECTION_NON_CLOTUREE");
			throw new ElectionInvalideException(ErreurElection.ELECTION_NON_CLOTUREE);
		}
		Query requete = entityManager.createQuery(
				"SELECT v FROM Vote v WHERE v.election.id=:id");
		requete.setParameter("id", election.getId());
		List<Vote> listeVote = requete.getResultList();
		HashMap<Choix, Integer> resultatVotes = new HashMap<Choix, Integer>();
		resultatVotes.put(Choix.OUI, 0);
		resultatVotes.put(Choix.NON, 0);
		resultatVotes.put(Choix.BLANC, 0);
		for (Vote vote : listeVote) {
			resultatVotes.put(vote.getChoix(), resultatVotes.get(vote.getChoix()) + 1);
		}
		return resultatVotes;
	}

	/**
	 * verifier si l'utilisateur relié au login est propriétaire de l'élection
	 * 
	 * @param election
	 * @param login
	 * @throws ElectionInvalideException
	 */
	public void verifierProprietaire(Election election, String login) throws ElectionInvalideException {
		log.info("=====> Vérification si {} propriétaire de {}.", login, election);
		if (!login.equals(election.getProprietaire().getLogin())) {
			traceService.creerTraceErreur("NON_PROPRIETAIRE");
			throw new ElectionInvalideException(ErreurElection.NON_PROPRIETAIRE);
		}
	}

	/**
	 * permet à utilisateur de voter à une élection
	 * 
	 * @param election
	 * @param utilisateur
	 * @param choix
	 * @throws ElectionInvalideException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void voter(Election election, Utilisateur utilisateur, String choix) throws ElectionInvalideException {
		log.info("=====> Vote de {} à {}.", utilisateur, election);

		Choix choixFinale;
		if (choix.equals("oui")) {
			choixFinale = Choix.OUI;
		} else if (choix.equals("non")) {
			choixFinale = Choix.NON;
		} else if (choix.equals("blanc")) {
			choixFinale = Choix.BLANC;
		} else {
			traceService.creerTraceErreur("VOTE_NON_VALIDE");
			throw new ElectionInvalideException(ErreurElection.VOTE_NON_VALIDE);
		}

		Query requete = entityManager.createQuery(
				"SELECT v FROM Vote v WHERE v.election.id=:id");
		requete.setParameter("id", election.getId());
		List<Vote> listeVote = requete.getResultList();
		List<Utilisateur> listeVotant = new LinkedList<Utilisateur>();
		for (Vote vote : listeVote) {
			listeVotant.add(vote.getUtilisateur());
		}

		if (listeVotant.contains(utilisateur)) {
			traceService.creerTraceErreur("DEJA_VOTE");
			throw new ElectionInvalideException(ErreurElection.DEJA_VOTE);
		}

		if (!(election.getDateCloture() == null)) {
			traceService.creerTraceErreur("ELECTION_CLOTUREE");
			throw new ElectionInvalideException(ErreurElection.ELECTION_CLOTUREE);
		}
		Vote vote = new Vote("e" + election.getId() + "u" + utilisateur.getLogin(), utilisateur, election, choixFinale);
		entityManager.persist(vote);
	}

}
