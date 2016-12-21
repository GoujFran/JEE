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
	// @Autowired
	private NotificationsServices notificationsServices;

	@Autowired
	private EntityManager entityManager;

	public Election creerElection(Election election) throws ElectionInvalideException {
		log.info("=====> Création de l'élection : {}.", election);

		if (election == null) {
			throw new ElectionInvalideException(ErreurElection.ELECTION_OBLIGATOIRE);
		}

		/**
		 * Validation de l'élection: lève une exception si l'élection est
		 * invalide.
		 */
		validationServices.validerElection(election);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de l'élection: " + election.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(election);

		return election;

	}

	public void fermerElection() {
		// TODO
	}

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
