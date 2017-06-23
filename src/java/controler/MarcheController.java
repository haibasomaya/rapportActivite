package controler;

import bean.Division;
import bean.Employe;
import bean.GrandeTache;
import bean.Marche;
import controler.util.JsfUtil;
import java.io.IOException;
import util.JsfUtil.PersistAction;
import service.MarcheFacade;

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
import service.DivisionFacade;
import service.EmployeFacade;
import service.GrandeTacheFacade;

@Named("marcheController")
@SessionScoped
public class MarcheController implements Serializable {
    
    private Marche selected;
    private GrandeTache grandeTach;
    private String nom;
    private Date dateMin;
    private Date dateMax;
    private int type;
    private List<Marche> items = null;
    private List<Employe> emps = null;
    private List<Division> divisions = null;
    private List<Division> dvsn = null;
    List<String> names = new ArrayList<>();
    private Division division;
    private Employe employe;
    
    @EJB
    private service.MarcheFacade ejbFacade;
    @EJB
    private GrandeTacheFacade grandeTacheFacade;
    @EJB
    private DivisionFacade divisionFacade;
    @EJB
    private EmployeFacade employeFacade;
    private Employe user = util.SessionUtil.getConnectedUser();
    
    public MarcheController() {
    }
//
//    public Division adminDivision(Employe employe) {
//        return divisionFacade.adminDivision(employe);
//    }

    public String retour() throws IOException {
        util.SessionUtil.redirect("/rapportActivite/faces/myList/ListMarchet");
        return "/myList/ListMarchet.xhtml";
    }
    
    public void igniorer(Division division) {
        dvsn.remove(dvsn.indexOf(division));
    }
    
    public void ajoutDivision() {
        dvsn.add(division);
    }
    
    public void AffecterGrandeTache() {
        if (division == null) {
            grandeTach.setEmploye(user);
        } else {
            grandeTach.setEmploye(division.getDirecteur());
        }
        if (grandeTach.getDateTache() == null) {
            grandeTach.setDateTache(new Date());
        }
        if (grandeTach.getDateFin() == null) {
            grandeTach.setDateFin(new Date());
        }
        grandeTach.setActivite(selected);
        grandeTacheFacade.create(grandeTach);
        selected.setAvancement(selected.getAvancement() + grandeTach.getAvancement());
        selected.getGrandeTaches().add(grandeTach);
        ejbFacade.edit(selected);
        grandeTach = new GrandeTache();
    }
    
    public Division findDivision(Employe employe) {
        return divisionFacade.findDivisionByAdmin(employe).get(0);
    }
    
    public void deletProjet(Marche projet) {
        selected = projet;
        RequestContext.getCurrentInstance().execute("PF('Supression').show()");
    }
    
    public String detail(Marche projet) throws IOException {
        selected = projet;
        util.SessionUtil.redirect("/rapportActivite/faces/marche/MarchetDetail");
        return "/marche/MarchetDetail.xhtml";
    }
    
    public String ModifierProjet(Marche projet) throws IOException {
        selected = projet;
        util.SessionUtil.redirect("/rapportActivite/faces/marche/ModifierMarche");
        return "/marche/ModifierMarche.xhtml";
    }
    
    public void findByTypeDate() {
        if ((type == -1) && (dateMax == null && dateMin == null)) {
            items = getItems();
        }
        items = ejbFacade.findByTypeDate(user, type, dateMin, dateMax);
        System.out.println("Controler findByTypeDate()------->" + items);
        
    }
    
    public String importance(Marche projet) {
        switch (projet.getDegrer()) {
            case 1:
                return "Urgent";
            case 2:
                return "Moderer";
            case 3:
                return "Minim";
            default:
                return " ";
        }
    }
    
    public void creation() {
        if (user.isAdmin() || user.getSuperAdmin() == 1) {
            selected.setGerant(user);
        }
        if (!nom.equals(" ")) {
            selected.getFournisseurs().add(nom);
        }
        if (!divisions.isEmpty()) {
            selected.setDivisions(dvsn);
        }
        ejbFacade.create(selected);
        prepareCreate();
        dvsn = null;
    }
    
    protected void setEmbeddableKeys() {
    }
    
    protected void initializeEmbeddableKey() {
    }
    
    private MarcheFacade getFacade() {
        return ejbFacade;
    }
    
    public Marche prepareCreate() {
        selected = new Marche();
        nom = "";
        initializeEmbeddableKey();
        return selected;
    }
    
    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MarcheCreated"));
        if (!util.JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MarcheUpdated"));
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MarcheDeleted"));
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
    
    public List<Marche> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }
    
    public List<Marche> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    @FacesConverter(forClass = Marche.class)
    public static class MarcheControllerConverter implements Converter {
        
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MarcheController controller = (MarcheController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "marcheController");
            return controller.getMarche(getKey(value));
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
            if (object instanceof Marche) {
                Marche o = (Marche) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Marche.class.getName()});
                return null;
            }
        }
        
    }
    
    public List<Employe> getEmps() {
        if (emps == null) {
            emps = ejbFacade.activiteEmploye(selected);
        }
        return emps;
    }
    
    public void setEmps(List<Employe> emps) {
        this.emps = emps;
    }
    
    public String getNom() {
        return nom = "";
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Employe getUser() {
        return user;
    }
    
    public void setUser(Employe user) {
        this.user = user;
    }
    
    public Marche getSelected() {
        if (selected == null) {
            selected = new Marche();
        }
        return selected;
    }
    
    public void setSelected(Marche selected) {
        this.selected = selected;
    }
    
    public List<Marche> getItems() {
        if (items == null) {
            testItmes();
        }
        return items;
    }
    
    private void testItmes() {
        if (user.isAdmin() || user.getSuperAdmin() == 1) {
            if (ejbFacade.findByGerant(user) != null) {
                items = ejbFacade.findByGerant(user);
            }
        } else if (ejbFacade.findByUser(user) != null) {
            items = ejbFacade.findByUser(user);
        } else {
            items = new ArrayList<>();
        }
    }
    
    public Marche getMarche(java.lang.Long id) {
        return getFacade().find(id);
    }
    
    public Division getDivision() {
        if (division == null) {
            division = new Division();
        }
        return division;
    }
    
    public void setDivision(Division division) {
        this.division = division;
    }
    
    public List<Division> getDivisions() {
        if (divisions == null) {
            divisions = divisionFacade.findAll();
        }
        return divisions;
    }
    
    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }
    
    public List<Division> getDvsn() {
        if (dvsn == null) {
            dvsn = new ArrayList<>();
        }
        return dvsn;
    }
    
    public void setDvsn(List<Division> dvsn) {
        this.dvsn = dvsn;
    }
    
    public GrandeTache getGrandeTach() {
        if (grandeTach == null) {
            grandeTach = new GrandeTache();
        }
        return grandeTach;
    }
    
    public void setGrandeTach(GrandeTache grandeTach) {
        this.grandeTach = grandeTach;
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
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public List<String> getNames() {
        names.add("Envoi vers le service de marcher");
        names.add("Publication de l'appelle d'offre sur marche public");
        names.add("Ouverture de plie");
        names.add("Ordre du service");
        if (selected.getFournisseurs() != null) {
            names.add("Livraison");//fourniture
            names.add("Reception provesoir");//fourniture
            names.add("Reception defenitive");//fournitur
        }
        if (selected.getDivisions() != null) {
            names.add("Execution");//traveau du service
            names.add("Attachement");//traveu au service
        }
        return names;
    }
    
    public void setNames(List<String> names) {
        this.names = names;
    }
}
