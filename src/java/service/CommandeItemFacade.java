/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.BonCommande;
import bean.CommandeItem;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author somaya
 */
@Stateless
public class CommandeItemFacade extends AbstractFacade<CommandeItem> {

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

//    public List<CommandeItem> findByCmd(BonCommande bonCommande) {
//        List<CommandeItem> items = em.createQuery("SELECT itm FROM CommandeItem itm   itm.bonCommande.id=" + bonCommande.getId()).getResultList();
//        if (items != null) {
//            return items;
//        } else {
//            return new ArrayList<>();
//        }
//    }

    public CommandeItemFacade() {
        super(CommandeItem.class);
    }

    public void createItems(List<CommandeItem> items, BonCommande bonCommande) {
        for (CommandeItem item : items) {
            item.setBonCommande(bonCommande);
            create(item);
        }
    }
}
