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
import org.primefaces.component.chart.bar.BarChart;
import org.primefaces.model.chart.BarChartModel;
import service.DivisionFacade;
import service.EmployeFacade;

@Named("tacheElementaireController")
@SessionScoped
public class TacheElementaireController implements Serializable {

    private List<TacheElementaire> items = null;
    private List<TacheElementaire> listes = null;
    private List<TacheElementaire> taches = null;
    private List<String> names = new ArrayList();
    private TacheElementaire selected;
    private String nom;
    private String prenom;
    private String login;
    private int type;
    private BarChartModel model;
    private int annee;
    private Date dateCherche;
    private Employe employe;
    private Employe user = util.SessionUtil.getConnectedUser();
    @EJB
    private service.TacheElementaireFacade ejbFacade;
    @EJB
    private EmployeFacade employeFacaede;

    @EJB
    private DivisionFacade divisionFacade;

    public TacheElementaireController() {
    }

    public List<Employe> employes() {
        if (user.isAdmin()) {
            return divisionFacade.findEmpByAdmin(user);
        } else if (user.getSuperAdmin() == 1) {
            return employeFacaede.admines();
        } else {
            return null;
        }
    }

    //statistique
    public void tryMethod() {
        ejbFacade.tacheByMonth(user, annee, 2);
        System.out.println("------------>create Model ");
        System.out.println("hahowa employe " + user);
        System.out.println("hahowa annee " + annee);
        System.out.println("hahowa Model controler---> " + model);
    }
//pr admine de divisioni

    public void findByEmp() {
        employe = employeFacaede.find(login);
        items = ejbFacade.findByEmploye(employe);
        init();
    }

    private void addNames() {
        if (names.isEmpty()) {
            names.add(nom);
        } else {
            for (String name : names) {
                if (!name.equals(nom)) {
                    names.add(nom);
                }
            }
        }
    }

    private void init() {
        nom = "";
        login = "";
        prenom = "";
        dateCherche = null;
    }

    public void findTache() {
        items = ejbFacade.findByDateEmp(login, dateCherche, type);
        System.out.println("liiiite des tache de " + login + " --->" + items);
    }

    public void ajouterList() {
        testDate();
        listes.add(selected);
        System.out.println("haa selected ----->" + selected);
        System.out.println("tache de ________>" + user);
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
        ejbFacade.createList(listes, user);
        listes = new ArrayList<>();
        addNames();
    }

    public void ignorer(TacheElementaire tacheElementaire) {
        listes.remove(listes.indexOf(tacheElementaire));
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

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public List<TacheElementaire> getItems() {
        if (items == null) {
            items = ejbFacade.findByEmploye(user);
        }
        return items;
    }

    public List<TacheElementaire> getListes() {
        if (listes == null) {
            listes = new ArrayList<>();
        }
        return listes;
    }

    public void setListes(List<TacheElementaire> listes) {
        this.listes = listes;
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
        if (names.isEmpty()) {
            names.add(nom);
        } else {
            for (String nam : names) {
                if (!nom.equals(nam)) {
                    names.add(nam);
                }
            }
        }
        return names;
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

    public String getNom() {
        nom = "";
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

    public BarChartModel getModel() {
        if (model == null) {
            model = new BarChartModel();
        }
        return model;
    }

    public void setModel(BarChartModel model) {
        this.model = model;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<TacheElementaire> getTaches() {
        if (taches == null) {
            taches = ejbFacade.findAll();
        }
        return taches;
    }

    public void setTaches(List<TacheElementaire> taches) {
        this.taches = taches;
    }

}
