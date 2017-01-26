package fr.sgr.formation.voteapp.elections.services;

import lombok.Builder;
import lombok.Getter;

public class ElectionInvalideException extends Exception {
	/** Identifie l'erreur. */
	@Getter
	private ErreurElection erreur;

	@Builder
	public ElectionInvalideException(ErreurElection erreur, Throwable cause) {
		super(cause);

		this.erreur = erreur;
	}

	public ElectionInvalideException(ErreurElection erreur) {
		this.erreur = erreur;
	}

	public enum ErreurElection {
		ELECTION_OBLIGATOIRE("L'élection est obligatoire pour effectuer l'opération."),
		ID_OBLIGATOIRE("L'id est obligatoire pour effectuer l'opération."),
		PROPRIETAIRE_OBLIGATOIRE("Le propriétaire est obligatoire pour effectuer l'opération."),
		TITRE_OBLIGATOIRE("Le titre de l'éléction est obligatoire."),
		DESCRIPTION_OBLIGATOIRE("La description de l'éléction est obligatoire."),
		NON_PROPRIETAIRE("Seul le propriétaire de l'élection peut effectuer l'opération."),
		DEJA_VOTE("Vous avez déjà voté à cette élection."),
		ELECTION_CLOTUREE("Cette élection a été coturé, vous ne pouvez plus voter."),
		ELECTION_NON_CLOTUREE("Cette élection n'est pas encore finie."),
		VOTE_NON_VALIDE("Votre vote n'est pas valide."),
		ELECTION_INEXISTANTE("L'élection n'existe pas.");

		@Getter
		public String message;

		private ErreurElection(String message) {
			this.message = message;
		}
	}
}
