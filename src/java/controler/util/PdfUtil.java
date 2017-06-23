//package controler.util;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.Map;
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpServletResponse;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//
///**
// *
// * @author Khalid
// */
//public class PdfUtil {
//
//    private static String absoluteJasperPath;
//    private static String absoluteWebPath;
//
//    static {
//        absoluteJasperPath ="C:\\Users\\dell\\Documents\\NetBeansProject\\rapportActivite\\src\\java\\jasper";
//        absoluteWebPath = "C:\\Users\\dell\\Documents\\NetBeansProject\\rapportActivite\\web\\";
//    }
////
////    private static void generateHeaderNotificationPdf(Map<String, Object> params) {
////        //params.put("ligne1", SessionUtil.getCurrentCommune().getHeader());
////       // params.put("commune_nom", SessionUtil.getCurrentCommune().getVille().getNom());
////      //  System.out.println("generateHeaderNotification liene1=" + SessionUtil.getCurrentCommune().getHeader());
////    }
////
////
////
////
////
////
////
////    public static void generatePdf(List myList, Map<String, Object> params, String outPoutFileName, String cheminJasper) throws JRException, IOException {
////        //JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
////        //JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
////        
////
////        
////            System.out.println("controler.util.PdfUtil.generatePdf()"+absoluteJasperPath);
////            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(myList); 
////            JasperDesign jasperDesign = JRXmlLoader.load( FacesContext.getCurrentInstance().getExternalContext().getRealPath(absoluteJasperPath + cheminJasper));
////            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
////            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, beanCollectionDataSource);        
////        
////        
////            OutputStream outputStream = getResponseOutput(outPoutFileName);
////            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
////    }
////
////    public static void generateXsl(List myList, Map<String, Object> params, String outPoutFileName, String bilan) throws JRException, IOException {
////        generateHeaderNotificationPdf(params);
////        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
////        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
////        ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
////        JRXlsExporter exporter = new JRXlsExporter();
////        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
////        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
////        exporter.exportReport();
////        getResponseXslOutput(bilan);
////    }
//
//  public static void generatePdfAvecLangue(List myList, Map<String, Object> params, String outPoutFileName, String bilan) throws JRException, IOException {
//        
//        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
//        System.out.println("bilan " + bilan);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
////        JasperReportMirrorer.mirrorLayoutJP(jasperPrint, langue);
//        OutputStream outputStream = getResponseOutput(outPoutFileName);
//        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//    }
//
//
//    private static OutputStream getResponseOutput(String fileName) throws IOException {
//        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//        httpServletResponse.addHeader("Pragma", "no-cache"); // HTTP 1.0.
//        httpServletResponse.addHeader("Expires", "0"); // Proxies.
//        httpServletResponse.addHeader("Content-Type", "application/pdf");
//        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
//        return httpServletResponse.getOutputStream();
//    }
//
////    private static OutputStream getResponseXslOutput(String fileName) throws IOException {
////        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
////        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
////        httpServletResponse.addHeader("Pragma", "no-cache"); // HTTP 1.0.
////        httpServletResponse.addHeader("Expires", "0"); // Proxies.
////        httpServletResponse.addHeader("Content-Type", "application/vnd.ms-excel");
////        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
////        return httpServletResponse.getOutputStream();
////    }
////
////    public static JasperPrint generatePdfs(List myList, Map<String, Object> params, String bilan) throws JRException, IOException {
////        System.out.println("haniii pdf");
////        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(myList);
////        System.out.println("params" + params);
////        JasperPrint jasperPrint = JasperFillManager.fillReport(PdfUtil.class.getResourceAsStream(bilan), params, jrBeanCollectionDataSource);
////        return jasperPrint;
////    }
////
////    public static void generatePdfsNotifaction(List<JasperPrint> myList, String nonFile) throws JRException, IOException {
////        JRExporter exporter = new JRPdfExporter();
////        exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, myList);
////
////        exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, getResponseOutput(nonFile));
////        exporter.exportReport();
////
////    }
//
//}
