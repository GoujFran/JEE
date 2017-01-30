package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException.ErreurUtilisateur;
import lombok.extern.slf4j.Slf4j;

/**
 * Sur la création et modification d'un utilisateur : - Vérification des champs
 * obligatoires - Vérification de la validité (longueur) des champs - Appeler un
 * service de notification inscrivant dans la log création ou modification de
 * l'utilisateur Sur la récupération d'un utilisateur Vérification de
 * l'existance de l'utilisateur Retourner l'utilisateur Sur la suppression d'un
 * utilisateur Vérification de l'existance de l'utilisateur Retourner
 * l'utilisateur Appeler un service de notification inscrivant dans la log la
 * suppression de l'utilisateur
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class UtilisateursServices {
	/** Services de validation d'un utilisateur. */
	@Autowired
	private ValidationUtilisateurServices validationServices;
	/** Services de notification des événements. */
	@Autowired
	private NotificationsServices notificationsServices;
	@Autowired
	private AuthentificationService authentificationService;
	@Autowired
	private VilleService villeService;
	@Autowired
	private ProfilsServices profilService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TraceService traceService;

	/**
	 * Crée un nouvel utilisateur sur le système.
	 * 
	 * @param utilisateur
	 *            Utilisateur à créer.
	 * @return Utilisateur créé.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'utilisateur est invalide.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur creer(Utilisateur utilisateur) throws UtilisateurInvalideException {
		log.info("=====> Création de l'utilisateur : {}.", utilisateur);

		if (utilisateur == null) {
			traceService.creerTraceErreur("UTILISATEUR_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE);
		}

		/** Validation de l'existance de l'utilisateur. */
		if (rechercherParLogin(utilisateur.getLogin()) != null) {
			traceService.creerTraceErreur("UTILISATEUR_EXISTANT");
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_EXISTANT);
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerUtilisateur(utilisateur);

		utilisateur = villeService.creerVilleSiBesoin(utilisateur);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de l'utilisateur: " + utilisateur.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(utilisateur);

		return utilisateur;
	}

	/**
	 * Modifie un utilisateur sur le système.
	 * 
	 * @param utilisateur
	 *            Utilisateur à modifier.
	 * @return Utilisateur modifié.
	 * @throws AuthentificationException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifier(Utilisateur utilisateur, String loginUtilisateur, String nom, String prenom,
			String email, String motDePasse, Date date, Adresse adresse, String[] profil)
					throws UtilisateurInvalideException, AuthentificationException {

		// si aucun loginUtilisateur n'est specifie, on considere que
		// l'utilisateur login modifie sa propre fiche
		Utilisateur utilisateurModifie = utilisateur;
		if (loginUtilisateur != null && !loginUtilisateur.isEmpty()) {
			utilisateurModifie = rechercherParLogin(loginUtilisateur);
		}

		authentificationService.verificationExistence(utilisateurModifie);

		if (profil != null && profil.length != 0) {
			// il faut etre admin pour changer des profils
			authentificationService.verificationAdministrateur(utilisateur.getLogin());
			profilService.attribuerProfils(utilisateurModifie, profil);
		}

		log.info("=====> Modification de l'utilisateur {}.", utilisateurModifie);
		if (nom != null && !nom.isEmpty()) {
			utilisateurModifie = modifierNom(utilisateurModifie, nom);
		}

		if (prenom != null && !prenom.isEmpty()) {
			utilisateurModifie = modifierPrenom(utilisateurModifie, prenom);
		}

		if (email != null && !email.isEmpty()) {
			utilisateurModifie = modifierEmail(utilisateurModifie, email);
		}

		if (motDePasse != null && !motDePasse.isEmpty()) {
			utilisateurModifie = modifierMDP(utilisateurModifie, motDePasse);
		}

		// La date en string doit être écrite au format JJ/MM/YYYY
		if (date != null) {
			utilisateurModifie = modifierDateNaissance(utilisateurModifie, date);
		}

		if (adresse != null) {
			utilisateurModifie = modifierAdresse(utilisateurModifie, adresse);
		}

		/** Notification de l'événement de modification */
		notificationsServices.notifier("Modification de l'utilisateur: " + utilisateurModifie.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(utilisateurModifie);

		return utilisateurModifie;
	}

	/**
	 * Retourne l'utilisateur identifié par le login.
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @return Retourne l'utilisateur identifié par le login.
	 */
	public Utilisateur rechercherParLogin(String login) {
		log.info("=====> Recherche de l'utilisateur de login {}.", login);

		if (StringUtils.isNotBlank(login)) {
			return entityManager.find(Utilisateur.class, login);
		}

		return null;
	}

	/**
	 * Retourne la liste des utilisateurs correspondant au critère de recherche
	 * chaque parametre peut etre null
	 */
	public List<Utilisateur> getListe(String nom, String prenom, String ville, String profil) {
		log.info("=====> Recherche des utilisateurs correspondant aux critères");
		// Si nom ou prenom n'est pas speficie, on le remplace par une chaine
		// vide pour que la requete fonctionne sans etre changee
		if (nom == null) {
			nom = "";
		}
		if (prenom == null) {
			prenom = "";
		}
		// requete si ni ville ni profil n'est precise :
		Query requete = entityManager.createQuery(
				"SELECT u FROM Utilisateur u "
						+ "WHERE LOWER(u.nom) LIKE CONCAT('%', LOWER(:lastName), '%') "
						+ "AND LOWER(u.prenom) LIKE CONCAT('%', LOWER(:firstName), '%')");

		// si une ville est precisee
		if (ville != null) {
			if (profil != null) {// si un profil a ete precise
				requete = entityManager.createQuery(
						"SELECT u FROM Utilisateur u INNER JOIN u.profils p INNER JOIN u.adresse.ville v "
								+ "WHERE LOWER(u.nom) LIKE CONCAT('%', LOWER(:lastName), '%') "
								+ "AND LOWER(u.prenom) LIKE CONCAT('%', LOWER(:firstName), '%') "
								+ "AND LOWER(p)=LOWER(:profil) "
								+ "AND LOWER(v.nom) = LOWER(:city)");
				requete.setParameter("profil", profil);
			} else {// si aucun profil n'a ete precise
				requete = entityManager.createQuery(
						"SELECT u FROM Utilisateur u INNER JOIN u.adresse.ville v "
								+ "WHERE LOWER(u.nom) LIKE CONCAT('%', LOWER(:lastName), '%') "
								+ "AND LOWER(u.prenom) LIKE CONCAT('%', LOWER(:firstName), '%') "
								+ "AND LOWER(v.nom) = LOWER(:city)");
			}
			requete.setParameter("city", ville);
		} else { // si aucune ville n'a ete precisee
			if (profil != null) {// si un profil a ete precise
				requete = entityManager.createQuery(
						"SELECT u FROM Utilisateur u INNER JOIN u.profils p "
								+ "WHERE LOWER(u.nom) LIKE CONCAT('%', LOWER(:lastName), '%') "
								+ "AND LOWER(u.prenom) LIKE CONCAT('%', LOWER(:firstName), '%') "
								+ "AND LOWER(p)=LOWER(:profil)");
				requete.setParameter("profil", profil);
			}
		}
		requete.setParameter("lastName", nom);
		requete.setParameter("firstName", prenom);
		List<Utilisateur> list = requete.getResultList();
		return list;
	}

	/**
	 * modifie le nom d'un utilisateur
	 * 
	 * @param nom
	 *            Nom de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierNom(Utilisateur utilisateur, String nom) {
		log.info("=====> Modification du nom de l'utilisateur {} par {}.", utilisateur, nom);
		utilisateur.setNom(nom);
		return utilisateur;
	}

	/**
	 * modifie le prénom d'un utilisateur
	 * 
	 * @param prenom
	 *            Prénom de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierPrenom(Utilisateur utilisateur, String prenom) {
		log.info("=====> Modification du prénom de l'utilisateur {} par {}.", utilisateur, prenom);
		utilisateur.setPrenom(prenom);
		return utilisateur;
	}

	/**
	 * modifie l'adresse email d'un utilisateur
	 * 
	 * @param email
	 *            Adresse email de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierEmail(Utilisateur utilisateur, String email) {
		log.info("=====> Modification de l'adresse email de l'utilisateur {} par {}.", utilisateur, email);
		utilisateur.setEmail(email);
		return utilisateur;
	}

	/**
	 * modifie le mot de passe d'un utilisateur
	 * 
	 * @param motDePasse
	 *            Mot de passe de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierMDP(Utilisateur utilisateur, String motDePasse) {
		log.info("=====> Modification du mot de passe de l'utilisateur {} par {}.", utilisateur, motDePasse);
		utilisateur.setMotDePasse(motDePasse);
		return utilisateur;
	}

	/**
	 * modifie la date de naissance d'un utilisateur
	 * 
	 * @param dateDeNaissance
	 *            Date de naissance de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierDateNaissance(Utilisateur utilisateur, Date dateDeNaissance) {
		log.info("=====> Modification de la date de naissance de l'utilisateur {} par {}.", utilisateur,
				dateDeNaissance);
		utilisateur.setDateDeNaissance(dateDeNaissance);
		return utilisateur;
	}

	/**
	 * modifie l'adresse d'un utilisateur
	 * 
	 * @param adresse
	 *            Adresse de l'utilisateur.
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifierAdresse(Utilisateur utilisateur, Adresse adresse) {
		log.info("=====> Modification de l'adresse de l'utilisateur {} par {}.", utilisateur, adresse);

		String cp = adresse.getVille().getCodePostal();
		String nom = adresse.getVille().getNom();

		if (villeService.rechercherVille(cp, nom) == null) {
			// si la ville n'existe pas en base on la cree
			Ville v = new Ville();
			v.setCodePostal(cp);
			v.setNom(nom);
			adresse.setVille(v);
			villeService.creer(v);
		} else {
			adresse.setVille(villeService.rechercherVille(cp, nom));
		}

		utilisateur.setAdresse(adresse);
		return utilisateur;
	}

	/**
	 * L'utilisateur devient administrateur
	 * 
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur toAdmin(Utilisateur utilisateur) {
		log.info("=====> Promotion au rang d'administrateur de l'utilisateur {} .", utilisateur);
		List<ProfilsUtilisateur> liste = new ArrayList<ProfilsUtilisateur>();
		liste = utilisateur.getProfils();
		liste.add(ProfilsUtilisateur.ADMINISTRATEUR);
		utilisateur.setProfils(liste);
		return utilisateur;
	}

	/**
	 * Génération d'un nouveau mot de passe pour l'utilisateur
	 * 
	 * @return Retourne l'utilisateur modifié.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur nouveauMotDePasse(Utilisateur utilisateur) {
		log.info("=====> Génération d'un nouveau mot de passe pour l'utilisateur {} .", utilisateur);
		double numero = Math.random() * 1000;
		int entier = (int) Math.round(numero);
		String nouveauMDP = String.valueOf(entier);
		utilisateur.setMotDePasse(nouveauMDP);
		String adresseMail = utilisateur.getEmail();
		log.info("=====> Envoi du nouveau mot de passe {}, à l'adresse {}.", nouveauMDP, adresseMail);
		return utilisateur;
	}

	/**
	 * lister les élections
	 * 
	 * @param titre
	 * @param clotures
	 * @param utilisateur
	 * @return
	 */
	public List<Election> listerElection(String titre, String clotures, Utilisateur utilisateur) {
		boolean cloture = false;
		if (clotures != null) {
			if (clotures.equals("oui")) {
				cloture = true;
			} else if (!clotures.equals("non")) {
				clotures = null;
			}
		}
		log.info("=====> Recherche des élections correspondant aux critères");
		Query requete = entityManager.createQuery(
				"SELECT e FROM Election e");
		if (titre != null) {
			if (utilisateur != null){
				if (clotures != null){
					if(cloture){
						requete = entityManager.createQuery(
								"SELECT e FROM Election e INNER JOIN e.proprietaire p  WHERE not (((e.dateCloture) Is Null))"
										+ "AND LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') "
										+ "AND LOWER(p)= LOWER(:utilisateur)");
						requete.setParameter("titre", titre);
						requete.setParameter("utilisateur",utilisateur);
					} else {
						requete = entityManager.createQuery(
								"SELECT e FROM Election e INNER JOIN e.proprietaire p  WHERE (((e.dateCloture) Is Null))"
										+ "AND LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') "
										+ "AND LOWER(p)= LOWER(:utilisateur)");
						requete.setParameter("titre", titre);
						requete.setParameter("utilisateur",utilisateur);
					}
				} else {
					requete = entityManager.createQuery(
							"SELECT e FROM Election e INNER JOIN e.proprietaire p "
									+ "WHERE LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') "
									+ "AND LOWER(p)= LOWER(:utilisateur)");
					requete.setParameter("titre", titre);
					requete.setParameter("utilisateur",utilisateur);
				}
			} else {
				if (clotures != null){
					if(cloture){
						requete = entityManager.createQuery(
								"SELECT e FROM Election e  WHERE not (((e.dateCloture) Is Null))"
										+ "AND LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') ");
						requete.setParameter("titre", titre);
					} else {
						requete = entityManager.createQuery(
								"SELECT e FROM Election e WHERE (((e.dateCloture) Is Null))"
										+ "AND LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') ");
						requete.setParameter("titre", titre);
					}
				} else {
					requete = entityManager.createQuery(
							"SELECT e FROM Election e "
									+ "WHERE LOWER(e.titre) LIKE CONCAT('%', LOWER(:titre), '%') ");
					requete.setParameter("titre", titre);
				}
			}
		} else {
			if (utilisateur != null){
				if (clotures != null){
					if(cloture){
						requete = entityManager.createQuery(
								"SELECT e FROM Election e INNER JOIN e.proprietaire p  WHERE not (((e.dateCloture) Is Null))"
										+ "AND LOWER(p)= LOWER(:utilisateur)");
						requete.setParameter("utilisateur",utilisateur);
					} else {
						requete = entityManager.createQuery(
								"SELECT e FROM Election e INNER JOIN e.proprietaire p  WHERE (((e.dateCloture) Is Null))"
										+ "AND LOWER(p)= LOWER(:utilisateur)");
						requete.setParameter("utilisateur",utilisateur);
					}
				} else {
					requete = entityManager.createQuery(
							"SELECT e FROM Election e INNER JOIN e.proprietaire p "
									+ "WHERE LOWER(p)= LOWER(:utilisateur)");
					requete.setParameter("utilisateur",utilisateur);
				}
			} else {
				if (clotures != null){
					if(cloture){
						requete = entityManager.createQuery(
								"SELECT e FROM Election e  WHERE not (((e.dateCloture) Is Null))");
					} else {
						requete = entityManager.createQuery(
								"SELECT e FROM Election e WHERE (((e.dateCloture) Is Null))");
					}
				}
			}
		}
		List<Election> list = requete.getResultList();
		return list;
	}

}
