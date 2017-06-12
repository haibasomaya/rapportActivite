/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.TacheElementaire;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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
public class TacheElementaireFacade extends AbstractFacade<TacheElementaire> {

    @EJB
    private EmployeFacade employeFacade;
    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TacheElementaireFacade() {
        super(TacheElementaire.class);
    }
//statistique

    public List<TacheElementaire> tacheByCritere(Employe employe, int annee, int type) {
        System.out.println("hahwa employe---" + employe);
        System.out.println("hahowa annee " + annee);
        System.out.println("hahowa type " + type);
        String req = "select t from TacheElementaire t where t.employe.login='" + employe.getLogin() + "' and extract (year from t.dateTache )=" + annee + "  and t.type = " + type + "";

        return em.createQuery(req).getResultList();
    }

    public void tacheByMonth(Employe employe, int annee, int type) {
        List<TacheElementaire> tacheElementaires = tacheByCritere(employe, annee, type);
        List<Long> avancement = new ArrayList();
        for (TacheElementaire tacheElementaire : tacheElementaires) {
            Long a = (Long) em.createQuery("select distinct t.avancement from TacheElementaire t WHERE t.id =" + tacheElementaire.getId()).getResultList().get(0);
            if (a != null) {
                avancement.add(a);
            }
        }
        ChartSeries tacheEffectue = new ChartSeries();
        ChartSeries tacheEnCours = new ChartSeries();
        ChartSeries tacheRien = new ChartSeries();
        BarChartModel model = new BarChartModel();
        List<Object[]> listeObjet = new ArrayList<>();
        for (int mois = 1; mois <= 12; mois++) {
            for (Long etatAv : avancement) {
                Long nbreTacheElementaire = 0l;
                for (TacheElementaire tacheElementaire : tacheElementaires) {
//                    && tacheElementaire.getAvancement() == etatAv
                    if (tacheElementaire.getDateTache().getMonth() + 1 == mois) {
                        nbreTacheElementaire = nbreTacheElementaire + 1;
                    }
                }
                listeObjet.add(new Object[]{nbreTacheElementaire, mois, etatAv});
            }

        }
        for (Object[] object : listeObjet) {
            Long etatAvancement = (Long) object[2];
            if (etatAvancement == 1) {
                tacheEffectue.set("mois " + (int) object[1], (Long) object[0]);
            }
            if (etatAvancement == 2) {
                tacheEnCours.set("mois " + (int) object[1], (Long) object[0]);
            }
            if (etatAvancement == 3) {
                tacheRien.set("mois " + (int) object[1], (Long) object[0]);
            }
            System.out.println("nbre tache " + (Long) object[0] + " etat avancement " + (int) object[2] + " mois " + (int) object[1]);
        }
        model.addSeries(tacheEffectue);
        model.addSeries(tacheEnCours);
        model.addSeries(tacheRien);
        System.out.println("haaaa l' modeel facade---->"+model);
    }
    //statistiique

    public List<TacheElementaire> findByDateEmp(String login, Date dateTache, int type) {
        Employe employe = employeFacade.find(login);
        if (employe == null) {
            return null;
        } else {
            String rq = "SELECT t FROM TacheElementaire t WHERE t.employe.login ='" + employe.getLogin() + "'";
            if (dateTache != null) {
                rq += " AND t.dateTache ='" + util.DateUtil.getSqlDate(dateTache) + "'";
            }
            if (type > 0) {
                rq += "AND t.type =" + type;
            }
            List<TacheElementaire> taches = em.createQuery(rq).getResultList();
            return taches;
        }
    }

    public List<TacheElementaire> chercherDate(Date dateCherche) {
        return em.createQuery("SELECT t from TacheElementaire t WHERE t.dateTache ='" + util.DateUtil.getSqlDate(dateCherche) + "'").getResultList();
    }

    public void createList(List<TacheElementaire> taches, Employe employe) {
        for (TacheElementaire tache : taches) {
            createTache(tache, employe);
        }
    }

    public boolean dateInvalid(Date dateChoisit) {
        Date dChoix = util.DateUtil.getSqlDate(dateChoisit);
        List<Date> listDate = em.createQuery("SELECT t.dateTache FROM TacheElementaire t WHERE t.dateTache ='" + dChoix + "'").getResultList();
        if (listDate.isEmpty()) {
            return true;
        }
        return false;
    }

    private void createTache(TacheElementaire tacheElementaire, Employe employe) {
        tacheElementaire.setEmploye(employe);
        employe.getTacheElementaires().add(tacheElementaire);
        tacheElementaire.setAvancement(0L);
        tacheElementaire.setPourcentage(0L);
        tacheElementaire.setGrandeTache(null);
        tacheElementaire.setType(1);
        create(tacheElementaire);
    }

    public List<TacheElementaire> findByEmploye(Employe employe) {
        if (employe != null) {
            return em.createQuery("SELECT t FROM TacheElementaire t WHERE t.employe.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

}
