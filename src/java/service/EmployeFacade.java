/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Activite;
import bean.Device;
import bean.Division;
import bean.Employe;
import bean.Reunion;
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
    @EJB
    private DivisionFacade divisionFacade;
    @EJB
    private ReunionFacade reunionFacade;
    @EJB
    private ActiviteFacade activiteFacade;
    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmployeFacade() {
        super(Employe.class);
    }

    public int deleteEmploye(Employe employe) {
        if (employe.isAdmin()) {
            if (!divisionFacade.findDivisionByAdmin(employe).isEmpty()) {
                Division d = divisionFacade.findDivisionByAdmin(employe).get(0);
                if (d != null) {
                    System.out.println("**********haaaaa divison--->" + d);
                    d.setDirecteur(null);
                    divisionFacade.edit(d);
                }
            }
            List<Reunion> reunions = reunionFacade.findByGerant(employe);
            if (reunions != null) {
                for (Reunion reunion : reunions) {
                    reunionFacade.remove(reunion);
                }
                System.out.println("*********haniii msa7eeet les reunion***********");
            }
            List<Activite> activites = activiteFacade.findByGerant(employe);
            if (activites != null) {
                for (Activite activite : activites) {
                    activiteFacade.remove(activite);
                }
                System.out.println("*************haniii msa7eeet les activitii*********");
            }
        }
        List<Device> dv = deviceFacade.findBYEmp(employe);
        if (dv != null) {
            for (Device device : dv) {
                deviceFacade.remove(device);
            }
            System.out.println("*************haniii msa7eeet les device*********");
        }
        remove(employe);
        System.out.println("*************haniii msa7eeet les employee*********");
        return 0;
    }

    public List<Employe> empNonAffecter() {
        return em.createQuery("SELECT e FROM Employe e WHERE e.service= NULL AND e.admin=0 AND e.superAdmin=0").getResultList();
    }

    public List<Employe> findByString(String nom, String prenom, String login) {
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
        System.out.println("haa la requette --->" + requette);
        return emp = em.createQuery(requette).getResultList();
    }

    public List<Employe> admines() {
        return em.createQuery("SELECT emp FROM Employe emp WHERE emp.admin = 1").getResultList();
    }

    public List<Employe> findByService(Service service) {
        return em.createQuery("SELECT emp FROM Employe emp WHERE emp.service.id = " + service.getId()).getResultList();
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

    public int creerEmp(Employe employe) throws MessagingException {
        if (find(employe.getLogin()) != null) {
            return -1;
        } else {
            String pass = util.HashageUtil.genererMdp();
            System.out.println("haaaa mot de pass lii t'genera----->" + pass);
            String message = "Bonjour Mr/Mme " + employe.getNom() + "  " + employe.getPrenom() + "  " + " votre login est :" + employe.getLogin() + " et votre password :" + pass + "  vous pouvez le changer apres s'il vous volez  ";
            String objecte = " Nouveau compte Wilaya";
            boolean send = util.EmailUtil.sendMail("marrakechsafiwilaya@gmail.com", "marrakech@safi", message, employe.getEmail(), objecte);
            employe.setPassword(util.HashageUtil.sha256(pass));
            if (send == true) {
                create(employe);
            }
            return 1;
        }
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

    public List<Employe> liseBloquer(int etat) {
        if (etat > 0) {
            return em.createQuery("SELECT emp from Employe emp WHERE emp.bloquer = " + etat).getResultList();
        } else {
            return null;
        }
    }

    public List<Employe> EmpAdminBloquer(int etat, List<Employe> employes) {
        List<Employe> emps = new ArrayList();
        if (employes != null || !employes.isEmpty()) {
            for (Employe employe : employes) {
                if (employe.getBloquer() == etat) {
                    emps.add(employe);
                }
            }
        }
        return emps;
    }

}
//        !util.HashageUtil.sha256(password).equals(employe.getPassword())
//            employe.equals(util.HashageUtil.sha256(password))
