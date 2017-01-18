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
		DESCRIPTION_OBLIGATOIRE("La description de l'éléction est obligatoire."),;

		@Getter
		public String message;

		private ErreurElection(String message) {
			this.message = message;
		}
	}
}
