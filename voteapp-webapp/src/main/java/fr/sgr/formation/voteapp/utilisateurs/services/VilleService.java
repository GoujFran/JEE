package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VilleService {
	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public void creer(Ville ville) {
		entityManager.persist(ville);
	}

	/**
	 * Retourne la ville identifiée par son id.
	 * 
	 * @param id
	 * @return Retourne la ville identifiee par l'id
	 */
	public Ville rechercherVille(String codePostal, String nom) {
		log.info("=====> Recherche de la ville {}.", nom);

		if (StringUtils.isNotBlank(codePostal) && StringUtils.isNotBlank(nom)) {
			Query requete = entityManager.createQuery(
					"SELECT v FROM Ville v WHERE v.codePostal LIKE :cp "
							+ "AND LOWER(v.nom) LIKE LOWER(:nom)");
			requete.setParameter("cp", codePostal);
			requete.setParameter("nom", nom);
			List<Ville> liste = requete.getResultList();
			if (liste.size() != 0) {
				return liste.get(0);
			}
		}

		return null;
	}

	public Utilisateur creerVilleSiBesoin(Utilisateur utilisateur) {

		if (utilisateur.getAdresse() != null) {// si une adresse est precisee
			Adresse adresse = utilisateur.getAdresse();
			String cp = utilisateur.getAdresse().getVille().getCodePostal();
			String nom = utilisateur.getAdresse().getVille().getNom();

			if (rechercherVille(cp, nom) == null) {
				// si la ville n'existe pas en base on la cree
				Ville v = new Ville();
				v.setCodePostal(cp);
				v.setNom(nom);
				adresse.setVille(v);
				utilisateur.setAdresse(adresse);
				creer(v);
			} else {
				adresse.setVille(rechercherVille(cp, nom));
				utilisateur.setAdresse(adresse);
			}
		}

		return utilisateur;
	}

}
