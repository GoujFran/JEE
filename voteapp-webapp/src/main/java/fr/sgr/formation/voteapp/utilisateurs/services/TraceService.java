package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Trace;
import fr.sgr.formation.voteapp.utilisateurs.ws.UtilisateursRest;
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
	private UtilisateursServices utilisateurServices;

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
	public Trace creer(Trace trace) {
		trace.setDate(new Date());
		Trace myTrace = new Trace(trace);
		log.info("=====> Création de la trace : {}.", myTrace);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de la trace: " + myTrace.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(myTrace);

		return myTrace;
	}

	/**
	 * Crée une trace OK.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceOK() {
		Trace myTrace = new Trace();
		UtilisateursRest.traceStatic.setResultatAction("OK");
		myTrace = this.creer(UtilisateursRest.traceStatic);
		return myTrace;
	}

	/**
	 * Crée une trace erreur.
	 * 
	 * @param login
	 * @return trace créé.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Trace creerTraceErreur(String description) {
		Trace myTrace = new Trace();
		UtilisateursRest.traceStatic.setResultatAction("En erreur");
		UtilisateursRest.traceStatic.setDescription(description);
		myTrace = this.creer(UtilisateursRest.traceStatic);
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

	/**
	 * Retourne la liste des traces correspondantes aux critères de recherche
	 * chaque parametre peut etre null
	 */
	public List<Trace> getListe(String loginUtilisateur, String nomUtilisateur, String typeAction, Date dateDebut,
			Date dateFin) {
		log.info("=====> Recherche des traces correspondant aux critères");

		// Si login, nom ou typeAction n'est pas speficie, on le remplace par
		// une chaine vide pour que la requete fonctionne sans etre changee
		if (nomUtilisateur == null) {
			nomUtilisateur = "";
		}

		// definition de la requete sous forme de string
		String req = "SELECT t FROM Trace t INNER JOIN t.utilisateur u WHERE LOWER(u.nom) LIKE CONCAT('%', LOWER(:nom), '%') ";
		if (loginUtilisateur != null) {
			req += "AND u.login = :login ";
		}
		if (typeAction != null) {
			req += "AND LOWER(t.typeAction)=LOWER(:typeAction) ";
		}
		if (dateDebut != null) {
			req += "AND t.date >= :dateDebut ";
		}
		if (dateFin != null) {
			req += "AND t.date <= :dateFin ";
		}

		Query requete = entityManager.createQuery(req);

		// definition des parametres
		requete.setParameter("nom", nomUtilisateur);
		if (loginUtilisateur != null) {
			requete.setParameter("login", loginUtilisateur);
		}
		if (typeAction != null) {
			requete.setParameter("typeAction", typeAction);
		}
		if (dateDebut != null) {
			requete.setParameter("dateDebut", dateDebut);
		}
		if (dateFin != null) {
			requete.setParameter("dateFin", dateFin);
		}

		List<Trace> list = requete.getResultList();
		return list;
	}

}
