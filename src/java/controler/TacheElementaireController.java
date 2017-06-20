package controler;

import bean.Employe;
import bean.Tache;
import java.io.IOException;
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
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;
import service.DivisionFacade;
import service.EmployeFacade;
import service.GrandeTacheFacade;
import service.TacheFacade;

@Named("tacheElementaireController")
@SessionScoped
public class TacheElementaireController implements Serializable {

    private List<Tache> items = null;
    private List<Tache> listes = null;
    List<String> names = null;
    private Tache selected;
    private String nom;
    private String prenom;
    private String nomTache;
    private String login;
    private String msg;
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

    //    public void findByEmp() {
//        employe = employeFacaede.find(login);
//        items = ejbFacade.findByEmploye(employe);
//        init();
//    }
    public List<String> getDistinctTach() {
        return getFacade().findTaches();
    }

    public String retour() throws IOException {
        if (user.isAdmin()) {
            util.SessionUtil.redirect("/rapportActivite/faces/admine/ListEmp");
            return "/admine/ListEmp.xhtml";
        } else if (user.getSuperAdmin() == 1) {
            util.SessionUtil.redirect("/rapportActivite/faces/superAdmine/ListAdmine");
            return "/superAdmine/ListAdmine.xhtml";
        } else {
            util.SessionUtil.redirect("/rapportActivite/faces/simpleUser/EmpTache");
            return "/simpleUser/EmpTache.xhtml";
        }
    }

    public void staticMethode() {
        model = new BarChartModel();
        ChartSeries series = new BarChartSeries();
        System.out.println("------------>create Model ");
        System.out.println("hahowa employe " + employe);
        System.out.println("hahowa annee " + annee);
        if (!nomTache.equals("")) {
            series = ejbFacade.tachePerMonth(employe, annee, nomTache);
            model.addSeries(series);
        } else {
            List<String> tachs = getDistinctTach();
            System.out.println("elementaires--->" + tachs);
            for (String tach : tachs) {
                series = new BarChartSeries();
                series = ejbFacade.tachePerMonth(employe, annee, tach);
                model.addSeries(series);
            }
        }

        model.setTitle("Statistique");
        model.setLegendPosition("ne");
        model.setAnimate(true);
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Les Mois");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Nombre de Taches Quotidiennes");
        yAxis.setMin(0);
        yAxis.setMax(10);

    }

    public BarChartModel initBarCharModel() {
        ChartSeries InitTache = new ChartSeries();
        BarChartModel modeleInit = new BarChartModel();
        for (int mois = 1; mois <= 12; mois++) {
            InitTache.set("mois " + mois, 0);
        }
        modeleInit.addSeries(InitTache);
        modeleInit.setTitle("Statistique");
        modeleInit.setLegendPosition("ne");
        Axis xAxis = modeleInit.getAxis(AxisType.X);
        xAxis.setLabel("Les mois");
        Axis yAxis = modeleInit.getAxis(AxisType.Y);
        yAxis.setLabel("Nombre de tache");
        yAxis.setMin(0);
        yAxis.setMax(20000);
        return modeleInit;
    }

    //ss
    public void findInEmpTaches() {
        employe = employeFacaede.find(login);
        System.out.println("coontroooo" + dateCherche);
        items = ejbFacade.tacheByDate(employe, dateCherche);
        employe = new Employe();
        init();
    }

    //creation tache
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

//    public void search() {
//        items = ejbFacade.search(nom, dateMin, dateMax);
//        inititems();
//    }
//
//    public void inititems() {
//        nom = "";
//        dateMax = null;
//        dateMin = null;
//
//    }
    public void ignorer(Tache tacheElementaire) {
        selected = tacheElementaire;
        listes.remove(selected);
        selected = new Tache();
    }

    public void createList() {
        ejbFacade.createList(listes, user);
        listes = new ArrayList<>();
    }

    public void nouveau() {
        RequestContext.getCurrentInstance().execute("PF('nouveau').show()");
    }

    public int addNames() {
        if (!names.isEmpty()) {
            selected.setNom(nom);
            names.add(nom);
        } else {
            for (String n : names) {
                if (!n.equals(nom)) {
                    names.add(nom);
                }
            }
        }
        return 0;
    }

    public void ajouterList() {
        if (selected.getNom().equals("")) {
            selected.setNom(nom);
        }
        int res = testDate();
        if (res > 0) {
            RequestContext.getCurrentInstance().execute("PF('message').show()");
        }
        listes.add(selected);
        init();
    }

    public int testDate() {
        if (selected.getDateTache().after(new Date())) {
            selected.setDateTache(new Date());
            msg = "IMPOSSIBLE DATE PREXISTER !!";
            return 1;
        } else if (ejbFacade.dateInvalid(selected.getDateTache()) == false) {
            selected.setDateTache(new Date());
            msg = "DATE DEJA EFFECTUER VOUS DEVEZ TAPER UNE NOUVELLE DATE !!";
            return 2;
        }
        return 0;
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

    public String getNomTache() {
        return nomTache;
    }

    public void setNomTache(String nomTache) {
        this.nomTache = nomTache;
    }

    public List<String> getNames() {
        if (names == null) {
            names = ejbFacade.findTaches();
        }
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

