/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author somaya
 */
@Entity
public class TacheElementaire extends Tache implements Serializable {

    private int type;// 1=quotidienne || 2=elemt d'une grande tache 
    @ManyToOne
    private GrandeTache grandeTache;
    @ManyToOne
    private Employe employe;

    public GrandeTache getGrandeTache() {
        return grandeTache;
    }

    public void setGrandeTache(GrandeTache grandeTache) {
        this.grandeTache = grandeTache;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final TacheElementaire other = (TacheElementaire) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TacheElementaire{" + "type=" + type + ", employe=" + employe + '}';
    }

}
