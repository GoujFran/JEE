package fr.sgr.formation.voteapp.elections.modele;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	@ElementCollection
	@CollectionTable(name = "images", joinColumns = @JoinColumn(name = "id") )
	private List<String> images;
	private Date dateCloture;
	@Singular
	@ElementCollection
	@CollectionTable(name = "votes", joinColumns = @JoinColumn(name = "id") )
	private List<Vote> votes;

	public Election(String id, Utilisateur propriétaire, String titre, String description) {
		this.id = id;
		this.proprietaire = propriétaire;
		this.titre = titre;
		this.description = description;
		this.dateCloture = new Date();
	}

	public Election(String id, Utilisateur propriétaire, String titre, String description, List<String> images) {
		this.id = id;
		this.proprietaire = propriétaire;
		this.titre = titre;
		this.description = description;
		this.images = images;
	}
}
