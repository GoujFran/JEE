package fr.sgr.formation.voteapp.fonctionnementInterne;

import java.util.List;

import lombok.Data;

/**
 * Classe pour avoir un type de retour permettant la pagination
 */
@Data
public class RetourPagine {
	// éléments de pagination a retourner dans la reponse :
	int nbTotalItems;
	int nbItems; // nb demande = nb retourne
	int numeroPage; // page demandee = page retournee
	int nbPages; // compteur a partir de 1

	// liste des elements de reponse
	List<?> liste;

	public RetourPagine(List<?> liste, Integer nbItems, Integer numeroPage) {
		this.liste = liste;

		// si l'utilisateur ne precise pas le nombre d'items par page ou met une
		// valeur inferieure a 1, on choisit d'en afficher autant qu'il y en a
		if (nbItems == null || nbItems < 1) {
			this.nbItems = liste.size();
		} else {
			this.nbItems = nbItems.intValue();
		}

		nbTotalItems = liste.size();

		if (this.nbItems != 0) {
			if (nbTotalItems % this.nbItems == 0) {
				nbPages = nbTotalItems / this.nbItems;
			} else {
				nbPages = nbTotalItems / this.nbItems + 1;
			}

		} else {
			// si l'utilisateur n'a pas choisi nbItems et que la liste est vide
			nbPages = 0;
		}

		// si l'utilisateur ne demande pas de numero de page ou met une valeur
		// inferieure a 1 ou trop grande, on le met automatiquement a la
		// premiere page
		if (numeroPage == null || numeroPage.intValue() < 1 || numeroPage > nbPages) {
			this.numeroPage = 1;
		} else {
			this.numeroPage = numeroPage;
		}

	}

}
