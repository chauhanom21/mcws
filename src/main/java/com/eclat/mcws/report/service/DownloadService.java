package com.eclat.mcws.report.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.report.bean.DailyProductivityBean;
import com.eclat.mcws.report.bean.ReportQueryParam;


@Service
public class DownloadService {

	public static final String TEMPLATE = "/mcws-report.jrxml";
	public static final String TEMPLATE_CODER_DAILY_PRODCTIVITY = "/coder_daily_productivity_report.jrxml";
	public static final String TEMPLATE_INVOICE_MCCG = "/MCCG_TEMPLATE.xls";
	public static final String TEMPLATE_INVOICE_ORHS = "/ORHS_TEMPLATE.xls";
	public static final String TEMPLATE_INVOICE_HC = "/HC_TEMPLATE.xls";
	public static final String TEMPLATE_INVOICE_PC = "/PC_TEMPLATE.xls";
		
	private static final String INVOICE_TEMPLATE_PATH = "/WEB-INF/report_templates/invoice/";
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	@Log
	private Logger logger;
	
	@Autowired
	private JasperDatasourceService datasource;
	
	@Autowired
	private ExporterService exporter;
	
	@Autowired
	private ReportService reportService;
	
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public void downloadSupervisorReport( HttpServletResponse response, ReportQueryParam requestParam ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream reader=null;
		try {
		    final boolean generated = reportService.generateSupervisorReport(requestParam);
		    if(generated) {
		    	//responseMap.put("isDataAvailable", true);
				//responseMap.put("reportData", reportsData);
		    	requestParam.setIsDataAvailable(true);
		    	final String relativePath = requestParam.getRelativePath();
		    	// Create an output byte stream where data will be written
				File file = new File(relativePath);

				reader = new FileInputStream(file);

				long length = file.length();
	            byte[] bytes = new byte[(int) length];

	            reader.read(bytes);

	            baos.write(bytes);
				response.setHeader("Content-Disposition", "inline; filename=" + "Supervisor_Report.csv");
				
				// Set content type
				response.setContentType("application/vnd.ms-excel");
				response.setContentLength(baos.size());
				
				write(response, baos);
		    }
	    }  catch (Exception e) {
	       logger.error("Exception while downloading reports : ", e);
	    }
	}
	
	
	public void download(String type, HttpServletResponse response, String fromDate, String toDate, String client, String chartType) {
		try {
			// 1. Add report parameters
			HashMap<String, Object> params = new HashMap<String, Object>(); 
			params.put("Title", "User Report");
			 
			// 2.  Retrieve template
			InputStream reportStream = this.getClass().getResourceAsStream(TEMPLATE); 
			 
			// 3. Convert template to JasperDesign
			JasperDesign jd = JRXmlLoader.load(reportStream);
			 
			// 4. Compile design to JasperReport
			JasperReport jr = JasperCompileManager.compileReport(jd);
			 
			// 5. Create the JasperPrint object
			// Make sure to pass the JasperReport, report parameters, and data source
			JasperPrint jp = JasperFillManager.fillReport(jr, params, datasource.getDataSource(fromDate, toDate, client, chartType));
			 
			// 6. Create an output byte stream where data will be written
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 
			// 7. Export report
			exporter.export(type, "supervisor_report", jp, response, baos);
			 
			// 8. Write to reponse stream
			write(response, baos);
		
		} catch (JRException jre) {
			logger.error("Unable to process download");
			throw new RuntimeException(jre);
		}
	}
	
