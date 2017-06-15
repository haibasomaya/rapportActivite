/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author somaya
 */
@Entity
public class Projet extends Activite implements Serializable {

    private String discription;

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    
    @Override
    public String toString() {
        return "Projet{" + "activite" + discription + '}';
    }

}
