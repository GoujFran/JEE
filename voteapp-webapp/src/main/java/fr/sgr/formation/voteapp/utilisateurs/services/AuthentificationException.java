package fr.sgr.formation.voteapp.utilisateurs.services;

import lombok.Builder;
import lombok.Getter;

/**
 * Exception levée pour indiquer qu'un utilisateur n'a pas le statut requis pour
 * effectuer une action
 */
public class AuthentificationException extends Exception {
	/** Identifie l'erreur. */
	@Getter
	private ErreurAuthentification erreur;

	@Builder
	public AuthentificationException(ErreurAuthentification erreur, Throwable cause) {
		super(cause);

		this.erreur = erreur;
	}

	public AuthentificationException(ErreurAuthentification erreur) {
		this.erreur = erreur;
	}

	public enum ErreurAuthentification {
		ADMINISTRATEUR_OBLIGATOIRE("Vous devez être administrateur pour effectuer l'opération."),
		GERANT_OBLIGATOIRE("Vous devez être gérant pour effectuer l'opération."),
		UTILISATEUR_OBLIGATOIRE("Vous devez être utilisateur pour effectuer l'opération."),
		UTILISATEUR_INEXISTANT("Vous recherchez un utilisateur qui n'existe pas dans notre système.");

		@Getter
		public String message;

		private ErreurAuthentification(String message) {
			this.message = message;
		}
	}
}
