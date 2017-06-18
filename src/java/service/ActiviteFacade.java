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
import java.util.List;
import java.util.function.Consumer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author somaya
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

    public List<Activite> findByGerant(Employe employe) {
        if (employe != null && (employe.isAdmin() || employe.getSuperAdmin() == 1)) {
            return em.createQuery("SELECT act FROM Activite act WHERE act.gerant.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

    public List<Activite> findByUser(Employe employe) {
        if (employe != null) {
            List<Activite> list = new ArrayList();
            List<GrandeTache> taches = em.createQuery("SELECT t FROM GrandeTache t WHERE t.tacheElementaires.employe.login" + employe.getLogin()).getResultList();
            for (GrandeTache tache : taches) {
                list.add(tache.getActivite());
            }
            return list;
        }
        return null;
    }
}
//ghadii tbadeel
//    public List<Employe> activiteEmploye(Activite activite) {
//        if (activite != null) {
//            List<Employe> emps = new ArrayList();
//            List<GrandeTache> taches = em.createQuery("SELECT t FROM GrandeTache t WHERE t.activite.id =" + activite.getId()).getResultList();
//            if (!taches.isEmpty()) {
//                for (GrandeTache tache : taches) {
//                    emps.add(tache.g)
//                }
//                });
//                System.out.println("les participant dans un projet-------> " + emps);
//            } else {
//                return null;
//            }
//        }
//        return null;
//    }
//}

//requete  = "SELECT e.dtype FROM Employe e WHERE e.id=" + employe.getId();
//    type  = (String) getEntityManager().createNativeQuery(requete).getSingleResult();
//    public String activiteType(Activite activite) {
//        String rq = "SELECT act FROM Activite act WHERE act.id=" + activite.getId();
//        return (String) getEntityManager().createNativeQuery("").getSingleResult();
//    }
