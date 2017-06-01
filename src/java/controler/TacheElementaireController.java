package controler;

import bean.Employe;
import bean.TacheElementaire;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.TacheElementaireFacade;

import java.io.Serializable;
import java.sql.Array;
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

@Named("tacheElementaireController")
@SessionScoped
public class TacheElementaireController implements Serializable {

    @EJB
    private service.TacheElementaireFacade ejbFacade;
    private List<TacheElementaire> items = null;
    private List<TacheElementaire> listes = new ArrayList();
    private TacheElementaire selected;
    private Date dateCherche;
    private List<String> names = new ArrayList();

    private Employe emp = util.SessionUtil.getConnectedUser();

    public TacheElementaireController() {
    }
//pr admine de division

    public void addNames(List<String> noms) {
        names.addAll(noms);
    }

    public void ajouterList() {
        testDate();
        listes.add(selected);
        System.out.println("haa selected ----->" + selected);
        System.out.println("tache de ________>" + emp);
        System.out.println("listes---->" + listes);
        prepareCreate();
    }

    public void testDate() {
        if (selected.getDateTache().before(new Date())) {
            selected.setDateTache(new Date());
            System.out.println("**************** IMPOSSIIIBLEEEE DAAATEEEE PREEXIISTERRRR*********************");
        } else if (ejbFacade.dateInvalid(selected.getDateTache()) == false) {
            selected.setDateTache(new Date());
            System.out.println("****************** dateee dejaa affeecteeeer***********************");
        }

    }

    public void createList() {
        ejbFacade.createList(listes, emp);
        listes = new ArrayList<>();;
    }

    public void ignorer(TacheElementaire tacheElementaire) {
        listes.remove(listes.indexOf(tacheElementaire));
    }

    public void chercherDate() {
        items = ejbFacade.chercherDate(dateCherche);
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TacheElementaireCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public TacheElementaire prepareCreate() {
        selected = new TacheElementaire();
        initializeEmbeddableKey();
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TacheElementaireUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TacheElementaireDeleted"));
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

    public TacheElementaire getTacheElementaire(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TacheElementaire> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TacheElementaire> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TacheElementaire.class)
    public static class TacheElementaireControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TacheElementaireController controller = (TacheElementaireController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tacheElementaireController");
            return controller.getTacheElementaire(getKey(value));
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
            if (object instanceof TacheElementaire) {
                TacheElementaire o = (TacheElementaire) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TacheElementaire.class.getName()});
                return null;
            }
        }

    }

    public TacheElementaire getSelected() {
        if (selected == null) {
            selected = new TacheElementaire();
        }
        return selected;
    }

    public void setSelected(TacheElementaire selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TacheElementaireFacade getFacade() {
        return ejbFacade;
    }

    public List<TacheElementaire> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<TacheElementaire> getListes() {
        return listes;
    }

    public void setListes(List<TacheElementaire> listes) {
        this.listes = listes;
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

    public Date getDateCherche() {
        return dateCherche;
    }

    public void setDateCherche(Date dateCherche) {
        this.dateCherche = dateCherche;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        names.add("Suprimer Fichier");
        names.add("Ajouter Fichier");
        names.add("Efectuer mission");
        names.add("Assister une reunion");
        return names;
    }

}
