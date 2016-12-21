package fr.sgr.formation.voteapp.elections.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.elections.modele.Election;
import fr.sgr.formation.voteapp.elections.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;

@Service
public class ValidationElectionServices {
	/**
	 * Vérifie qu'une élection est valide.
	 * 
	 * @param election
	 *            Election à valider.
	 * @return true si l'election est valide, false si aucune élection n'est
	 *         passé en paramètre.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'élection est invalide.
	 */
	public boolean validerElection(Election election) throws ElectionInvalideException {
		if (election == null) {
			return false;
		}

		validerPropriétaire(election);
		validerTitre(election);
		validerDescription(election);

		/** Validation des champs. */
		return true;
	}

	private void validerPropriétaire(Election election) throws ElectionInvalideException {
		if (election.getProprietaire() == null) {
			throw new ElectionInvalideException(ErreurElection.PROPRIETAIRE_OBLIGATOIRE);
		}
	}

	private void validerTitre(Election election) throws ElectionInvalideException {
		if (StringUtils.isBlank(election.getTitre())) {
			throw new ElectionInvalideException(ErreurElection.TITRE_OBLIGATOIRE);
		}
	}

	private void validerDescription(Election election) throws ElectionInvalideException {
		if (StringUtils.isBlank(election.getDescription())) {
			throw new ElectionInvalideException(ErreurElection.DESCRIPTION_OBLIGATOIRE);
		}
	}

}
