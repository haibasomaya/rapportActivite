package controler;

import bean.Employe;
import bean.GrandeTache;
import bean.Tache;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
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
import org.primefaces.model.chart.BarChartModel;
import service.DivisionFacade;
import service.EmployeFacade;
import service.GrandeTacheFacade;
import service.TacheFacade;

@Named("tacheElementaireController")
@SessionScoped
public class TacheElementaireController implements Serializable {

    private List<Tache> items = null;
    private List<Tache> listes = null;
    private List<Tache> taches = null;
    private Tache selected;
    private String nom;
    private String prenom;
    private String login;
    private BarChartModel model;
    private int annee;
    private Date dateCherche;
    private Employe employe;
    private Date dateMin;
    private Date dateMax;

    private Employe user = util.SessionUtil.getConnectedUser();
    @EJB
    private service.TacheFacade ejbFacade;
    @EJB
    private EmployeFacade employeFacaede;
    @EJB
    private DivisionFacade divisionFacade;
    @EJB
    private GrandeTacheFacade grandeTacheFacade;

    public TacheElementaireController() {
    }

    //creation tache
    public void ignorer(Tache tacheElementaire) {
        selected = tacheElementaire;
        listes.remove(selected);
        selected = new Tache();
    }

//            for (int i = 0; i < p; i++) {
//                TacheElementaire t = taches.get(i);
//                if (!t.getNom().equals(nom)) {
//                    taches.add(selected);
//                    selected.setNom(nom);
//                }
//            }
    public int addNames() {
        if ((!nom.equals(""))) {
            int p = taches.size();
            for (Tache t : taches) {
                if (!t.getNom().equals(nom)) {
                    selected.setNom(nom);
                    taches.add(selected);
                    return 1;
                }
            }
        }
        return 0;
    }

    public void createList() {
        ejbFacade.createList(listes, user);
        listes = new ArrayList<>();
    }

    public void ajouterList() {
        if (selected.getNom().equals("")) {
            selected.setNom(nom);
            addNames();
        }
        testDate();
        listes.add(selected);
        System.out.println("haa selected ----->" + selected);
        System.out.println("tache de ________>" + user);
        System.out.println("listes---->" + listes);
        init();
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

    private void init() {
        nom = "";
        login = "";
        prenom = "";
        dateCherche = null;
        selected = new Tache();
        prepareCreate();
    }

    //statistique
//    public void tryMethod() {
//        ejbFacade.tacheByMonth(user, annee, 2);
//        System.out.println("------------>create Model ");
//        System.out.println("hahowa employe " + user);
//        System.out.println("hahowa annee " + annee);
//        System.out.println("hahowa Model controler---> " + model);
//    }
//pr admine de divisioni
    public List<Employe> employes() {
        if (user.isAdmin()) {
            return divisionFacade.findEmpByAdmin(user);
        } else if (user.getSuperAdmin() == 1) {
            return employeFacaede.admines();
        } else {
            return null;
        }
    }

    public void findInMyTaches() {
        System.out.println("h dateCherche ------> " + dateCherche);
        items = ejbFacade.tacheByDate(user, dateCherche);
        System.out.println("haaa la liste trouver ------> " + items);
    }

    public void search() {
        items = ejbFacade.search(nom, dateMin, dateMax);
        inititems();
    }

    public void inititems() {
        nom = "";
        dateMax = null;
        dateMin = null;

    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TacheElementaireCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public Tache prepareCreate() {
        selected = new Tache();
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

    public Tache getTacheElementaire(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Tache> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Tache> getItemsAvailableSelectOne() {
        return getFacade().findAll();

    }

    @FacesConverter(forClass = Tache.class)
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
            if (object instanceof Tache) {
                Tache o = (Tache) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Tache.class.getName()});
                return null;
            }
        }

    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TacheFacade getFacade() {
        return ejbFacade;
    }

    public Tache getSelected() {
        if (selected == null) {
            selected = new Tache();
        }
        return selected;
    }

    public void setSelected(Tache selected) {
        this.selected = selected;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }
//    public void findByEmp() {
//        employe = employeFacaede.find(login);
//        items = ejbFacade.findByEmploye(employe);
//        init();
//    }

    public List<Tache> getTaches() {
        if (taches == null) {
            taches = ejbFacade.findAll();
        }
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    public List<Tache> getItems() {
        if (items == null) {
            if (user.getSuperAdmin() != 1) {
                items = ejbFacade.findByEmploye(user);
            } else {
                items = new ArrayList<>();
            }
        }
        return items;
    }

    public List<Tache> getListes() {
        if (listes == null) {
            listes = new ArrayList<>();
        }
        return listes;
    }

    public void setListes(List<Tache> listes) {
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
}
//    public void findInEmpTaches() {
//        System.out.println("h dateCherche ------> " + dateCherche);
//        items = ejbFacade.findByDateEmp(employe, dateCherche);
//        System.out.println("haaa la liste trouver ------> " + items);
//
//    }
//Tache 
