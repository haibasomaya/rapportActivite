/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.GrandeTache;
import bean.Projet;
import bean.Reunion;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author somaya
 */
@Stateless
public class ProjetFacade extends AbstractFacade<Projet> {

    @EJB
    private ReunionFacade reunionFacade;
    @EJB
    private GrandeTacheFacade grandeTacheFacade;

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjetFacade() {
        super(Projet.class);
    }

    public List<Projet> findByGerant(Employe employe) {
        if (employe != null && (employe.isAdmin() || employe.getSuperAdmin() == 1)) {
            return em.createQuery("SELECT act FROM Projet act WHERE act.gerant.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

    public List<Projet> findByUser(Employe employe) {
        if (employe != null) {
            List<Projet> list = new ArrayList();
            List<GrandeTache> taches = em.createQuery("SELECT t FROM GrandeTache t WHERE t.tacheElementaires.employe.login" + employe.getLogin()).getResultList();
            for (GrandeTache tache : taches) {
                Projet p = (Projet) tache.getActivite();
                list.add(p);
            }
            return list;
        }
        return null;
    }

    public List<Employe> activiteEmploye(Projet activite) {
        if (activite != null) {
            List<Employe> emps = new ArrayList();
            List<GrandeTache> taches = em.createQuery("SELECT t FROM GrandeTache t WHERE t.activite.id =" + activite.getId()).getResultList();
            if (!taches.isEmpty()) {
                taches.forEach(new Consumer<GrandeTache>() {
                    @Override
                    public void accept(GrandeTache tache) {
                        emps.addAll(tache.getEmployes());
                    }
                });
            } else {
                return null;
            }
        }
        return null;
    }

    public int deleteProjet(Projet projet) {
        if (projet != null) {
            List<Reunion> reunions = projet.getReunions();
            if (!reunions.isEmpty()) {
                reunions.forEach((reunion) -> {
                    reunionFacade.remove(reunion);
                });
            }
            List<GrandeTache> grandeTaches = projet.getGrandeTaches();
            if (!grandeTaches.isEmpty()) {
                grandeTaches.forEach((grandeTache) -> {
                    grandeTacheFacade.remove(grandeTache);
                });
            }
            remove(projet);
            return 0;
        } else {
            return -1;
        }
    }
}
