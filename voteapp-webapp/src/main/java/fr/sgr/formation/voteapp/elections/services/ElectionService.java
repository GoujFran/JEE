package fr.sgr.formation.voteapp.elections.services;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void fermerElection() {
		// TODO

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void modifierElection() {
		// TODO
	}

	public void listerElection() {
		// TODO
	}

	public void consulterRésultats() {
		// TODO
	}
}
