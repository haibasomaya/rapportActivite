package controler;

import bean.Division;
import bean.Employe;
import bean.Reunion;
import bean.Service;
import controler.util.JsfUtil;
import util.JsfUtil.PersistAction;

import service.ReunionFacade;

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
import service.ServiceFacade;

@Named("reunionController")
@SessionScoped
public class ReunionController implements Serializable {

    private Reunion selected;
    private Division division;
    private Service service;
    private Employe emp;
    private List<Reunion> items = null;
    private List<Service> services = null;
    private List<Employe> employes = null;
    private List<Employe> emps = null;
    private List<Division> divisions = null;
    @EJB
    private service.ReunionFacade ejbFacade;
    @EJB
    private DivisionFacade divisionFacade;
    @EJB
    private ServiceFacade serviceFacade;

    @EJB
    private EmployeFacade employeFacade;

    private Employe employe = util.SessionUtil.getConnectedUser();

    public ReunionController() {
    }

    public void listServices() {
        if (division != null) {
            System.out.println("la divisionnn est-----> " + division);
            services = serviceFacade.findByDivision(division);
        }

    }

    public void valider() {
        selected.setGerant(employe);
        ejbFacade.create(selected);
        ejbFacade.valider(emps, selected);
        System.out.println("haaaa new Reunion---->" + selected);
        emps = null;
    }

    public void ignorer(Employe emp) {
        emps.remove(emps.indexOf(emp));
    }

    public void ajoutEmp() {
        emps.add(emp);
    }

    public void listEmp() {
        employes = employeFacade.findByService(service);
        System.out.println("haa les employaes dyalooo ----->" + employes);
    }

    private void initAdmine() {
        if (employe != null && (employe.isAdmin())) {
            division = divisionFacade.findDivisionByAdmin(employe);
            services = serviceFacade.findByDivision(division);
            items = ejbFacade.findByGerant(employe);
        } else {
            division = new Division();
            services = new ArrayList();
            items = ejbFacade.findByGerant(employe);
            System.out.println("haaaaa a5eeer fct dyal rn par employe ----->" + items);
        }
    }

    public Reunion prepareCreate() {
        selected = new Reunion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ReunionCreated"));
        if (util.JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ReunionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ReunionDeleted"));
        if (util.JsfUtil.isValidationFailed()) {
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

    public Reunion getReunion(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Reunion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Reunion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Reunion.class)
    public static class ReunionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReunionController controller = (ReunionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reunionController");
            return controller.getReunion(getKey(value));
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
            if (object instanceof Reunion) {
                Reunion o = (Reunion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Reunion.class.getName()});
                return null;
            }
        }

    }

    public List<Reunion> getItems() {
        if (items == null) {
            initAdmine();
        }
        return items;
    }

    public Reunion getSelected() {
        if (selected == null) {
            selected = new Reunion();
        }
        return selected;
    }

    public void setSelected(Reunion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ReunionFacade getFacade() {
        return ejbFacade;
    }

    public Division getDivision() {
        if (division == null) {
            initAdmine();
        }
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
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
            initAdmine();
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Employe> getEmployes() {
        if (employes == null) {
            employes = new ArrayList();
        }
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
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

    public List<Division> getDivisions() {
        if (divisions == null) {
            divisions = divisionFacade.findAll();
        }
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
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

}
