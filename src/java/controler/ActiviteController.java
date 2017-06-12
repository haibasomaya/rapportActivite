package controler;

import bean.Activite;
import bean.BonCommande;
import bean.Employe;
import bean.Marche;
import bean.Projet;
import controler.util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.ActiviteFacade;

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
import service.EmployeFacade;

@Named("activiteController")
@SessionScoped
public class ActiviteController implements Serializable {

    private Activite selected;
    private String type = "";
    private List<Activite> items = null;
    private List<Employe> admins = null;
    @EJB
    private service.ActiviteFacade ejbFacade;
    @EJB
    private EmployeFacade employeFacade;

    public ActiviteController() {
    }

    public int typeActivite() {
        if (type.equals("")) {
            switch (type) {
                case "projet":
                    return 1;
                case "Marche":
                    return 2;
                case "Bon Commande":
                    return 3;
                default:
                    return 0;
            }
        }
        return -1;
    }

    public List<Employe> getAdmins() {
        if (admins == null) {
            admins = employeFacade.admines();
        }
        return admins;
    }

    public void setAdmins(List<Employe> admins) {
        this.admins = admins;
    }

    public Activite getSelected() {
        if (selected == null) {
            switch (typeActivite()) {
                case 1:
                    selected = new Projet();
                    break;
                case 2:
                    selected = new Marche();
                    break;
                case 3:
                    selected = new BonCommande();
                    break;
                case 0:
                    selected = new BonCommande();
                    break;
                default:
                    break;
            }
        }
        return selected;
    }

    public void setSelected(Activite selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ActiviteFacade getFacade() {
        return ejbFacade;
    }

    public Activite prepareCreate() {
        selected = new Activite();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ActiviteCreated"));
        if (!util.JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ActiviteUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ActiviteDeleted"));
        if (!util.JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Activite> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
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

    public Activite getActivite(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Activite> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Activite> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Activite.class)
    public static class ActiviteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ActiviteController controller = (ActiviteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "activiteController");
            return controller.getActivite(getKey(value));
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
            if (object instanceof Activite) {
                Activite o = (Activite) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Activite.class.getName()});
                return null;
            }
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
