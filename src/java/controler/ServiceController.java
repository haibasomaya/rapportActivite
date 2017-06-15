package controler;

import bean.Division;
import bean.Employe;
import bean.Service;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.ServiceFacade;

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
import service.DivisionFacade;
import service.EmployeFacade;

@Named("serviceController")
@SessionScoped
public class ServiceController implements Serializable {

    @EJB
    private service.ServiceFacade ejbFacade;
    private Service selected;
    private Division division;
    private String nom = "";
    private Employe emp;
    private List<Service> items = null;
    private List<Division> divisions = null;
    private List<Employe> employes = null;
    private List<Employe> emps = null;
    private Employe user = util.SessionUtil.getConnectedUser();
    @EJB
    private DivisionFacade divisionFacade;
    @EJB
    private EmployeFacade employeFacade;

    public ServiceController() {
    }

    public void ajouter() {
        emps.add(emp);
    }

    public void ignorer(Employe e) {
        emps.remove(emps.indexOf(e));
    }

    public void createService() {
        if (user.isAdmin()) {
            selected.setDivision(divisionFacade.findDivisionByAdmin(user).get(0));
        } else if (selected.getDivision() != null) {
            selected.setEmployes(emps);
            ejbFacade.create(selected);
            emps = new ArrayList();
        } else {
            System.out.println("*********** Inpossible de crer Pas de division *********");
        }
    }

    public void search() {
        System.out.println("**********search contrllerrr*********");
        System.out.println("Devision choisit est---->" + division);
        System.out.println("la nm tapper est---->" + nom);
        items = ejbFacade.search(nom, division);
        System.out.println("haaa les items resultat----->" + items);
    }

    public Service prepareCreate() {
        selected = new Service();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ServiceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ServiceUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ServiceDeleted"));
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

    public Service getService(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Service> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Service> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Service.class)
    public static class ServiceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ServiceController controller = (ServiceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "serviceController");
            return controller.getService(getKey(value));
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
            if (object instanceof Service) {
                Service o = (Service) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Service.class.getName()});
                return null;
            }
        }

    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {

    }

    private ServiceFacade getFacade() {
        return ejbFacade;
    }

    public List<Division> getDivisions() {
        if (divisions == null) {
            if (user.getSuperAdmin() == 1) {
                divisions = divisionFacade.findAll();
            } else {
                divisions = new ArrayList<>();
            }
        }
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
//pr creation

    public Employe getEmp() {
        if (emp == null) {
            emp = new Employe();
        }
        return emp;
    }

    public void setEmp(Employe emp) {
        this.emp = emp;
    }

    public List<Employe> getEmps() {
        if (emps == null) {
            emps = new ArrayList();
        }
        return emps;
    }

    public void setEmps(List<Employe> emps) {
        this.emps = emps;
    }

    // pr initialisation
    public List<Employe> getEmployes() {
        if (employes == null) {
            if (user != null && user.isAdmin()) {
                employes = divisionFacade.findEmpByAdmin(user);
                System.out.println("haa les employe-------> " + employes);
            } else if (user != null && user.getSuperAdmin() == 1) {
                employes = employeFacade.empNonAffecter();
            }
        }
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    public Employe getUser() {
        return user;
    }

    public void setUser(Employe user) {
        this.user = user;
    }

    public Service getSelected() {
        if (selected == null) {
            selected = new Service();
        }
        return selected;
    }

    public void setSelected(Service selected) {
        this.selected = selected;
    }

    public List<Service> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
}
