package com.wilProject.tiendaMusicalReportes.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wilProject.tiendaMusicalReportes.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class JasperReportsServiceImpl implements JasperReportsService {
	
	@Value("${spring.datasource.driverClassName}")
	String driver;
	
	@Value("${spring.datasource.url}")
	String url;
	
	@Value("${spring.datasource.username}")
	String user;
	
	@Value("${spring.datasource.password}")
	String password;

	@Override
	public JasperPrint compilarReportesJasper(ByteArrayOutputStream archivoBytes, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException {
		
		InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/musicR.png");
		
		Map<String, Object> map = new HashMap<>();
		map.put("orderID", orderID);
		map.put("logo", imageInputStream);
		
		byte[] bytes = archivoBytes.toByteArray();
		InputStream archivoInputStream = new ByteArrayInputStream(bytes);
		
		Class.forName(this.driver);
		Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(archivoInputStream);
		
		imageInputStream.close();
		archivoInputStream.close();
		
		return JasperFillManager.fillReport(jasperReport, map, connection);
	}

}
