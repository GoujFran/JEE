package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Trace;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class TraceService {

	/** Services de validation d'un utilisateur. */
	@Autowired
	private ValidationUtilisateurServices validationServices;
	/** Services de notification des événements. */
	@Autowired
	private NotificationsServices notificationsServices;

	@Autowired
	private EntityManager entityManager;

	/**
	 * Crée une nouvelle trace sur le système.
	 * 
	 * @param trace
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creer(Trace trace, String login) {
		trace.setLoginUtilisateur(login);
		trace.setDate(new Date());
		log.info("=====> Création de la trace : {}.", trace);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de la trace: " + trace.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(trace);

		return trace;
	}

	/**
	 * Crée une trace de consultation d'un utilisateur.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceConsultationUtilisateur(String login) {
		log.info("=====> Création d'une trace de consultation.");

		Trace myTrace = new Trace();
		myTrace.setDescription("description");
		myTrace.setResultatAction("Consultation OK");
		myTrace.setTypeAction("Consultation d'un utilisateur");
		myTrace = this.creer(myTrace, login);
		return myTrace;
	}

	/**
	 * Crée une trace de création d'un utilisateur.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceCreationUtilisateur(String login, boolean succes) {
		log.info("=====> Création d'une trace de création.");

		Trace myTrace = new Trace();
		myTrace.setDescription("description");
		if (succes) {
			myTrace.setResultatAction("Création OK");
		} else {
			myTrace.setResultatAction("Création en erreur");
		}
		myTrace.setTypeAction("Création d'un utilisateur");
		myTrace = this.creer(myTrace, login);
		return myTrace;
	}

	/**
	 * Crée une trace de renouvellement de mot de passe d'un utilisateur.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceRenouvellementMDPUtilisateur(String login) {
		log.info("=====> Création d'une trace de renouvellement de mot de passe.");

		Trace myTrace = new Trace();
		myTrace.setDescription("description");
		myTrace.setResultatAction("Renouvellement OK");
		myTrace.setTypeAction("Renouvellement d'un mot de passe");
		myTrace = this.creer(myTrace, login);
		return myTrace;
	}

	/**
	 * Crée une trace de modification d'un utilisateur.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceModificationUtilisateur(String login) {
		log.info("=====> Création d'une trace de modification d'un utilisateur.");

		Trace myTrace = new Trace();
		myTrace.setDescription("description");
		myTrace.setResultatAction("Modification OK");
		myTrace.setTypeAction("Modification d'un utilisateur");
		myTrace = this.creer(myTrace, login);
		return myTrace;
	}

	/**
	 * Retourne la trace identifiée par l'id.
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @return Retourne l'utilisateur identifié par le login.
	 */
	public Trace rechercherId(int id) {
		log.info("=====> Recherche de l'utilisateur de login {}.", id);
		return entityManager.find(Trace.class, id);
	}

}
