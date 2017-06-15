/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author somaya
 */
@Entity
public class Employe implements Serializable {

    @Id
    private String login;
    private String fonction;
    private String nom;
    private String prenom;
    private String cin;
    private String tel;
    private String password;
    private String email;
    private int numPost;
    private int numBureau;
    private int bloquer;
    private int essay;
    private boolean admin;
    private int superAdmin;
    @ManyToOne
    private Service service;
    @ManyToOne
    private GrandeTache grandeTache;
    @OneToMany(mappedBy = "employe")
    private List<TacheElementaire> tacheElementaires;
    @OneToMany(mappedBy = "employe")
    private List<Device> devices;

    public boolean isAdmin() {
        return admin;
    }

    public int getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(int superAdmin) {
        this.superAdmin = superAdmin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public int getEssay() {
        return essay;
    }

    public void setEssay(int essay) {
        this.essay = essay;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getBloquer() {
        return bloquer;
    }

    public void setBloquer(int bloquer) {
        this.bloquer = bloquer;
    }

    public GrandeTache getGrandeTache() {
        return grandeTache;
    }

    public void setGrandeTache(GrandeTache grandeTache) {
        this.grandeTache = grandeTache;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumPost() {
        return numPost;
    }

    public void setNumPost(int numPost) {
        this.numPost = numPost;
    }

    public int getNumBureau() {
        return numBureau;
    }

    public void setNumBureau(int numBureau) {
        this.numBureau = numBureau;
    }

    public List<TacheElementaire> getTacheElementaires() {
        if (tacheElementaires == null) {
            tacheElementaires = new ArrayList<>();
        }
        return tacheElementaires;
    }

    public void setTacheElementaires(List<TacheElementaire> tacheElementaires) {
        this.tacheElementaires = tacheElementaires;
    }

    public Service getService() {
        if (service == null) {
            service = new Service();
        }
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.login);
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
        final Employe other = (Employe) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Employe{" + "login=" + login + ", nom=" + nom + ", prenom=" + prenom + "fonction=" + fonction + ", cin=" + cin + ", tel=" + tel + ", password=" + password + ", email=" + email + ", numPost=" + numPost + ", numBureau=" + numBureau + '}';
    }

}
