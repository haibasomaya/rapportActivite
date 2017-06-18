/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Tache;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private EmployeFacade employeFacade;
    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TacheFacade() {
        super(Tache.class);
    }

    public List<Tache> tacheByDate(Employe employe, Date dateTache) {
        if (employe == null) {
            return null;
        } else {
            String rq = "SELECT t FROM Tache t WHERE t.employe.login ='" + employe.getLogin() + "'";
            if (dateTache != null) {
                rq += " AND t.dateTache ='" + util.DateUtil.getSqlDate(dateTache) + "'";
            }
            System.out.println("haaaa la requette de recherche -----> " + rq);
            List<Tache> taches = em.createQuery(rq).getResultList();
            return taches;
        }
    }

    public List<Tache> chercherDate(Date dateCherche) {
        return em.createQuery("SELECT t from Tache t WHERE t.dateTache ='" + util.DateUtil.getSqlDate(dateCherche) + "'").getResultList();
    }

    public void createList(List<Tache> taches, Employe employe) {
        for (Tache tache : taches) {
            createTache(tache, employe);
        }
    }

    public boolean dateInvalid(Date dateChoisit) {
        Date dChoix = util.DateUtil.getSqlDate(dateChoisit);
        List<Date> listDate = em.createQuery("SELECT t.dateTache FROM Tache t WHERE t.dateTache ='" + dChoix + "'").getResultList();
        if (listDate.isEmpty()) {
            return true;
        }
        return false;
    }

    private void createTache(Tache tacheElementaire, Employe employe) {
        tacheElementaire.setEmploye(employe);
        employe.getTacheElementaires().add(tacheElementaire);
        tacheElementaire.setAvancement(0L);
        create(tacheElementaire);
    }

    public List<Tache> findByEmploye(Employe employe) {
        if (employe != null) {
            return em.createQuery("SELECT t FROM Tache t WHERE t.employe.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

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

}
