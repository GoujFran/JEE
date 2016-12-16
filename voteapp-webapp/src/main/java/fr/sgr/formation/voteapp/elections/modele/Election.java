package fr.sgr.formation.voteapp.elections.modele;

import java.util.Date;
import java.util.List;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;

public class Election {
	private Utilisateur proprietaire;
	private String titre;
	private String description;
	private List<String> images;
	private Date dateCloture;
	private List<Vote> votes;
}
