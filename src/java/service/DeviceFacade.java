/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Device;
import bean.Employe;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author somaya
 */
@Stateless
public class DeviceFacade extends AbstractFacade<Device> {

    @EJB
    private EmployeFacade employeFacade;

    @PersistenceContext(unitName = "rapportActivitePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DeviceFacade() {
        super(Device.class);
    }

    public List<Device> findBYEmp(Employe employe) {
        if (employe != null) {
            return em.createQuery("SELECT d FROM Device d WHERE d.employe.login ='" + employe.getLogin() + "'").getResultList();
        } else {
            return null;
        }
    }

    public Device findMac(Device device) {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
//            System.out.println("Current IP address : " + ip.getHostAddress());
            device.setAdresseIP(ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
//                System.out.print("Current MAC address : ");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                System.out.println(sb.toString());
                device.setAdresseMac(sb.toString());
                return device;

            }
        } catch (java.net.UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return device;
    }

    public void creerDevice(Employe employe) {
        Device device = new Device();
        device = findMac(device);
        device.setDateEntree(new Date());
        device.setEmploye(employe);
        employe.getDevices().add(device);
        employeFacade.edit(employe);
        create(device);
        util.Session.setAttribut("Device", device);
    }
}
