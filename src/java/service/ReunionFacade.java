/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Reunion;
import java.util.ArrayList;
import java.util.Date;
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

    public List<Reunion> findByDate(Date datechercher) {
        if (datechercher != null) {
            return em.createQuery("SELECT rn FROM Reunion rn WHERE rn.dateDebut ='" + util.DateUtil.getSqlDate(datechercher) + "'").getResultList();
        } else {
            return null;
        }
    }

    public ReunionFacade() {
        super(Reunion.class);
    }

    public List<Reunion> findByGerant(Employe employe) {
        if (employe != null && employe.isAdmin()) {
            return em.createQuery("SELECT rn FROM Reunion rn WHERE rn.gerant.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

    public void valider(List<Employe> employes, Reunion reunion) {
        employes.forEach((emp) -> {
            reunion.getParticipants().add(emp);
        });
        edit(reunion);
    }

    public void retirerEmp(Employe employe) {
        System.out.println("----- hanii f'retirerEmp--------");
        List<Reunion> reunions = findAll();
        for (Reunion reunion : reunions) {
            List<Employe> par = reunion.getParticipants();
            for (Employe emp : par) {
                if (employe.getLogin().equals(emp.getLogin())) {
                    reunion.getParticipants().remove(reunion.getParticipants().indexOf(emp));

                }
            }
            edit(reunion);
        }
        System.out.println("------hanii (rejt bisalam mnhaa-----");
    }

    public List<Reunion> findByParticipant(Employe employe) {
        List<Reunion> reunions = findAll();
        List<Reunion> liste = new ArrayList<>();
        for (Reunion reunion : reunions) {
            List<Employe> par = reunion.getParticipants();
            for (Employe emp : par) {
                if (employe.getLogin().equals(emp.getLogin())) {
                    liste.add(reunion);
                }
            }
        }
        return liste;
    }
}
//   public List<Reunion> findByEmp(Employe employe) {
//        return em.createQuery("SELECT rn FROM Reunion rn WHERE rn.participants.login='" + employe.getLogin() + "'").getResultList();
//    }
//     
//    public List<Employe> findByDivisionOrService(List<Employe> emps, Division division,Service service){
//        
//    }
