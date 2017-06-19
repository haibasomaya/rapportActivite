package controler;

import bean.Activite;
import bean.Employe;
import bean.GrandeTache;
import java.io.IOException;
import util.JsfUtil;
import util.JsfUtil.PersistAction;
import service.GrandeTacheFacade;

import java.io.Serializable;
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
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import service.ActiviteFacade;

@Named("grandeTacheController")
@SessionScoped
public class GrandeTacheController implements Serializable {

    private List<GrandeTache> items = null;
    private List<Activite> activites = null;
    private List<Employe> emps = null;
    private GrandeTache selected;
    private Activite activite;
    private Date dateCherche;
    private BarChartModel model = null;
    private int annee;
    private Employe employe;
    private String nom;
    @EJB
    private service.GrandeTacheFacade ejbFacade;
    @EJB
    private ActiviteFacade activiteFacade;

    private Employe user = util.SessionUtil.getConnectedUser();

    public GrandeTacheController() {
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

    public void tryMethod() {
        System.out.println("------------>create Model ");
        System.out.println("hahowa employe " + employe);
        System.out.println("hahowa annee " + annee);
        System.out.println("hahowa annee " + nom);
        model = ejbFacade.tacheByMonth(employe, annee, nom);
        model.setTitle("Statistique");
        model.setLegendPosition("ne");
        model.setAnimate(true);
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Les sites");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Quantite Materiel");
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
//statistique

    public void GrandeByDate() {
        System.out.println("h dateCherche ------> " + dateCherche);
        items = ejbFacade.GrandeByDate(user, dateCherche);
        System.out.println("haaa la liste trouver ------> " + items);
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
            items = ejbFacade.findGrandeTachByEmp(user);
        }
        return items;
    }

    public List<Activite> getActivites() {
        if (activites == null) {
            if (user.isAdmin() || user.getSuperAdmin() == 1) {
                activites = activiteFacade.findByGerant(user);
            } else {
                activites = activiteFacade.findByUser(user);
            }
        }
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public Activite getActivite() {
        if (activite == null) {
            activite = new Activite();
        }
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    public List<Employe> getEmps() {
        return emps;
    }

    public void setEmps(List<Employe> emps) {
        this.emps = emps;
    }

    public Employe getUser() {
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

    public BarChartModel getModel() {
        if (model == null) {
            System.out.println("model avant init " + initBarCharModel());
            model = initBarCharModel();
            System.out.println("model apres init " + initBarCharModel());
        }
        return model;
    }

    public void setModel(BarChartModel model) {
        this.model = model;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public Employe getEmploye() {
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

}