	@SuppressWarnings("static-access")
	public void downloadClientwiseConsolidationReport( HttpServletResponse response, String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		InputStream reader=null;
		try {
			
			// Create an output byte stream where data will be written
			File file = new File(fileName);

			reader = new FileInputStream(file);

			long length = file.length();
            byte[] bytes = new byte[(int) length];

            reader.read(bytes);

            baos.write(bytes);
			response.setHeader("Content-Disposition", "inline; filename=" + "Clientwise_Consolidation_Report.csv");
			
			// Set content type
			response.setContentType("application/vnd.ms-excel");
			response.setContentLength(baos.size());
			
			write(response, baos);
		
		} catch (Exception jre) {
			logger.error("Unable to process download for clientwise report");
		}
	    finally {
	    	try {
				baos.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	}
	
	/**
	 * 	
	 * @param response
	 * @param fileName
	 */
	
	@SuppressWarnings("static-access")
	public void downloadFilewiseTATReport( HttpServletResponse response, String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		InputStream reader=null;
		try {
			
			// Create an output byte stream where data will be written
			File file = new File(fileName);

			reader = new FileInputStream(file);

			long length = file.length();
            byte[] bytes = new byte[(int) length];

            reader.read(bytes);

            baos.write(bytes);
			response.setHeader("Content-Disposition", "inline; filename=" + "Filewise_TAT_Report.xls");
			
			// Set content type
			response.setContentType("application/vnd.ms-excel");
			response.setContentLength(baos.size());
			
			write(response, baos);
		
		} catch (Exception jre) {
			logger.error("Unable to process download for clientwise report");
		}
	    finally {
	    	try {
				baos.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	}
	
	/**
	 * 	
	 * @param response
	 * @param fileName
	 */
	
	@SuppressWarnings("static-access")
	public void downloadUserTypeTrackingReport( HttpServletResponse response, String fileName, String userType) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		InputStream reader=null;
		try {
			
			// Create an output byte stream where data will be written
			File file = new File(fileName);

			reader = new FileInputStream(file);

			long length = file.length();
            byte[] bytes = new byte[(int) length];

            reader.read(bytes);

            baos.write(bytes);
			response.setHeader("Content-Disposition", "inline; filename=" + userType + "_Tracking_Report.xls");
			
			// Set content type
			response.setContentType("application/vnd.ms-excel");
			response.setContentLength(baos.size());
			
			write(response, baos);
		
		} catch (Exception jre) {
			logger.error("Unable to process download for clientwise report");
		}
	    finally {
	    	try {
				baos.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	}
	
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public Map<String, Object> downloadInvoiceReport( HttpServletRequest request, HttpServletResponse response, ReportQueryParam requestParam ) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			 //Read INVOICE REPORT TEMPLATE path from ServletContext		
			ServletContext servletContext = request.getSession().getServletContext();
			String relativeWebPath = INVOICE_TEMPLATE_PATH;
			String invoiceReportTemplatePath = servletContext.getRealPath(relativeWebPath);
			
			responseMap = reportService.generateInvoiceReports(requestParam, responseMap);
			
			if( !(responseMap.containsKey("isDataAvailable") && Boolean.parseBoolean(responseMap.get("isDataAvailable").toString()))){
				return responseMap;
			}
			else if(responseMap.containsKey("isCSVFilesGenerated") && Boolean.parseBoolean(responseMap.get("isCSVFilesGenerated").toString())) {
				//
			    // The Relative path is the root directory of data to be
			    // compressed.
			    //
				final String relativePath = requestParam.getRelativePath();
				final String clientName = requestParam.getQueryClient();
				if(relativePath != null && clientName != null) {
					/**
					 * Copy the template of the client from resource to download location.
					 */
					copyTemplateToRalatinvePath(invoiceReportTemplatePath, relativePath+"/", clientName);
					
					File directory = new File(relativePath);
				    String[] files = directory.list();
				    // Checks to see if the directory contains some files.
				    if (files != null && files.length > 0) {
					    // Call the zipFiles method for creating a zip stream.
					    byte[] zip = zipFiles(directory, files);
					    
					    // setting the downloadFile path to map
					    responseMap.put("downloadFilePath", relativePath);
					    /**
					     * Sends the response back to the user / browser. The
					     * content for zip file type is "application/zip". We
					     * also set the content disposition as attachment for
					     * the browser to show a dialog that will let user 
					     * choose what action will he do to the sent content.
					     */
					    ServletOutputStream sos = response.getOutputStream();
					    response.setContentType("application/zip");
					    response.setHeader("Content-Disposition", "attachment; filename=\""+clientName+".ZIP\"");
					
					    sos.write(zip);
					    sos.flush();
				    }
				}
		    }
	    }  catch (Exception e) {
	       logger.error("Exception while downloading reports : ", e);
	    }
		return responseMap;
	}
	
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public Map<String,Object>  downloadDailyProductivityReport( HttpServletResponse response, ReportQueryParam requestParam ) {
		Map<String,Object> responseMap = new HashMap<String, Object>();
		try {
			final List<DailyProductivityBean> reportsData = reportService.generateCoderDailyProductivityReportData(requestParam);
			if(reportsData != null && reportsData.size() > 0) {
				
				responseMap.put("isDataAvailable", true);
				responseMap.put("reportData", reportsData);
				requestParam.setIsDataAvailable(true);
				
				// 1. Add report parameters
				HashMap<String, Object> params = new HashMap<String, Object>(); 
				params.put("Title", "User Report");
				 
				// 2.  Retrieve template
				InputStream reportStream = this.getClass().getResourceAsStream(TEMPLATE_CODER_DAILY_PRODCTIVITY); 
				 
				// 3. Convert template to JasperDesign
				JasperDesign jd = JRXmlLoader.load(reportStream);
				 
				// 4. Compile design to JasperReport
				JasperReport jr = JasperCompileManager.compileReport(jd);
				 
				// 5. Create the JasperPrint object
				// Make sure to pass the JasperReport, report parameters, and data source
				JasperPrint jp = JasperFillManager.fillReport(jr, params, datasource.getCoderDailyProductivtyDataSource(reportsData));
				 
				// 6. Create an output byte stream where data will be written
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 
				// 7. Export report
				exporter.export("xls", "Coder_Daily_Productivity_Report", jp, response, baos);
				 
				// 8. Write to reponse stream
				write(response, baos);
			} else {
				responseMap.put("isDataAvailable", false);
				return responseMap;
			}
		} catch (JRException jre) {
			logger.error("Unable to process download");
			throw new RuntimeException(jre);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return responseMap;
	}
	
	
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public void downloadLocalQADailyProductivityReport( HttpServletResponse response, ReportQueryParam requestParam ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream reader=null;
		try {
		    final boolean generated = reportService.generateLocalQADailyProductivityReportData(requestParam);
		    if(generated) {
		    	//responseMap.put("isDataAvailable", true);
				//responseMap.put("reportData", reportsData);
		    	requestParam.setIsDataAvailable(true);
		    	final String relativePath = requestParam.getRelativePath();
		    	// Create an output byte stream where data will be written
				File file = new File(relativePath);

				reader = new FileInputStream(file);

				long length = file.length();
	            byte[] bytes = new byte[(int) length];

	            reader.read(bytes);

	            baos.write(bytes);
				response.setHeader("Content-Disposition", "inline; filename=" + "LocalQA_Daily_Productivity_Report.csv");
				
				// Set content type
				response.setContentType("application/vnd.ms-excel");
				response.setContentLength(baos.size());
				
				write(response, baos);
		    }
	    }  catch (Exception e) {
	       logger.error("Exception while downloading reports : ", e);
	    }
	}
	
	
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public boolean downloadCodersQualityReports( final HttpServletResponse response, ReportQueryParam requestParam ) {
		boolean isDataAvailable = false;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream reader = null;
		try {
			final String excelFileName = "Quality_Report.xls";		
			isDataAvailable = reportService.generateCodersQualityReports(requestParam, excelFileName);
			
			if(isDataAvailable) {
				final String relativePath = requestParam.getRelativePath();
		    	// Create an output byte stream where data will be written
				File file = new File(relativePath);
				reader = new FileInputStream(file);
				long length = file.length();
	            byte[] bytes = new byte[(int) length];

	            reader.read(bytes);

	            baos.write(bytes);
				response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
				
				// Set content type
				response.setContentType("application/xlsx");
				response.setContentLength(baos.size());
				
				write(response, baos);
			} else 
				isDataAvailable = false;
		    
	    }  catch (final Exception e) {
	       logger.error("Exception while downloading Coder Quality reports : ", e);
	       isDataAvailable = false;
	    }
		return isDataAvailable;
	}
	/**
	 * 
	 * @param response
	 * @param requestParam
	 */
	public boolean downloadMiscellaneousReport( final HttpServletResponse response, ReportQueryParam requestParam ) {
		boolean isDataAvailable = false;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream reader = null;
		try {
			final String excelFileName = "Miscellaneous_Report.xlsx";		
			isDataAvailable = reportService.generateMiscellaneousReport(requestParam, excelFileName);
			
			if(isDataAvailable) {
				final String relativePath = requestParam.getRelativePath();
		    	// Create an output byte stream where data will be written
				File file = new File(relativePath);
				reader = new FileInputStream(file);
				long length = file.length();
	            byte[] bytes = new byte[(int) length];
	            reader.read(bytes);
	            baos.write(bytes);
				response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
				
				// Set content type
				response.setContentType("application/xlsx");
				response.setContentLength(baos.size());
				
				write(response, baos);
			} else 
				isDataAvailable = false;
		    
	    }  catch (final Exception e) {
	       logger.error("Exception while downloading Coder Quality reports : ", e);
	       isDataAvailable = false;
	    }
		return isDataAvailable;
	}
	
	/**
     * Compress the given directory with all its files.
     */
    private byte[] zipFiles(File directory, String[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];

        for (String fileName : files) {
            FileInputStream fis = new FileInputStream(directory.getPath() + 
                FILE_SEPARATOR + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);

            zos.putNextEntry(new ZipEntry(fileName));

            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
    }
    
	/**
	* Writes the report to the output stream
	*/
	private void write(HttpServletResponse response,
			ByteArrayOutputStream baos) {
		 
		try {
			logger.debug("ByteArrayOutputStream : " +baos.size());
			
			// Retrieve output stream
			ServletOutputStream outputStream = response.getOutputStream();
			// Write to output stream
			baos.writeTo(outputStream);
			// Flush the stream
			outputStream.flush();
			
		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
			throw new RuntimeException(e);
		}
	}
	
	private void copyTemplateToRalatinvePath(String sourcePath, String desPath, String clientName) {
		InputStream inStream = null;
		OutputStream outStream = null;
    	try {
 
    		if(clientName.equalsIgnoreCase("MCCG")){
    			sourcePath = sourcePath+TEMPLATE_INVOICE_MCCG;
    			desPath = desPath+TEMPLATE_INVOICE_MCCG;
    		} else if(clientName.equalsIgnoreCase("ORHS")){
    			sourcePath = sourcePath+TEMPLATE_INVOICE_ORHS;
    			desPath = desPath+TEMPLATE_INVOICE_ORHS;
    		} else if(clientName.equalsIgnoreCase("HC")){
    			sourcePath = sourcePath+TEMPLATE_INVOICE_HC;
    			desPath = desPath+TEMPLATE_INVOICE_HC;
    		} else if(clientName.equalsIgnoreCase("PC")){
    			sourcePath = sourcePath+TEMPLATE_INVOICE_PC;
    			desPath = desPath+TEMPLATE_INVOICE_PC;
    		}
    	    File afile =new File(sourcePath);
    	    File bfile =new File(desPath);
 
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
    	    byte[] buffer = new byte[(int) afile.length()];
    	    inStream.read(buffer); 
    	    outStream.write(buffer);
     	    	 
    	    inStream.close();
    	    outStream.close();
    	    
    	    logger.debug("Template File is copied successful!");
 
    	} catch(IOException e){
    	    e.printStackTrace();
    	}
	}

}
