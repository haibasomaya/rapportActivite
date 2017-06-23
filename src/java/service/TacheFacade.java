/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Activite;
import bean.Employe;
import bean.GrandeTache;
import bean.Tache;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.ChartSeries;
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

    public List<String> findTaches() {
        return em.createQuery("SELECT DISTINCT (te.nom) FROM Tache te").getResultList();
    }

    public List<Tache> tacheByType(Employe employe, int annee) {
        System.out.println("hahwa employe---" + employe);
        System.out.println("hahowa annee " + annee);
        String req = "select t from Tache t where 1=1";
        if (employe != null) {
            req += " AND  t.employe.login='" + employe.getLogin() + "'";
        }
        if (annee != 0) {
            req += " AND  extract (year from t.dateTache )='" + annee + "'";
        }
        return em.createQuery(req).getResultList();
    }

    public ChartSeries tachePerMonth(Employe employe, int annee, String tache) {
        List<Tache> taches = tacheByType(employe, annee);
        ChartSeries series = new ChartSeries();

        for (int mois = 1; mois <= 12; mois++) {

            Long nbreTache = 0l;
            for (Tache gt : taches) {
                if (gt.getDateTache().getMonth() + 1 == mois && gt.getNom().equals(tache)) {
                    nbreTache = nbreTache + 1;
                }
            }
            series.set("mois " + mois, nbreTache);

        }
        series.setLabel("" + tache);
        return series;
    }

    public List<Tache> tacheByDate(Employe employe, Date dateTache) {
        if (employe == null) {
            return null;
        } else {
            String rq = "SELECT t FROM Tache t WHERE t.employe.login ='" + employe.getLogin() + "'";
            if (dateTache != null) {
                System.out.println("haaaaaa------>" + util.DateUtil.getSqlDate(dateTache));
                rq += " AND t.dateTache ='" + util.DateUtil.getSqlDate(dateTache) + "'";
            }
            System.out.println("haaaa la requette de recherche -----> " + rq);
            List<Tache> taches = em.createQuery(rq).getResultList();
            System.out.println("haa list fl'facaade------->" + taches);
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
        System.out.println("inside createTache");
        tacheElementaire.setEmploye(employe);
        tacheElementaire.setAvancement(0L);
        create(tacheElementaire);
    }

    public List<Tache> findByEmploye(Employe employe) {
        if (employe != null) {
            List<Tache> taches = em.createQuery("SELECT t FROM Tache t WHERE t.employe.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
        return null;
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
