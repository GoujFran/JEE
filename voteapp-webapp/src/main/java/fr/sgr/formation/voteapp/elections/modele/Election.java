package fr.sgr.formation.voteapp.elections.modele;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Election {
	@Id
	private String id;
	@ManyToOne
	private Utilisateur proprietaire;
	private String titre;
	private String description;
	@Singular
	@ElementCollection(targetClass = String.class)
	private List<String> images = new LinkedList<String>();
	private Date dateCloture;
	@Singular
	@ElementCollection(targetClass = Vote.class)
	private List<Vote> votes = new LinkedList<Vote>();
	@Singular
	@ElementCollection(targetClass = Utilisateur.class)
	@OneToMany
	private List<Utilisateur> listeVotants = new LinkedList<Utilisateur>();

	public Election(String id, Utilisateur propriétaire, String titre, String description) {
		this.id = id;
		this.proprietaire = propriétaire;
		this.titre = titre;
		this.description = description;
	}

	public Election(String id, Utilisateur propriétaire, String titre, String description, List<String> images) {
		this.id = id;
		this.proprietaire = propriétaire;
		this.titre = titre;
		this.description = description;
		this.images = images;
	}
}
