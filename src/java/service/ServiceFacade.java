/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Division;
import bean.Service;
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
public class ServiceFacade extends AbstractFacade<Service> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServiceFacade() {
        super(Service.class);
    }

    public List<Service> search(String nom, Division division) {
        String requette = "SELECT srv FROM Service srv WHERE 1=1";
        if (division != null) {
            requette += " and srv.division.id = " + division.getId();
        }
        if (!nom.equals("")) {
            requette += SearchUtil.addConstraint("srv", "nom", "=", nom);
        }
        return em.createQuery(requette).getResultList();
    }

    public List<Service> findByDivision(Division division) {
        if (division != null) {
            return em.createQuery("SELECT srv FROM Service srv WHERE  srv.division.id = " + division.getId()).getResultList();
        } else {
            return null;
        }
    }

}
