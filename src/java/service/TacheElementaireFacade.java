/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.TacheElementaire;
import java.util.Date;
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
