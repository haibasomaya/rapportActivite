/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dell
 */
@Stateless
public class EmployeFacade extends AbstractFacade<Employe> {

    @EJB
    private DeviceFacade deviceFacade;

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmployeFacade() {
        super(Employe.class);
    }

    public int connexion(String login, String password) {
        if (login.equals("") || password.equals("")) {
            return -1;
        }
        Employe employe = find(login);
        if (employe == null) {
            return -2;
        }
        if (!employe.getPassword().equals(password)) {
            employe.setEssay(employe.getEssay() + 1);
            edit(employe);
            System.out.println("Essay num" + employe.getEssay());
        }
        if (employe.getEssay() == 3) {
            employe.setBloquer(1);
            edit(employe);
            return -3;
        }
        if (employe.getPassword().equals(password) && employe.getBloquer() == 0) {
            employe.setEssay(0);
            edit(employe);
            deviceFacade.creerDevice(employe);
            util.SessionUtil.registerUser(employe);
            return 1;
        } else {
            return 0;
        }
    }

    public void creerEmp(Employe employe, String pasword) {
        employe.setPassword(util.HashageUtil.sha256(pasword));
        create(employe);
    }

    public int bloquerDebloquer(Employe employe) {
        if (employe.getBloquer() == 0) {
            employe.setBloquer(1);
            edit(employe);
            return 1;
        } else if (employe.getBloquer() == 1) {
            employe.setBloquer(0);
            edit(employe);
            return 0;
        } else {
            return -1;
        }
    }

    public List<Employe> liseBloquer(int etat) {
        return em.createQuery("SELECT emp from Employe emp WHERE emp.bloquer = " + etat).getResultList();
    }

    public List<Employe> recherche(String nom, String prenom, String login) {
        List<Employe> emp = new ArrayList();
        String requette = "SELECT emp from Employe emp WHERE 1=1";
        if (!login.equals("")) {
            emp.add(find(login));
        } else {
            if (!nom.equals("")) {
                requette += " and emp.nom ='" + nom + "'";
            }
            if (!prenom.equals("")) {
                requette += " and emp.prenom ='" + prenom + "'";
            }
            emp = em.createQuery(requette).getResultList();
        }
        return emp;
    }
}
//        !util.HashageUtil.sha256(password).equals(employe.getPassword())
//            employe.equals(util.HashageUtil.sha256(password))
