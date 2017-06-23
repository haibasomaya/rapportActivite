package controler;

import bean.BonCommande;
import bean.CommandeItem;
import bean.Employe;
import controler.util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.BonCommandeFacade;

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
import org.primefaces.context.RequestContext;
import service.CommandeItemFacade;

@Named("bonCommandeController")
@SessionScoped
public class BonCommandeController implements Serializable {

    private BonCommande selected;
    private CommandeItem commandeItem;
    private List<BonCommande> items = null;
    private List<CommandeItem> commandeItems = null;
    private List<CommandeItem> listItem = new ArrayList();
    
    private Employe user = util.SessionUtil.getConnectedUser();

    @EJB
    private service.BonCommandeFacade ejbFacade;
    @EJB
    private CommandeItemFacade commandeItemFacade;
    
    

    public BonCommandeController() {
    }
    public void generatePdf(BonCommande bonCommande ){
        System.out.println("Itemm----->"+items);
    }

    public void voirItem(BonCommande bnCmd) {
        selected = bnCmd;
        RequestContext.getCurrentInstance().execute("PF('bonCommandeDialg').show()");
    }

    public void valider() {
        selected.setGerant(user);
        selected.setCommandeItems(commandeItems);
        ejbFacade.create(selected);
        commandeItemFacade.createItems(commandeItems, selected);
        commandeItems = new ArrayList();

    }

    public void ignorer(CommandeItem commandeItem) {
        commandeItems.remove(commandeItems.indexOf(commandeItem));
    }

    public void ajoutItem() {
        commandeItems.add(commandeItem);
        commandeItem = new CommandeItem();
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BonCommandeFacade getFacade() {
        return ejbFacade;
    }

    public BonCommande prepareCreate() {
        selected = new BonCommande();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BonCommandeCreated"));
        if (!util.JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BonCommandeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("BonCommandeDeleted"));
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

    public BonCommande getBonCommande(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<BonCommande> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BonCommande> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BonCommande.class)
    public static class BonCommandeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BonCommandeController controller = (BonCommandeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bonCommandeController");
            return controller.getBonCommande(getKey(value));
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
            if (object instanceof BonCommande) {
                BonCommande o = (BonCommande) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BonCommande.class.getName()});
                return null;
            }
        }

    }

    public BonCommande getSelected() {
        if (selected == null) {
            selected = new BonCommande();
        }
        return selected;
    }

    public void setSelected(BonCommande selected) {
        this.selected = selected;
    }

    public List<BonCommande> getItems() {
        if (items == null) {
            if (user.getSuperAdmin() == 1) {
                items = ejbFacade.findByGerant(user);
            } else {
                items = ejbFacade.findByGerant(user);
            }
        }
        return items;
    }

    public List<CommandeItem> getCommandeItems() {
        if (commandeItems == null) {
            commandeItems = new ArrayList();
        }
        return commandeItems;
    }

    public void setCommandeItems(List<CommandeItem> commandeItems) {
        this.commandeItems = commandeItems;
    }

    public CommandeItem getCommandeItem() {
        if (commandeItem == null) {
            commandeItem = new CommandeItem();
        }
        return commandeItem;
    }

    public void setCommandeItem(CommandeItem commandeItem) {
        this.commandeItem = commandeItem;
    }

    public List<CommandeItem> getListItem() {
        if (listItem == null) {
            listItem = ejbFacade.listItem(selected);
        }
        return listItem;
    }

    public void setListItem(List<CommandeItem> listItem) {
        this.listItem = listItem;
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

}
