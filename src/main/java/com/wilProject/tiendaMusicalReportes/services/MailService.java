package com.wilProject.tiendaMusicalReportes.services;

import javax.ws.rs.core.Response;

import com.dropbox.core.v2.DbxClientV2;

public interface MailService {

	public Response enviarEmail(DbxClientV2 dbxClientV2, String destinatario, String cliente, String orderID);
}
