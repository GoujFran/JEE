package fr.sgr.formation.voteapp.elections.modele;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "idVote" })
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Vote {

	@Id
	private String idVote;
	@ManyToOne
	private Utilisateur utilisateur;
	@ManyToOne
	private Election election;
	@Enumerated(EnumType.STRING)
	private Choix choix;
}
