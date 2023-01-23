package com.wilProject.tiendaMusicalReportes.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;

import com.wilProject.tiendaMusicalReportes.services.MailService;

@Service
public class MailServiceImpl implements MailService {
	
	@Value("${spring.mail.aws.smpt.user}")
	String user;
	
	@Value("${spring.mail.aws.smpt.password}")
	String password;
	
	@Value("${spring.mail.aws.smpt.port}")
	String port;
	
	@Value("${spring.mail.aws.smpt.sender}")
	String sender;
	
	@Value("${spring.mail.aws.smpt.starttls.enable}")
	String starttls;
	
	@Value("${spring.mail.aws.smpt.host}")
	String host;
	
	@Value("${spring.dropbox.directorio.reportes}")
	String pathReportesDropbox;

	@Override
	public Response enviarEmail(DbxClientV2 dbxClientV2, String destinatario, String cliente, String orderID) {
		
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", this.host);
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", starttls);
		properties.put("mail.smtp.port", this.port);
		
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		
		
		//Creando estructura del correo
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(this.sender));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setSubject("Compra realizada exitósamente - " + orderID);
			
			ByteArrayOutputStream archivoByte = new ByteArrayOutputStream();
			
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(this.pathReportesDropbox + "/" + cliente + "/" + orderID + ".pdf");
			downloader.download(archivoByte);
			
			BodyPart bodyPartText = new MimeBodyPart();
			bodyPartText.setText("Has realizado tu compra de manera exitosa, adjunto a este correo podrás encontrar tu comprobante de pago");
			
			byte[] bytes = archivoByte.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			ByteArrayDataSource bads = new ByteArrayDataSource(inputStream, "application/pdf");
			
			BodyPart bodyPartFile = new MimeBodyPart();
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPartText);
			bodyPartFile.setDataHandler(new DataHandler(bads));
			bodyPartFile.setFileName("COMPROBANTE-" + orderID + ".pdf");
			multipart.addBodyPart(bodyPartFile);
			
			message.setContent(multipart);
			
			AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
			
			PrintStream printStream = System.out;
			message.writeTo(printStream);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
			
			SendRawEmailRequest sendRawEmailRequest = new SendRawEmailRequest(rawMessage);
			
			ses.sendRawEmail(sendRawEmailRequest);
			
			return Response.ok().build();
			
		} catch (AddressException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DownloadErrorException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();}
	}

}
