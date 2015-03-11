package com.ccm.chat.server;
 
import java.util.List;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
 


import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
 
public class WelcomeServlet extends HttpServlet{
       
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
 
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                        throws ServletException, IOException {
                // TODO Auto-generated method stub
                // super.doGet(req, resp);
               
                resp.setContentType("text/html");
               
                UserService userService = UserServiceFactory.getUserService();
                User user = userService.getCurrentUser();
                if(user==null){
                        String urlToLogin = userService.createLoginURL("/chat_ccm_jordan/welcome");
                        resp.getWriter().println("Bonjour !");
                        resp.getWriter().println(" <br/>Pour vous connecter veuillez suivre cette URL : <a href='"+urlToLogin+"'> Me connecter </a>");
                }else{
                        String urlToLogout = userService.createLogoutURL("/chat_ccm_jordan/welcome");
                        resp.getWriter().println("Bienvenue "+user.getNickname()+", "+"votre email est "+user.getEmail());
                        resp.getWriter().println(" <br/><br/><a href='"+urlToLogout+"'> Me déconnecter </a>");
                       
                        Properties props = new Properties();
                        Session session = Session.getDefaultInstance(props, null);
                        String msgBody = "...";
                       
                        Key userKey = KeyFactory.createKey("Registred", user.getEmail());
                    Entity userEntity;
                    try{
                      userEntity = datastore.get(userKey);
                    }catch(EntityNotFoundException e){
                      // Create a RegisteredUser entity, specifying the mainEmail to use in the key
                      userEntity = new Entity("RegisteredUser",user.getEmail());
                    }
                   
                    // Set properties on the RegisteredUser entity
                    // Set the properties:
                    userEntity.setProperty("mainMail", user.getEmail());
 
                    // Save the entity in the datastore
                    datastore.put(userEntity);
 
                        try{
                                Message msg = new MimeMessage(session);
                                msg.setFrom(new InternetAddress("user@example.com","Mr. User"));
                                msg.addRecipient(Message.RecipientType.TO,new InternetAddress("test@ccm-chat-jordan.appspotmail.com"));
                                msg.setSubject("Your Example.com account has been activated");
                                msg.setText(msgBody);
                                Transport.send(msg);
                        }catch(Exception e){
                               
                        }
                       
                       
                         resp.getWriter().println("<form enctype='multipart/form-data' method='post' action='"+BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/upload")+"'>");
                         resp.getWriter().println("<input type='file' name='file' size='30' />");
                         resp.getWriter().println("<input type='submit' /></form>");
                       
                        Query q = new Query("RegisteredUser");
                PreparedQuery pq = datastore.prepare(q);
                resp.getWriter().println("<h3>Liste des utilisateurs</h3>");
                resp.getWriter().println("<ul>");
                for (Entity result : pq.asIterable()) {
                        resp.getWriter().println("<li>");
                        resp.getWriter().println(result.getProperty("mainMail"));
                        resp.getWriter().println("</li>");
                }
                resp.getWriter().println("</ul>");
                       
                }
                       
        }
        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
            List<BlobKey> blobKeys = blobs.get("file");

            //res.getWriter().println(blobKeys);
            		
            if (blobKeys == null || blobKeys.isEmpty()) {
                res.sendRedirect("/");
            } else {
            	blobstoreService.serve(new BlobKey(blobKeys.get(0).getKeyString()), res);
            }
        }
 
}