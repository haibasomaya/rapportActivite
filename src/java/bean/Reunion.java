/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author somaya
 */
@Entity
public class Reunion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    List<Employe> participants;
    private String discreption;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateDebut;
    @OneToOne
    private Employe gerant;
    @ManyToOne
    private Activite activite;

    public Long getId() {
        return id;
    }

    public Activite getActivite() {
        if (activite == null) {
            activite = new Activite();
        }
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscreption() {
        return discreption;
    }

    public void setDiscreption(String discreption) {
        this.discreption = discreption;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public List<Employe> getParticipants() {
        if(participants == null){
            participants = new ArrayList();
        }
        return participants;
    }

    public void setParticipants(List<Employe> participants) {
        this.participants = participants;
    }

    public Employe getGerant() {
        return gerant;
    }

    public void setGerant(Employe gerant) {
        this.gerant = gerant;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reunion other = (Reunion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reunion{" + "nom=" + discreption + ", dateDebut=" + dateDebut + '}';
    }

}
