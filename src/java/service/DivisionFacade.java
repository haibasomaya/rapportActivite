/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Division;
import bean.Employe;
import bean.Service;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dell
 */
@Stateless
public class DivisionFacade extends AbstractFacade<Division> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Division adminDivision(Employe employe) {
        if (employe.isAdmin()) {
            Division d = (Division) em.createQuery("SELECT d FROM Division d WHERE d.directeur.login ='" + employe.getLogin() + "'").getResultList().get(0);
            if (d != null) {
                return d;
            }
        }
        return new Division();
    }

    public DivisionFacade() {
        super(Division.class);
    }

    public List<Division> findDivisionByAdmin(Employe employe) {
        if (employe.isAdmin()) {
            return em.createQuery("SELECT d FROM Division d WHERE d.directeur.login = '" + employe.getLogin() + "'").getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Employe> findEmpByAdmin(Employe employe) {
        if (employe != null) {
            List<Employe> employes = new ArrayList();
            Division division = findDivisionByAdmin(employe).get(0);
            if (division != null) {
                List<Service> services = em.createQuery("SELECT srv FROM Service srv WHERE srv.division.id=" + division.getId()).getResultList();
                if (services.isEmpty()) {
                    return null;
                } else {
                    services.stream().map((service) -> em.createQuery("SELECT emp FROM Employe emp WHERE emp.service.id=" + service.getId()).getResultList()).forEachOrdered((emps) -> {
                        employes.addAll(emps);
                    });
                }
            }
            return employes;
        } else {
            return null;
        }
    }
}
