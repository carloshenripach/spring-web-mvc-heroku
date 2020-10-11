package br.com.springboot.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable{	
	/*Retorna nosso PDF em Byte para download no navegador*/
	public byte[] gerarRelatorio(List listDados, String relatorio, 
								ServletContext servletContext) throws JRException{
		/*Cria a lista de dados para o relatorio com nossa lista de objetos para imprimir*/
		JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listDados);
		
		/*Carrega o caminho do arquivo jasper compilado*/
		String caminhoJasper = servletContext.getRealPath("relatorios")
								+ File.separator + relatorio + ".jasper";
		/*Carrega o arquivo Jasper passando os dados*/
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper,new HashMap(), jrBeanCollectionDataSource);
		/*Exporta para byte fazer download*/
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}
