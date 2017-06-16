/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author somaya
 */
@Entity
public class Activite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String local;
    private long avancement;
    private int degrer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateDebut;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateFin;
    @OneToOne
    private Employe gerant;
    @OneToMany(mappedBy = "activite")
    private List<GrandeTache> grandeTaches;
    @OneToMany(mappedBy = "activite")
    private List<Reunion> reunions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getAvancement() {
        return avancement;
    }

    public void setAvancement(long avancement) {
        this.avancement = avancement;
    }

    public int getDegrer() {
        return degrer;
    }

    public void setDegrer(int degrer) {
        this.degrer = degrer;
    }

    public Date getDateDebut() {
        if (dateDebut == null) {
            dateDebut = new Date();
        }
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        if (dateFin == null) {
            dateFin = new Date();
        }
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public List<GrandeTache> getGrandeTaches() {
        return grandeTaches;
    }

    public void setGrandeTaches(List<GrandeTache> grandeTaches) {
        this.grandeTaches = grandeTaches;
    }

    public Employe getGerant() {
        return gerant;
    }

    public void setGerant(Employe gerant) {
        this.gerant = gerant;
    }

    public List<Reunion> getReunions() {
        return reunions;
    }

    public void setReunions(List<Reunion> reunions) {
        this.reunions = reunions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final Activite other = (Activite) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Activite{" + "id" + id + "nom=" + nom + ", avancement=" + avancement + ", degrer=" + degrer + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + '}';
    }

}
