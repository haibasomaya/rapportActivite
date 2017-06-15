package controler;

import bean.Employe;
import bean.Projet;
import controler.util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.ProjetFacade;

import java.io.Serializable;
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

@Named("projetController")
@SessionScoped
public class ProjetController implements Serializable {

    @EJB
    private service.ProjetFacade ejbFacade;
    private List<Projet> items = null;
    private Projet selected;
    Employe user = util.SessionUtil.getConnectedUser();

    public ProjetController() {
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
            items = getFacade().findAll();
        }
        return items;
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
}
