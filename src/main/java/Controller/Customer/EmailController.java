package Controller.Customer;

import Service.CustomerService;
import Service.impl.CustomerServiceImpl;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/email", name = "EmailServlet")
public class EmailController extends HttpServlet {
    CustomerService customerService = new CustomerServiceImpl();
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SQL");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String url = "/changepass.html";
        String action = request.getParameter("action");

        if (action.equals("forgetpass")){
            forgetpass(request, response, url);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
    protected void forgetpass (HttpServletRequest request, HttpServletResponse response, String url)
            throws ServletException, IOException{
        String useremail = request.getParameter("email");

        String to = useremail;
        String from = "coza@store.com";
        String subject = "Coza - Customer account confirmation";
        String body =
                "<h1 style=\"color: #633b00\">Coza</h1>\n" +
                        "<h2 style=\"color: #633b00\">Welcome to Coza!</h2>\n" +
                        "<p>Congratulations, you have successfully activated your customer account. Next time you make a purchase, please log in to make payment more convenient. Come to our store\n" +
                        "</p>\n" +
                        "<button style=\"background-color: #d7ffef; width: 200px; height: 100px; border-radius: 10px\">\n" +
                        "<a href=\"http://localhost:8080/demo4_war_exploded/\" style=\"color: #1c1e28\">Go To Our Store</a>\n" +
                        "</button>" +
                        "<p>If you have any questions, don't hesitate to contact us at:\n" +
                        "\t<a href=\"mailto:quangcuatuonglai@gmail.com\" style=\"font-size:14px;text-decoration:none;color:#1666a2\" target=\"_blank\">quangcuatuonglai@gmail.com</a></p>";

        boolean isBodyHTML = true;
        try {
            util.MailUtilGmail.sendMail(to, from, subject, body,
                    isBodyHTML);
        } catch (MessagingException e) {
            String errorMessage
                    = "ERROR: Unable to send email. "
                    + "Check Tomcat logs for details.<br>"
                    + "NOTE: You may need to configure your system "
                    + "as described in chapter 14.<br>"
                    + "ERROR MESSAGE: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            this.log(
                    "Unable to send email. \n"
                            + "Here is the email you tried to send: \n"
                            + "=====================================\n"
                            + "TO: " + useremail + "\n"
                            + "FROM: " + from + "\n"
                            + "SUBJECT: " + subject + "\n\n"
                            + body + "\n\n");
        }
        url = "/Home.jsp";
    }
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
