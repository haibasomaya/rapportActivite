package controler;

import bean.Division;
import bean.Employe;
import bean.Service;
import java.io.IOException;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.EmployeFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.mail.MessagingException;
import service.DivisionFacade;
import service.ServiceFacade;

@Named("employeController")
@SessionScoped
public class EmployeController implements Serializable {

    private Employe selected;
    private Employe employe;
    private String login;
    private String password;
    private String nom;
    private String prenom;
    private int etat;
    private Service service;
    private Division division;
    private List<Employe> items = null;
    private List<Employe> employes = null;
    private List<Employe> admines = null;
    private List<Service> services = null;
    private List<Division> divisions = null;
    @EJB
    private service.EmployeFacade ejbFacade;
    @EJB
    private ServiceFacade serviceFacade;
    @EJB
    private DivisionFacade divisionFacade;

    public EmployeController() {
    }

    public void empByServiceDivision() {
        if (division != null && service == null) {
            items = divisionFacade.findEmpByAdmin(division.getDirecteur());
        } else if (division != null && service != null) {
            items = ejbFacade.findByService(service);
        } else {
            items = ejbFacade.findAll();
        }
        System.out.println("les items--------> " + items);
    }

    public void listEmp() {
        if (division != null) {
            services = serviceFacade.findByDivision(division);
        }
    }

    public void ListServices() {
        if (division != null) {
            services = serviceFacade.findByDivision(division);
        }
    }

    public boolean simpleUser() {
        if (!employe.isAdmin() && employe.getSuperAdmin() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void empAdminBloquer() {
        employes = ejbFacade.EmpAdminBloquer(etat, employes);
        System.out.println("Employes ------>" + employes);
    }

    public void listBloque() {
        items = ejbFacade.liseBloquer(etat);
        System.out.println("liiiste des compte bmoquer sont ----->" + items);
    }

    public void recherche() {
        employes = new ArrayList<>();
        if (ejbFacade.recherche(nom, prenom, login) != null) {
            employes.addAll(ejbFacade().recherche(nom, prenom, login));
        }
        if (ejbFacade.liseBloquer(etat) != null) {
            employes.addAll(ejbFacade.liseBloquer(etat));
        }
        System.out.println("List employes----->" + employes);

    }

    private void findByDivision() {
        if (employe.isAdmin()) {
            division = divisionFacade.findDivisionByAdmin(employe);
            services = serviceFacade.findByDivision(division);
        } else {
            services = new ArrayList<>();
        }
    }

    public String deconnexion() {
        int res = ejbFacade.deconexion(employe);
        if (res < 0) {
            System.out.println("employe est --------------> NULL");
            return null;
        } else {
            return "../pageAccueil/loginV.xhtml";
        }
    }

    public String connecter() throws IOException {
        System.out.println("connecter controler");
        int res = ejbFacade.connexion(login, password);
        if (res == -1) {
            System.out.println("vous devez inseret votre login et passwourd");
        } else if (res == -2) {
            System.out.println("inseret login et password correcte");
        } else if (res == -3) {
            System.out.println("vous etes bloquer maintenant");
        } else if (res == 0) {
            System.out.println(" ereure Inconuu");
        } else if (res > 0) {
            employe = util.SessionUtil.getConnectedUser();
            return goProfil();
        }
        return null;
    }

    private String goProfil() throws IOException {
        if (employe.isAdmin()) {
            util.SessionUtil.redirect("/rapportActivite/faces/admine/ListEmp");
            return "/admine/ListEmp.xhtml";
        } else if (employe.getSuperAdmin() == 1) {
            util.SessionUtil.redirect("/rapportActivite/faces/superAdmine/ListEmp");
            return "/admine/ListEmp.xhtml";
        } else {
            util.SessionUtil.redirect("/rapportActivite/faces/simpleUser/EmpTache");
            return "/simpleUser/EmpTache.xhtml";
        }
    }

    public void bloquerDebloquer(Employe employe) {
        int res = ejbFacade.bloquerDebloquer(employe);
    }

    public void creationEmp() throws MessagingException {
        ejbFacade.creerEmp(selected);
        prepareCreate();
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmployeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public Employe prepareCreate() {
        selected = new Employe();
        initializeEmbeddableKey();
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmployeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EmployeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    ejbFacade().edit(selected);
                } else {
                    ejbFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    @FacesConverter(forClass = Employe.class)
    public static class EmployeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmployeController controller = (EmployeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "employeController");
            return controller.getEmploye(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Employe) {
                Employe o = (Employe) object;
                return getStringKey(o.getLogin());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Employe.class.getName()});
                return null;
            }
        }

    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmployeFacade ejbFacade() {
        return ejbFacade;
    }

    public Employe getEmploye(java.lang.String id) {
        return ejbFacade().find(id);
    }

    public List<Employe> getItemsAvailableSelectMany() {
        return ejbFacade().findAll();
    }

    public List<Employe> getItemsAvailableSelectOne() {
        return ejbFacade().findAll();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public Employe getSelected() {
        if (selected == null) {
            selected = new Employe();
        }
        return selected;
    }

    public void setSelected(Employe selected) {
        this.selected = selected;
    }

    public List<Employe> getEmployes() {
        if (employes == null) {
            if (employe.isAdmin()) {
                employes = divisionFacade.findEmpByAdmin(employe);
            } else if (employe.getSuperAdmin() == 1) {
                employes = ejbFacade.admines();
            } else {
                employes = new ArrayList();
            }
        }
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    public Service getService() {
        if (service == null) {
            service = new Service();
        }
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<Service> getServices() {
        if (services == null) {
            findByDivision();
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Division> getDivisions() {
        if (divisions == null) {
            divisions = divisionFacade.findAll();
        }
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    public Division getDivision() {
        if (division == null) {
            if (employe.isAdmin()) {
                division = divisionFacade.findDivisionByAdmin(employe);
            }
        } else {
            division = new Division();
        }
        return division;
    }

    public List<Employe> getAdmines() {
        if (admines == null) {
            admines = ejbFacade.admines();
        }
        return admines;
    }

    public void setAdmines(List<Employe> admines) {
        this.admines = admines;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Employe getEmploye() {
        if (employe == null) {
            employe = new Employe();
        }
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public List<Employe> getItems() {
        if (items == null) {
            items = ejbFacade().findAll();
        }
        return items;
    }

}
