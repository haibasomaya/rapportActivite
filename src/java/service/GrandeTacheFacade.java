/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Activite;
import bean.GrandeTache;
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
//    public int createGrandeTache(Activite activite , GrandeTache grandeTache){
//        if(grandeTache!= null && activite!= null){
//            grandeTache.setActivite(activite);
//            grandeTache.setAvancement(gr);
//            
//        }
//    }
}
