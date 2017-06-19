/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Activite;
import bean.Employe;
import bean.GrandeTache;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author dell
 */
@Stateless
public class GrandeTacheFacade extends AbstractFacade<GrandeTache> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrandeTacheFacade() {
        super(GrandeTache.class);
    }
    private List<String> names = new ArrayList();

    public List<GrandeTache> findGrandeTachByEmp(Employe employe) {
        return em.createQuery("SELECT t FROM GrandeTache t WHERE t.employe.login ='" + employe.getLogin() + "'").getResultList();
    }

    public List<GrandeTache> findGrandeTachByEmpByact(Employe employe, Activite activitie) {
        if (activitie != null && employe != null) {
            return em.createQuery("SELECT t FROM GrandeTache t WHERE t.employe.login ='" + employe.getLogin() + "'" + " and t.activite.id =" + activitie.getId()).getResultList();
        }
        return null;
    }

    public List<GrandeTache> GrandeByDate(Employe employe, Date dateTache) {
        if (employe == null) {
            return null;
        } else {
            String rq = "SELECT t FROM GrandeTache t WHERE t.employe.login ='" + employe.getLogin() + "'";
            if (dateTache != null) {
                rq += " AND t.dateTache ='" + util.DateUtil.getSqlDate(dateTache) + "'";
            }
            System.out.println("haaaa la requette de recherche -----> " + rq);
            List<GrandeTache> taches = em.createQuery(rq).getResultList();
            return taches;
        }
    }
//statistique

    public List<GrandeTache> tacheByCritere1(Employe employe, int annee, String nom) {
        System.out.println("hahwa employe---" + employe);
        System.out.println("hahowa annee " + annee);
        System.out.println("hahowa nom " + nom);
        String req = "select t from GrandeTache t where 1=1";
        if (employe != null) {
            req += " AND t.employe.login='" + employe.getLogin() + "'";
        }
        if (annee != 0) {
            req += " AND  extract (year from t.dateTache )='" + annee + "'";
        }
        if (nom != null) {
            req += " AND t.nom = '" + nom + "'";
        }
        System.out.println("req---->" + req);
        return em.createQuery(req).getResultList();
    }

    public BarChartModel tacheByMonth(Employe employe, int annee, String nom) {
        List<GrandeTache> grandeTaches = tacheByCritere1(employe, annee, nom);
        System.out.println("grandeTaches--->" + grandeTaches);
        List<Long> avancement = new ArrayList<>();
        avancement = em.createQuery("select distinct t.avancement from GrandeTache t").getResultList();
        System.out.println("avancement " + avancement);
        ChartSeries tacheEffectue = new ChartSeries();
        ChartSeries tacheEnCours = new ChartSeries();
        ChartSeries tacheRien = new ChartSeries();
        BarChartModel model = new BarChartModel();
        List<Object[]> listeObjet = new ArrayList<>();
        for (int mois = 1; mois <= 12; mois++) {
            for (Long etatAv : avancement) {
                Long nbreGrandeTache = 0l;
                for (GrandeTache gt : grandeTaches) {
                    if (gt.getDateTache().getMonth() + 1 == mois && gt.getAvancement().equals(etatAv)) {
                        nbreGrandeTache = nbreGrandeTache + 1;
                    }
                }

                listeObjet.add(new Object[]{nbreGrandeTache, mois, etatAv});
            }
            for (Object[] object : listeObjet) {
                Long etatAvancement = (Long) object[2];
                if (etatAvancement == 100) {
                    tacheEffectue.set("mois " + (int) object[1], (Long) object[0]);
                }
                if (etatAvancement > 0 && etatAvancement < 100) {
                    tacheEnCours.set("mois " + (int) object[1], (Long) object[0]);
                }
                if (etatAvancement == 0) {
                    tacheRien.set("mois " + (int) object[1], (Long) object[0]);
                }
                System.out.println("nbre tache " + (Long) object[0] + " etat avancement " + (Long) object[2] + " mois " + (int) object[1]);

            }
        }
        tacheEffectue.setLabel("tache effectue");
        tacheEnCours.setLabel("tache en cours");
        tacheRien.setLabel("rien");
        model.addSeries(tacheEffectue);
        model.addSeries(tacheEnCours);
        model.addSeries(tacheRien);
//            System.out.println("haaaa l' modeel facade---->" + model);
        return model;
    }
}
