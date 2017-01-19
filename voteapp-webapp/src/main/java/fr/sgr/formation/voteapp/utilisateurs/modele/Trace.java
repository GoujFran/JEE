package fr.sgr.formation.voteapp.utilisateurs.modele;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Trace {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String loginUtilisateur;
	private String typeAction;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private String resultatAction;
	private String description;

}
