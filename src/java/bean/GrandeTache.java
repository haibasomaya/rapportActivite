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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author somaya
 */
@Entity
public class GrandeTache extends Tache implements Serializable {

    private long dure;
    @ManyToOne
    private Activite activite;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateFin;

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public long getDure() {
        return dure;
    }

    public void setDure(long dure) {
        this.dure = dure;
    }

    public Activite getActivite() {
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    @Override
    public String toString() {
        return "GrandeTache{" + "dateFin=" + dateFin + ", dure=" + dure + '}';
    }

}
