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

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
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
	private EntityManager entityManager;

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
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE);
		}

		/** Validation de l'existance de l'utilisateur. */
		if (rechercherParLogin(utilisateur.getLogin()) != null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_EXISTANT);
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerUtilisateur(utilisateur);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de l'utilisateur: " + utilisateur.toString());

		/** Persistance de l'utilisateur. */
		entityManager.persist(utilisateur);

		return utilisateur;
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
	public Utilisateur modifierDateNaissance(Utilisateur utilisateur, String dateDeNaissance) {
		log.info("=====> Modification de la date de naissance de l'utilisateur {} par {}.", utilisateur,
				dateDeNaissance);
		Date date = new Date();
		String[] myTable = dateDeNaissance.split("/");
		date.setDate(Integer.parseInt(myTable[0]));
		date.setMonth(Integer.parseInt(myTable[1]));
		date.setYear(Integer.parseInt(myTable[2]));

		utilisateur.setDateDeNaissance(date);
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
		log.info("=====> Envoie du nouveau mot de passe {}, à l'adresse {}.", nouveauMDP, adresseMail);
		return utilisateur;
	}

}
