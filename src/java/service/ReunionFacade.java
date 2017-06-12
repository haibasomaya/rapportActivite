/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Reunion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author somaya
 */
@Stateless
public class ReunionFacade extends AbstractFacade<Reunion> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReunionFacade() {
        super(Reunion.class);
    }

    public List<Reunion> findByGerant(Employe employe) {
        return em.createQuery("SELECT rn FROM Reunion rn WHERE rn.gerant.login ='" + employe.getLogin() + "'").getResultList();
    }

//    public List<Reunion> findByEmp(Employe employe) {
//        return em.createQuery("SELECT rn FROM Reunion rn WHERE rn.participants.login='" + employe.getLogin() + "'").getResultList();
//    }

    public void valider(List<Employe> employes, Reunion reunion) {
        employes.forEach((emp) -> {
            reunion.getParticipants().add(emp);
        });
        edit(reunion);
    }
}
