/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Device;
import bean.Employe;
import bean.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dell
 */
@Stateless
public class EmployeFacade extends AbstractFacade<Employe> {

    @EJB
    private DeviceFacade deviceFacade;

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmployeFacade() {
        super(Employe.class);
    }

    public List<Employe> findByService(Service service) {
        return em.createQuery("SELECT emp FROM Employe emp WHERE emp.service.id = " + service.getId()).getResultList();
    }

    public List<Employe> liseBloquer(int etat) {
        if (etat > 0) {
            return em.createQuery("SELECT emp from Employe emp WHERE emp.bloquer = " + etat).getResultList();
        } else {
            return null;
        }
    }

    public List<Employe> recherche(String nom, String prenom, String login) {
        List<Employe> emp = new ArrayList();
        String requette = "SELECT emp from Employe emp WHERE 1=1";
        if (!login.equals("")) {
            emp.add(find(login));
            return emp;
        } else {
            if (!nom.equals("")) {
                requette += util.SearchUtil.addConstraint("emp", "nom", "=", nom);
            }
            if (!prenom.equals("")) {
                requette += util.SearchUtil.addConstraint("emp", "prenom", "=", prenom);
            }
        }
        return emp = em.createQuery(requette).getResultList();
    }

    public int deconexion(Employe employe) {
        if (employe != null) {
            List<Device> devices = employe.getDevices();
            Device d = new Device();
            int p = devices.size();
            for (int i = 0; i < p; i++) {
                for (Device device : devices) {
                    d = device;
                }
            }
            d.setDateSortie(new Date());
            deviceFacade.edit(d);
//            employe.getDevices().get(p).setDateSortie(new Date());
            return 1;
        } else {
            return -1;
        }
    }

    public void creerEmp(Employe employe) throws MessagingException {
        String pass = util.HashageUtil.genererMdp();
        System.out.println("haaaa mot de pass lii t'genera----->" + pass);
        String message = "votre login est :" + employe.getLogin() + " votre password " + pass + "  'vous pouvez le changer apres s'il vous volez ' ";
        String objecte = "compte Wilaya";
//        util.EmailUtil.sendMail("wlialaya.marrakech@gmail.com", "somaya@wijdan", "Bonjour M/Mme" + employe.getNom() + " " + message, employe.getEmail(), objecte);
        employe.setPassword(util.HashageUtil.sha256(pass));
        create(employe);
    }

    public int connexion(String login, String password) {
        if (login.equals("") || password.equals("")) {
            return -1;
        }
        Employe employe = find(login);
        if (employe == null) {
            return -2;
        }
        if (!employe.getPassword().equals(password)) {
            employe.setEssay(employe.getEssay() + 1);
            edit(employe);
            System.out.println("Essay num" + employe.getEssay());
        }
        if (employe.getEssay() == 3) {
            employe.setBloquer(1);
            edit(employe);
            return -3;
        }
        if (employe.getPassword().equals(password) && employe.getBloquer() == 0) {
            employe.setEssay(0);
            edit(employe);
            deviceFacade.creerDevice(employe);
            util.SessionUtil.registerUser(employe);
            return 1;
        } else {
            return 0;
        }
    }

    public int bloquerDebloquer(Employe employe) {
        if (employe != null) {
            switch (employe.getBloquer()) {
                case 0:
                    employe.setBloquer(1);
                    edit(employe);
                    return 1;
                case 1:
                    employe.setBloquer(0);
                    edit(employe);
                    return 0;
                default:
                    return -1;
            }
        } else {
            return -2;
        }
    }

    public List<Employe> EmpAdminBloquer(int etat, List<Employe> employes) {
        List<Employe> emps = new ArrayList();
        for (Employe emp : employes) {
            if (emp.getBloquer() == etat) {
                emps.add(emp);
            }
        }
        return emps;
    }

}
//        !util.HashageUtil.sha256(password).equals(employe.getPassword())
//            employe.equals(util.HashageUtil.sha256(password))
