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

}
