package controler;

import bean.Activite;
import bean.GrandeTache;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.GrandeTacheFacade;

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
import service.ActiviteFacade;

@Named("grandeTacheController")
@SessionScoped
public class GrandeTacheController implements Serializable {

    private List<GrandeTache> items = null;
    private GrandeTache selected;
    private List<Activite> activites = null;
    @EJB
    private service.GrandeTacheFacade ejbFacade;
    @EJB
    private ActiviteFacade activiteFacade;

    public GrandeTacheController() {
    }

    public void createGrdTache() {

    }

    public GrandeTache prepareCreate() {
        selected = new GrandeTache();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("GrandeTacheCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("GrandeTacheUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("GrandeTacheDeleted"));
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

    public GrandeTache getGrandeTache(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<GrandeTache> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<GrandeTache> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = GrandeTache.class)
    public static class GrandeTacheControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            GrandeTacheController controller = (GrandeTacheController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "grandeTacheController");
            return controller.getGrandeTache(getKey(value));
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
            if (object instanceof GrandeTache) {
                GrandeTache o = (GrandeTache) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), GrandeTache.class.getName()});
                return null;
            }
        }

    }

    public GrandeTache getSelected() {
        if (selected == null) {
            selected = new GrandeTache();
        }
        return selected;
    }

    public void setSelected(GrandeTache selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private GrandeTacheFacade getFacade() {
        return ejbFacade;
    }

    public List<GrandeTache> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<Activite> getActivites() {
        if (activites == null) {
            activites = new ArrayList();
        }
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

}
