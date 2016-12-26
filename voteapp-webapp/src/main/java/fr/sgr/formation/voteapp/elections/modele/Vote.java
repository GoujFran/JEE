package fr.sgr.formation.voteapp.elections.modele;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;

@Entity
public class Vote {

	@Id
	private String idVote;
	@ManyToOne
	private Utilisateur utilisateur;
	@Enumerated(EnumType.STRING)
	private Choix choix;
}
