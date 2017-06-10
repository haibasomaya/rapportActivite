/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Tache;
import bean.TacheElementaire;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.SearchUtil;

/**
 *
 * @author dell
 */
@Stateless
public class TacheFacade extends AbstractFacade<Tache> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TacheFacade() {
        super(Tache.class);
    }

//    public List<Tache> findByEmploye(Employe employe) {
//        if (employe != null) {
//            List<Tache> Taches = new ArrayList();
//            List<TacheElementaire> tachselem = em.createQuery("SELECT te FROM TacheElementaire te WHERE te.employe.login = '" + employe.getLogin() + "'").getResultList();
//            List<GrandeTache> grandeTaches = em.createQuery("SELECT gt FROM GrandeTache gt  WHERE gt.")
//        }
//    }

    public List<Tache> search(String nom, Date dateMin, Date dateMax) {
        String requette = "SELECT t FROM Tache t WHERE 1=1 ";

        if (dateMin != null || dateMax != null) {
            requette += SearchUtil.addConstraintMinMaxDate("t", "dateTache", dateMin, dateMax);
        }

        if (nom != null) {
            requette += SearchUtil.addConstraint("t", "nom", "=", nom);
        }

        return em.createQuery(requette).getResultList();
    }
    //public List<Tache> TacheByEmploye(Employe emp){
    //  if(emp==null) return null;
    // return  search(emp,null,null);

    //}
}
