/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author somaya
 */
@Entity
public class GrandeTache extends Tache implements Serializable {

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateFin;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dure;
    @OneToMany(mappedBy = "grandeTache")
    private List<TacheElementaire> tacheElementaires;
    @OneToMany(mappedBy = "grandeTache")
    private List<Employe> employes;

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Date getDure() {
        return dure;
    }

    public void setDure(Date dure) {
        this.dure = dure;
    }

    public List<TacheElementaire> getTacheElementaires() {
        return tacheElementaires;
    }

    public void setTacheElementaires(List<TacheElementaire> tacheElementaires) {
        this.tacheElementaires = tacheElementaires;
    }

    public List<Employe> getEmployes() {
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    @Override
    public String toString() {
        return "GrandeTache{" + "dateFin=" + dateFin + ", dure=" + dure + '}';
    }

   
}
