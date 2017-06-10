/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controler;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;

/**
 *
 * @author somaya
 */
@Named(value = "templateController")
@Dependent
public class TemplateController {

    /**
     * Creates a new instance of TemplateController
     */
    public TemplateController() {
    }
    
    public void redirectHome() throws IOException{
        FacesContext.getCurrentInstance().getExternalContext().redirect("/rapportActivite/faces/pageAccueil/loginV.xhtml");
    }
    
}
