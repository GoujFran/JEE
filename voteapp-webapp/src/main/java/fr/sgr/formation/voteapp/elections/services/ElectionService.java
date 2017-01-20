package fr.sgr.formation.voteapp.elections.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.modele.Vote;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public Election creerElection(Election election) throws ElectionInvalideException {
		log.info("=====> Création de l'élection : {}.", election);

		if (election == null) {
			throw new ElectionInvalideException(ErreurElection.ELECTION_OBLIGATOIRE);
		}

		if (election.getId() == null || election.getId().equals("")) {
			throw new ElectionInvalideException(ErreurElection.ID_OBLIGATOIRE);
		}

		if (election.getProprietaire() == null) {
			throw new ElectionInvalideException(ErreurElection.PROPRIETAIRE_OBLIGATOIRE);
		}

		if (election.getTitre() == null || election.getTitre().equals("")) {
			throw new ElectionInvalideException(ErreurElection.TITRE_OBLIGATOIRE);
		}

		if (election.getDescription() == null || election.getDescription().equals("")) {
			throw new ElectionInvalideException(ErreurElection.DESCRIPTION_OBLIGATOIRE);
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
	 */
	public Election recupererElection(String id) {
		log.info("=====> Recherche de l'élection d'id {}.", id);
		Election election = null;
		if (!id.isEmpty()) {
			election = entityManager.find(Election.class, id);
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

	public List<Election> listerElection(String profil) {
		log.info("=====> Recherche des élections correspondant aux critères");
		Query requete = entityManager.createQuery(
				"SELECT e FROM Elections e");
		if (!profil.isEmpty()) {
			requete = entityManager.createQuery(
					"SELECT e FROM Elections e INNER JOIN e.profils p "
							+ "AND LOWER(p)=LOWER(:profil) ");
			requete.setParameter("profil", profil);
		}
		List<Election> list = requete.getResultList();
		return list;
	}

	public void consulterRésultats(String id) throws ElectionInvalideException {
		Election election = recupererElection(id);
		if (election.getDateCloture() == null) {
			throw new ElectionInvalideException(ErreurElection.ELECTION_NON_CLOTUREE);
		}
		List<Vote> votes = election.getVotes();

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
			throw new ElectionInvalideException(ErreurElection.NON_PROPRIETAIRE);
		}
	}

	/**
	 * permet à utilisateur de voter à une élection
	 * 
	 * @param election
	 * @param utilisateur
	 * @param vote
	 * @throws ElectionInvalideException
	 */
	public void voter(Election election, Utilisateur utilisateur, Vote vote) throws ElectionInvalideException {
		log.info("=====> Vote de {} à {}.", utilisateur, election);
		if (election.getListeVotants().contains(utilisateur)) {
			throw new ElectionInvalideException(ErreurElection.DEJA_VOTE);
		}
		if (!(election.getDateCloture() == null)) {
			throw new ElectionInvalideException(ErreurElection.ELECTION_CLOTUREE);
		}
		election.getVotes().add(vote);
		election.getListeVotants().add(utilisateur);
	}

}
