package Controller.Customer;

import Service.CustomerService;
import Service.impl.CustomerServiceImpl;
import model.CustomerEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/account", name = "AccountController")
public class AccountController extends HttpServlet {
    CustomerService customerService = new CustomerServiceImpl();
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SQL");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String url = "/editcustomerin4.jsp";
        String action = request.getParameter("action");

        //      <--- Edit Info --->
        if (action.equals("edit")){
            url = edit(request,response);
        }
        else if (action.equals("changepass")){
            url = changepass(request,response);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    protected String edit (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String url;
        HttpSession session = request.getSession();
        CustomerEntity cus1 = (CustomerEntity) session.getAttribute("user");

        try {
            transaction.begin();
            String firstname = request.getParameter("fname");
            String lastname = request.getParameter("lname");
            String province = request.getParameter("province");
            String district = request.getParameter("district");
            String ward = request.getParameter("ward");
            String street = request.getParameter("street");
            String phonenumber = request.getParameter("phonenumber");

            String address = street + ", " + ward + ", " + district + ", " + province;


            cus1.setCustomerFName(firstname);
            cus1.setCustomerLName(lastname);
            cus1.setCustomerAddress(address);
            cus1.setCustomerPhone(phonenumber);

            entityManager.merge(cus1);
            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
                url = "/editcustomerin4.jsp";
            } else {
                url = "/customer-account.jsp";
            }
            entityManager.close();
            entityManagerFactory.close();
        }
        return url;
    }

    protected String changepass (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String url;
        String message = null;
        String email = request.getParameter("email");
        CustomerEntity account = customerService.findByEmail(email);


        String password = request.getParameter("pwd");
        String repassword = request.getParameter("repwd");

        if (password.equals(repassword)) {
            try {
                transaction.begin();
                account.setCustomerPwd(password);
                entityManager.merge(account);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                    url = "/changepass.jsp";
                } else {
                    url = "/Home.jsp";
                }
                entityManager.close();
                entityManagerFactory.close();
            }
        }
        else {
            message = "Sorry, Please ReEnter true password!";
            url = "/changepass.jsp";
        }
        request.setAttribute("message", message);
        request.setAttribute("useremail", email);
        return url;
    }
}
