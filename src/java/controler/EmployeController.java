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
import org.primefaces.context.RequestContext;
import service.DivisionFacade;
import service.ReunionFacade;
import service.ServiceFacade;

@Named("employeController")
@SessionScoped
public class EmployeController implements Serializable {

    private Employe selected;
    private Employe user;
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
    @EJB
    private ReunionFacade reunionFacade;

    public EmployeController() {
    }
//            RequestContext.getCurrentInstance().execute("PF('eventDialog').hide()");
//      RequestContext.getCurrentInstance().execute("PF('Supression').show()");

    public void view(Employe employe) {
        selected = employe;
        RequestContext.getCurrentInstance().execute("PF('EmployeViewDialog').show()");

    }

    public Division adminDivision(Employe employe) {
        if (divisionFacade.findDivisionByAdmin(employe) != null) {
            return divisionFacade.findDivisionByAdmin(employe).get(0);
        } else {
            return new Division();
        }
    }

    public void edit(Employe employe) {
        selected = employe;
        RequestContext.getCurrentInstance().execute("PF('EmployeEditDialog').show()");

    }

    public void Supression(Employe employe) {
        selected = employe;
        RequestContext.getCurrentInstance().execute("PF('Supression').show()");
    }

    public void deleteEmploye() {
        int res = ejbFacade.deleteEmploye(selected);
        System.out.println("delete==--------------->" + res);
        employes = ejbFacade.admines();
    }

    public void findByString() {
        System.out.println("nom--->" + nom + " prenom------>" + prenom + "login------>" + login);
        if (!ejbFacade.findByString(nom, prenom, login).isEmpty()) {
            employes = ejbFacade().findByString(nom, prenom, login);
            initParam();
        }
    }

    public void chercher() {
        if (service != null) {
            employes = ejbFacade.findByService(service);
        }
        if (etat >= 0) {
            employes = ejbFacade.EmpAdminBloquer(etat, employes);
        }
        if (etat < 0 && service == null) {
            employes = null;
        }
        if (division != null && user.getSuperAdmin() == 1) {
            employes = new ArrayList<>();
            employes.add(division.getDirecteur());
        }
        System.out.println("Employes dyal recherche ------>" + employes);
        initParam();
    }

    public void initParam() {
        login = "";
        nom = "";
        prenom = "";
        services = new ArrayList<>();
        etat = -1;
        division = new Division();

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
        if (!user.isAdmin() && user.getSuperAdmin() == 0) {
            return true;
        } else {
            return false;
        }
    }

//    public void listBloque() {
//        items = ejbFacade.liseBloquer(etat);
//        System.out.println("liiiste des compte bmoquer sont ----->" + items);
//    }
    public void empAdminBloquer() {
        employes = ejbFacade.EmpAdminBloquer(etat, employes);
        System.out.println("Employes ------>" + employes);
    }

    public void bloquerDebloquer(Employe employe) {
        ejbFacade.bloquerDebloquer(employe);
    }

    public void creationEmp() throws MessagingException {
        int res = ejbFacade.creerEmp(selected);
        if (user.getSuperAdmin() == 1) {
            selected.setAdmin(true);
            ejbFacade.edit(selected);
            division.setDirecteur(selected);
            divisionFacade.edit(division);
        }
        if (res < 0) {
            System.out.println("***********Ce login est déja exister***********");
        }
        System.out.println("haaaa res de creation------->" + res);
        prepareCreate();
    }

    public String deconnexion() {
        int res = ejbFacade.deconexion(user);
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
            user = util.SessionUtil.getConnectedUser();
            return goProfil();
        }
        return null;
    }

    private String goProfil() throws IOException {
        employes = null;
        if (user.isAdmin()) {
            util.SessionUtil.redirect("/rapportActivite/faces/admine/ListEmp");
            return "/admine/ListEmp.xhtml";
        } else if (user.getSuperAdmin() == 1) {
            util.SessionUtil.redirect("/rapportActivite/faces//superAdmine/ListAdmine");
            return "/superAdmine/ListAdmine.xhtml";
        } else {
            util.SessionUtil.redirect("/rapportActivite/faces/simpleUser/EmpTache");
            return "/simpleUser/EmpTache.xhtml";
        }
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

    public void updateAdmine() {
        if (division != null) {
            Division d = divisionFacade.findDivisionByAdmin(selected).get(0);
            System.out.println("haa division la9dima dyaal " + selected + "  " + d);
            d.setDirecteur(null);
            divisionFacade.edit(d);
            division.setDirecteur(selected);
            System.out.println("haa division jdida dyaal " + selected + "  " + division);
            divisionFacade.edit(division);
            division = null;
        }

    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmployeUpdated"));
        selected = new Employe();

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
            if (user.isAdmin()) {
                employes = divisionFacade.findEmpByAdmin(user);
            } else if (user.getSuperAdmin() == 1) {
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
            if (user.isAdmin()) {
                division = divisionFacade.findDivisionByAdmin(user).get(0);
                services = serviceFacade.findByDivision(division);
            } else {
                services = new ArrayList<>();
            }
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
            if (user.isAdmin()) {
                division = divisionFacade.findDivisionByAdmin(user).get(0);
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

    public Employe getUser() {
        if (user == null) {
            user = new Employe();
        }
        return user;
    }

    public void setUser(Employe user) {
        this.user = user;
    }

    public List<Employe> getItems() {
        if (items == null) {
            items = ejbFacade().findAll();
        }
        return items;
    }

}
