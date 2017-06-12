/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author somaya
 */
@Entity
public class Marche extends Activite implements Serializable {

    private List<String> fournisseurs;
    @OneToMany
    private List<Division> divisions;

    public List<String> getFournisseurs() {
        return fournisseurs;
    }

    public void setFournisseurs(List<String> fournisseurs) {
        this.fournisseurs = fournisseurs;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    @Override
    public String toString() {
        return "Marche{" + "fournisseurs=" + fournisseurs + '}';
    }

}
