/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author somaya
 */
@Entity
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String nom;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date dateTache;
    protected Long pourcentage;
    protected Long avancement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateTache() {
        if (dateTache == null) {
            dateTache = new Date();
        }
        return dateTache;
    }

    public void setDateTache(Date dateTache) {
        this.dateTache = dateTache;
    }

    public Long getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(Long pourcentage) {
        this.pourcentage = pourcentage;
    }

    public Long getAvancement() {
        return avancement;
    }

    public void setAvancement(Long avancement) {
        this.avancement = avancement;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.nom);
        hash = 37 * hash + Objects.hashCode(this.dateTache);
        hash = 37 * hash + Objects.hashCode(this.pourcentage);
        hash = 37 * hash + Objects.hashCode(this.avancement);
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
        final Tache other = (Tache) obj;
        if (!Objects.equals(this.nom, other.nom)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.dateTache, other.dateTache)) {
            return false;
        }
        if (!Objects.equals(this.pourcentage, other.pourcentage)) {
            return false;
        }
        if (!Objects.equals(this.avancement, other.avancement)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tache{" + "nom=" + nom + ", DateTach=" + dateTache + ", pourcentage=" + pourcentage + ", avancement=" + avancement + '}';
    }

}
