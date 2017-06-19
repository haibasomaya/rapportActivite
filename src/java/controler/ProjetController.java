package controler;

import bean.Division;
import bean.Employe;
import bean.GrandeTache;
import bean.Projet;
import bean.Service;
import controler.util.JsfUtil;
import java.io.IOException;
import util.JsfUtil.PersistAction;
import service.ProjetFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.context.RequestContext;
import service.ActiviteFacade;
import service.DivisionFacade;
import service.EmployeFacade;
import service.GrandeTacheFacade;
import service.ServiceFacade;

@Named("projetController")
@SessionScoped
public class ProjetController implements Serializable {

    private Projet selected;
    private Date dateMin;
    private Date dateMax;
    private int type;
    private Service service;
    private Division division;
    private Employe emp;
    private GrandeTache grandeTach = null;
    private List<Service> services = null;
    private List<Projet> items = null;
    private List<Employe> emps = null;
    private List<Employe> employes = null;
    private List<Division> divisions = null;
    Employe user = util.SessionUtil.getConnectedUser();
    @EJB
    private service.ProjetFacade ejbFacade;
    @EJB
    private ActiviteFacade activiteFacade;
    @EJB
    private GrandeTacheFacade grandeTacheFacade;
    @EJB
    private EmployeFacade employeFacade;
    @EJB
    private ServiceFacade serviceFacade;
    @EJB
    private DivisionFacade divisionFacade;

    public ProjetController() {
    }

    public String retour() throws IOException {
        util.SessionUtil.redirect("/rapportActivite/faces//myList/ListProjet");
        return "/myList/ListProjet.xhtml";
    }

    public void voirTache(Employe employe) {
        emp = employe;
        RequestContext.getCurrentInstance().execute("PF('ListTachDialg').show()");
        emp = null;
    }

    public List<GrandeTache> findGrandeTachByEmp() {
        return grandeTacheFacade.findGrandeTachByEmpByact(emp, selected);
    }

    public void listEmp() {
        employes = employeFacade.findByService(service);
        System.out.println("haa les employaes dyalooo ----->" + employes);
    }

    public void findByTypeDate() {
        if ((type == -1) && (dateMax == null && dateMin == null)) {
            items = getItems();
        }
        items = ejbFacade.findByTypeDate(user, type, dateMin, dateMax);
        System.out.println("Controler findByTypeDate()------->" + items);

    }

    public void AffecterGrandeTache() {
        System.out.println("grandeTach--------->" + grandeTach);
        if (emp != null) {
            System.out.println("haaa l'emloye------>" + emp);
            grandeTach.setEmploye(emp);
        } else if (division != null) {
            emp = division.getDirecteur();
            grandeTach.setEmploye(emp);
        } else {
            grandeTach.setEmploye(user);
        }
        grandeTach.setActivite(selected);
        grandeTacheFacade.create(grandeTach);
//        selected.setAvancement(selected.getAvancement() + grandeTach.getAvancement());
        selected.getGrandeTaches().add(grandeTach);
        ejbFacade.edit(selected);
        grandeTach = new GrandeTache();

        init();
    }
//        selected.setAvancement(selected.getAvancement() + grandeTach.getAvancement());

    private void init() {
        emp = null;
        services = null;
        employes = null;
        grandeTach = new GrandeTache();
    }

    public String importance(Projet projet) {
        switch (projet.getDegrer()) {
            case 1:
                return "Urgent";
            case 2:
                return "Moderer";
            case 3:
                return "Minim";
            default:
                return " ";
        }
    }

    public void deletProjet(Projet projet) {
        selected = projet;
        RequestContext.getCurrentInstance().execute("PF('Supression').show()");
    }

    public String detail(Projet projet) throws IOException {
        selected = projet;
        util.SessionUtil.redirect("/rapportActivite/faces/projet/ProjetDetail");
        return "/projet/ProjetDetail.xhtml";
    }

    public String ModifierProjet(Projet projet) throws IOException {
        selected = projet;
        util.SessionUtil.redirect("/rapportActivite/faces/projet/ModifierProjet");
        return "/projet/ModifierProjet.xhtml";
    }

    public void creation() {
        if (user.isAdmin() || user.getSuperAdmin() == 1) {
            selected.setGerant(user);
        }
        ejbFacade.create(selected);
        prepareCreate();
    }

    public Projet prepareCreate() {
        selected = new Projet();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProjetCreated"));
        if (!util.JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProjetUpdated"));
    }

    public void destroy() {
        ejbFacade.deleteProjet(selected);
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ProjetDeleted"));
        if (!util.JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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

    @FacesConverter(forClass = Projet.class)
    public static class ProjetControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProjetController controller = (ProjetController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "projetController");
            return controller.getProjet(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Projet) {
                Projet o = (Projet) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Projet.class.getName()});
                return null;
            }
        }

    }

    public Projet getProjet(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Projet> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Projet> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<Projet> getItems() {
        if (items == null) {
            testItmes();
        }
        return items;
    }

    private void testItmes() {
        if (user.isAdmin() || user.getSuperAdmin() == 1) {
            if (ejbFacade.findByGerant(user) != null) {
                items = ejbFacade.findByGerant(user);
            }
        } else if (ejbFacade.findByUser(user) != null) {
            items = ejbFacade.findByUser(user);
        } else {
            items = new ArrayList<>();
        }
    }

    public Projet getSelected() {
        if (selected == null) {
            selected = new Projet();
        }
        return selected;
    }

    public void setSelected(Projet selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProjetFacade getFacade() {
        return ejbFacade;
    }

    public List<Employe> getEmps() {
        if (emps == null) {
            emps = ejbFacade.activiteEmploye(selected);
            System.out.println("les participant dans un projet-------> " + emps);
        }
        return emps;
    }

    public void setEmps(List<Employe> emps) {
        this.emps = emps;
    }

    public Employe getUser() {
        return user;
    }

    public void setUser(Employe user) {
        this.user = user;
    }

    public GrandeTache getGrandeTach() {
        if (grandeTach == null) {
            grandeTach = new GrandeTache();
        }
        return grandeTach;
    }

    public void setGrandeTach(GrandeTache grandeTach) {
        this.grandeTach = grandeTach;
    }

    public Date getDateMin() {
        return dateMin;
    }

    public void setDateMin(Date dateMin) {
        this.dateMin = dateMin;
    }

    public Date getDateMax() {
        return dateMax;
    }

    public void setDateMax(Date dateMax) {
        this.dateMax = dateMax;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public Employe getEmp() {
        if (emp == null) {
            emp = new Employe();
        }
        return emp;
    }

    public void setEmp(Employe emp) {
        this.emp = emp;
    }

    public List<Service> getServices() {
        if (services == null) {
            division = divisionFacade.adminDivision(selected.getGerant());
            services = serviceFacade.findByDivision(division);
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Employe> getEmployes() {
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    public Division getDivision() {
        if (division == null) {
            division = new Division();
        }
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
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

}
