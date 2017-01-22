package fr.sgr.formation.voteapp.utilisateurs.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException.ErreurUtilisateur;

/**
 * Bean mettant à disposition les services permettant de valider les
 * informations d'un utilisateur.
 */
@Service
public class ValidationUtilisateurServices {
	@Autowired
	private TraceService traceService;

	/**
	 * Vérifie qu'un utilisateur est valide.
	 * 
	 * @param utilisateur
	 *            Utilisateur à valider.
	 * @return true si l'utilisateur est valide, false si aucun utilisateur
	 *         n'est passé en paramètre.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'utilisateur est invalide.
	 */
	public boolean validerUtilisateur(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (utilisateur == null) {
			return false;
		}

		validerLogin(utilisateur);
		validerNom(utilisateur);
		validerPrenom(utilisateur);
		validerMotDePasse(utilisateur);
		validerEmail(utilisateur);
		validerProfils(utilisateur);

		/** Validation des champs. */
		return true;
	}

	private void validerNom(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (StringUtils.isBlank(utilisateur.getNom())) {
			traceService.creerTraceErreur("NOM_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.NOM_OBLIGATOIRE);
		}
	}

	private void validerPrenom(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (StringUtils.isBlank(utilisateur.getPrenom())) {
			traceService.creerTraceErreur("PRENOM_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.PRENOM_OBLIGATOIRE);
		}
	}

	private void validerLogin(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (StringUtils.isBlank(utilisateur.getLogin())) {
			traceService.creerTraceErreur("LOGIN_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.LOGIN_OBLIGATOIRE);
		}
	}

	private void validerMotDePasse(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (StringUtils.isBlank(utilisateur.getMotDePasse())) {
			traceService.creerTraceErreur("MDP_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.MDP_OBLIGATOIRE);
		}
	}

	private void validerEmail(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (StringUtils.isBlank(utilisateur.getEmail())) {
			traceService.creerTraceErreur("EMAIL_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.EMAIL_OBLIGATOIRE);
		}
	}

	private void validerProfils(Utilisateur utilisateur) throws UtilisateurInvalideException {
		if (utilisateur.getProfils().isEmpty()) {
			traceService.creerTraceErreur("PROFIL_OBLIGATOIRE");
			throw new UtilisateurInvalideException(ErreurUtilisateur.PROFIL_OBLIGATOIRE);
		}
	}
}
