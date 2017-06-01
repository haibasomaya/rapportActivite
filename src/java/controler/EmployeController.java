package controler;

import bean.Employe;
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

@Named("employeController")
@SessionScoped
public class EmployeController implements Serializable {

    @EJB
    private service.EmployeFacade ejbFacade;
    private List<Employe> items = null;
    private List<Employe> employes = null;
    private Employe selected;
    private String login;
    private String password;
    private String nom;
    private String prenom;
    private int etat = -1;

    public String connecter() {
        System.out.println("connecter controler");
        int res = ejbFacade.connexion(login, password);
        if (res == -1) {
            System.out.println("vous devez nseret login et passwourd");
        } else if (res == -2) {
            System.out.println("inseret login et password correcte");
        } else if (res == -3) {
            System.out.println("vous etes bloquer maintenant");
        } else if (res == 0) {
            System.out.println(" ereure Inconuu");
        } else if (res > 0) {
            System.out.println("haaaaa res ====>" + res);
            selected = util.SessionUtil.getConnectedUser();
            System.out.println("finn ghadiii____>" + goProfil());
            return goProfil();
        }
        return null;
    }

    private String goProfil() {
        if (selected.isAdmin()) {
            System.out.println("");
            return "/profils/ProfilAdmin.xhtml";
        } else {
            return "/tacheElementaire/EmpTache.xhtml";
        }
    }

    public void bloquerDebloquer(Employe employe) {
        int res = ejbFacade.bloquerDebloquer(employe);
    }

    public void recherche() {
        employes = ejbFacade.recherche(nom, prenom, login);
        init();
    }

    public void listBloquer() {
        employes = ejbFacade.liseBloquer(etat);
        init();
    }

    public void init() {
        login = null;
        password = null;
        prenom = null;
        nom = null;
    }
//        ejbFacade.creerEmp(selected, selected.getPassword());

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmployeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public EmployeController() {
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

    public Employe getSelected() {
        if (selected == null) {
            selected = new Employe();
        }
        return selected;
    }

    public List<Employe> getItems() {
        if (items == null) {
            items = ejbFacade().findAll();
        }
        return items;
    }

    public void setSelected(Employe selected) {
        this.selected = selected;
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

    public List<Employe> getEmployes() {
        if (employes == null) {
            employes = ejbFacade.findAll();
        }
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

}
