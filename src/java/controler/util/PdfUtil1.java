//package controler.util;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpServletResponse;
//import net.sf.jasperreports.engine.JRDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporter;
//import net.sf.jasperreports.engine.JRExporterParameter;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
//import net.sf.jasperreports.engine.export.JRXlsExporter;
//import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
//import static org.primefaces.component.calendar.Calendar.PropertyKeys.locale;
//
///**
// *
// * @author Khalid
// */
//public class PdfUtil1 {
//
//    private static String absoluteJasperPath;
//    private static String absoluteWebPath;
//
//    static {
//        absoluteJasperPath = "C:\\Users\\Younes\\Documents\\taxeCommune\\taxe_commune_zouani\\src\\java\\report\\";
//        absoluteWebPath = "C:\\Users\\Younes\\Documents\\taxeCommune\\taxe_commune_zouani\\web\\";
//    }
//
//    private static void generateHeaderNotificationPdf(Map<String, Object> params) {
//        //l'entête du pdf
//        params.put("ligne1", SessionUtil.getCurrentCommune().getHeader());
//        params.put("commune_nom", SessionUtil.getCurrentCommune().getVille().getNom());
//        System.out.println("generateHeaderNotification liene1=" + SessionUtil.getCurrentCommune().getHeader());
//    }
//
//    private static void generateHeaderNotificationPdfArabe(Map<String, Object> params) {
//        params.put("ligne1", SessionUtil.getCurrentCommune().getHeaderArabe());
//        params.put("commune_nom", SessionUtil.getCurrentCommune().getNomArabe());
//        System.out.println("generateHeaderNotification liene1=" + SessionUtil.getCurrentCommune().getHeader());
//    }
//
//    
//    public static void generateXls(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Locale locale) throws JRException {
//        generateHeaderNotificationPdf(params);
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        String jasperPrint = JasperFillManager.fillReportToFile(absoluteJasperPath + "taxeSejour.jasper", params, jrBeanCollectionDataSource);
//        fillLocalAndRedevableInformationsPdf(params, locale);
//
//        JRXlsExporter exporter = new JRXlsExporter();
//
//        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
//                jasperPrint);
//        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, absoluteWebPath + "sample_report.xls");
//        exporter.exportReport();
//    }
//
//    public static void generateXls(List myList, Map<String, Object> params, String outPoutFileName, String bilan) throws JRException {
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        String jasperPrint = JasperFillManager.fillReportToFile(absoluteJasperPath + "LocaleParTaxe.jasper", params, jrBeanCollectionDataSource);
//
//        JRXlsExporter exporter = new JRXlsExporter();
//
//        exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,
//                jasperPrint);
//        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
//                absoluteWebPath + "sample_report.xls");
//        exporter.exportReport();
//    }
//
//    private static void generateHeaderAndGeneralInformations(Map<String, Object> params, Locale locale, String langue) {
//        System.out.println("haniii pdf");
//        if (langue.equals("Francais")) {
//            generateHeaderNotificationPdf(params);
//        } else if (langue.equals("Arabe")) {
//            generateHeaderNotificationPdfArabe(params);
//        }
//
//        if (locale != null) {
//            if (langue.equals("Francais")) {
//                fillLocalAndRedevableInformationsPdf(params, locale);
//            } else if (langue.equals("Arabe")) {
//                fillLocalAndRedevableInformationsPdfArabe(params, locale);
//            }
//        }
//    }
//
//    
//
//   
//    
//    public static void generatePdfAvecLangue(List myList, Map<String, Object> params, String outPoutFileName, String bilan, String langue) throws JRException, IOException {
//        if(langue.equals("Arabe")){
//            generateHeaderNotificationPdfArabe(params);
//        }
//        if(langue.equals("Francais")){
//            generateHeaderNotificationPdf(params);
//        }
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        System.out.println("bilan " + bilan);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
////        JasperReportMirrorer.mirrorLayoutJP(jasperPrint, langue);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generatePdfAvecLangueTransport(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Vehicule vehicule, String langue) throws JRException, IOException {
//    
//    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Locale locale) throws JRException, IOException {
//        System.out.println("haniii pdf");
//        generateHeaderNotificationPdf(params);
//        if (locale != null) {
//            fillLocalAndRedevableInformationsPdf(params, locale);
//        }
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Terrain terrain) throws JRException, IOException {
//        System.out.println("haniii pdf");
//        generateHeaderNotificationPdf(params);
//        if (locale != null) {
//            fillTerrainAndRedevableInformationsPdf(params, terrain);
//        }
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Abattoire abattoire) throws JRException, IOException {
//        generateHeaderNotificationPdf(params);
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generateSimulationPdf(List dataSource, String outPoutFileName, String bilan) throws JRException, IOException {
//        JRDataSource jRDataSource = new JRBeanCollectionDataSource(dataSource);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), null, jRDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//
//    }
//
//    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Vehicule vehicule) throws JRException, IOException {
//        generateHeaderNotificationPdf(params);
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String bilan) throws JRException, IOException {
//        generateHeaderNotificationPdf(params); // ajouter l'entête du pdf au paramétre
//        //instancier l'objet JRBeanCollectionDataSource en lui associent la liste des objet a afficher dans le pdf
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        //instncier l'objet jasperPrint en lui passant la liste des pamaramétres et l'objet jrBeanCollectionDataSource contient les donnée
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        //exportation du pdf
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generatePdfItext(List myList, Map<String, Object> params, String outPoutFileName, String bilan) throws JRException, IOException {
//        generateHeaderNotificationPdf(params);
//        System.out.println("params " + params.size());
//        System.out.println("locales " + myList.size());
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//    public static void generateXsl(List myList, Map<String, Object> params, String outPoutFileName, String bilan, Vehicule vehicule) throws JRException, IOException {
//        generateHeaderNotificationPdf(params);
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//        JRXlsExporter exporter = new JRXlsExporter();
//        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//        exporter.exportReport();
//        getResponseXslOutput(bilan);
//    }
//
//   
//
//    public static OutputStream getResponseOutput(String fileName) throws IOException {
//        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//        httpServletResponse.addHeader("Pragma", "no-cache"); // HTTP 1.0.
//        httpServletResponse.addHeader("Expires", "0"); // Proxies.
//        httpServletResponse.addHeader("Content-Type", "application/pdf");
//        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
//        return httpServletResponse.getOutputStream();
//    }
//
//    private static OutputStream getResponseXslOutput(String fileName) throws IOException {
//        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//        httpServletResponse.addHeader("Pragma", "no-cache"); // HTTP 1.0.
//        httpServletResponse.addHeader("Expires", "0"); // Proxies.
//        httpServletResponse.addHeader("Content-Type", "application/vnd.ms-excel");
//        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
//        return httpServletResponse.getOutputStream();
//    }
//
//    public static JasperPrint generatePdfs(List myList, Map<String, Object> params, String bilan) throws JRException, IOException {
//        System.out.println("haniii pdf");
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        System.out.println("params" + params);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
//        return jasperPrint;
//    }
//
//    public static void generatePdfsNotifaction(List<JasperPrint> myList, String nonFile) throws JRException, IOException {
//        JRExporter exporter = new JRPdfExporter();
//        exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, myList);
//
//        exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, getResponseOutput(nonFile));
//        exporter.exportReport();
//
//    }
//
//    public static String removeLastCaracter(String chaine, String caracter) {
//        if (chaine.length() > 0 && chaine.endsWith(caracter)) {
//            chaine = chaine.substring(0, chaine.length() - 1);
//        }
//        return chaine;
//    }
//
//    
//
//}
