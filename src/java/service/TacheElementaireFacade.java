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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dell
 */
@Stateless
public class TacheElementaireFacade extends AbstractFacade<TacheElementaire> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TacheElementaireFacade() {
        super(TacheElementaire.class);
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

    public void createTache(TacheElementaire tacheElementaire, Employe employe) {
        tacheElementaire.setEmploye(employe);
        employe.getTacheElementaires().add(tacheElementaire);
        tacheElementaire.setAvancement(0L);
        tacheElementaire.setPourcentage(0L);
        tacheElementaire.setGrandeTache(null);
        tacheElementaire.setType(1);
        create(tacheElementaire);
    }

    //     public String findUserFonction(Employe employe) {
//        String requete;
//        String type = "";
//        if (employe != null) {
//            requete = "SELECT e.dtype FROM Employe e WHERE e.id=" + employe.getId();
//            type = (String) getEntityManager().createNativeQuery(requete).getSingleResult();
//        }
//        return type;
//    }
//    
}
