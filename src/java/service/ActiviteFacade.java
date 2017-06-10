/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Activite;
import bean.Employe;
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
public class ActiviteFacade extends AbstractFacade<Activite> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActiviteFacade() {
        super(Activite.class);
    }

    public List<Activite> activiteEnCours() {
        return em.createQuery("SELECT ect FROM Activite act WHERE act.avancement <= 0").getResultList();
    }

    public List<Activite> search(String nom, Employe employe, Long avancement, Date Debut, Date Fin) {
        String requette = "SELECT act FROM Activite act WHERE 1=1";
        if (nom != null) {
            requette += SearchUtil.addConstraint("act", "nom", "=", nom);
        }
        if (employe != null) {
            requette += SearchUtil.addConstraint("act", "employe", "=", employe);
        }

        if (avancement != null) {
            requette += " and avancement = " + avancement;
        }

        if (Debut != null) {
            requette += SearchUtil.addConstraintDate("act", "dateDebut", ">=", Debut);
        }
        if (Fin != null) {
            requette += SearchUtil.addConstraintDate("act", "dateFin", "<=", Fin);
        }
        return em.createQuery(requette).getResultList();
    }
}
