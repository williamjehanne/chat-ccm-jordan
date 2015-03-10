package com.ccm.chat.server;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MailServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		try{
			MimeMessage message = new MimeMessage( session, req.getInputStream());
			String from = message.getFrom()[0].toString();
		}catch(MessagingException e){

		}
	}
	
}
