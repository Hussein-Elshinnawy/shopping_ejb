package shop;

import jakarta.annotation.Resource;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import jakarta.transaction.*;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
//
//import javax.jms.*;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.IllegalStateException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");

    private final EntityManager entityManager = emf.createEntityManager();

    @Resource
    private UserTransaction userTransaction;


    private ShippingCompanyResource companyResource;


    private CoveredRegionResource regionResource;

    @EJB
    private SellingCompanyCreationBean sellingCompanyCreationBean;


    @GET
    @Path("/welcome")
    public String welcomeMessage() {
        return "Hello Customer!";
    }

    @POST
    @Path("/register")
    public String registerAdmin(Admin admin) {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.persist(admin);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Admin registered successfully!\n" + "Welcome Admin: " + admin.getName();
    }

    @POST
    @Path("/login")
    public String loginCustomer(Admin admin) {
        Admin adminFromDB = entityManager.find(Admin.class, admin.getUsername());
        if (adminFromDB.getPassword().equals(admin.getPassword())) {
            return "Admin logged in successfully!";
        } else {
            return "Admin login failed!";
        }
    }

    // method to get all customers
    @GET
    @Path("/getAllCustomers")
    public List<Customer> getAllCustomers() {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    @GET
    @Path("/getAllAdmins")
    public List<Admin> getAllAdmins() {
        return entityManager.createQuery("SELECT c FROM Admin c", Admin.class).getResultList();
    }

    @GET
    @Path("/{username}")
    public Admin getAdminByUsername(@PathParam("username") String username) {
        Admin admin = entityManager.find(Admin.class, username);
        return admin;
    }

    @PUT
    @Path("/updateAdmin/{username}")
    public String updateAdmin(@PathParam("username") String username, Admin admin) {

        Admin adminFromDB = entityManager.find(Admin.class, username);
        adminFromDB.setName(admin.getName());
        adminFromDB.setEmail(admin.getEmail());
        adminFromDB.setPassword(admin.getPassword());
        adminFromDB.setAddress(admin.getAddress());
        adminFromDB.setPhone(admin.getPhone());


        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.merge(adminFromDB);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Admin updated successfully!";

    }

    @DELETE
    @Path("/deleteAdmin/{username}")
    public String deleteAdmin(@PathParam("username") String username) {
        Admin admin = entityManager.find(Admin.class, username);
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.remove(admin);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Admin deleted successfully!";
    }


    @POST
    @Path("/createSellingCompany")
    public String createSellingCompany(SellingCompanyRep s) {
        sellingCompanyCreationBean.createSellingCompany(s);
        String password = sellingCompanyCreationBean.generatePassword();
        return "Selling company created successfully!\n" + "Welcome " + s.getName() + ", your password is " + s.getPassword();
    }


    @GET
    @Path("/getAllSellingCompanyRep")
    public List<SellingCompanyRep> getAllSellingCompanyRep() {
        return entityManager.createQuery("SELECT s FROM SellingCompanyRep s", SellingCompanyRep.class).getResultList();
    }

    @POST
    @Path("/createShippingCompany")
    public String createShippingCompany(ShippingCompany company) {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.persist(company);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException |
                 SystemException e) {
            e.printStackTrace();
        }
        return "shipping company created successfully!\n" + " welcome " + company.getName() + "your company id is" + company.getId();
    }

    @POST
    @Path("/createCoveredRegion")
    public String createCoveredRegion(CoveredRegion region) {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.persist(region);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException |
                 SystemException e) {
            e.printStackTrace();
        }
        return "shipping company created successfully!\n" + " welcome " + region.getRegion() + "your company id is";
    }

    @POST
    @Path("/addCompaniesToRegions")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addCompaniesToRegions(List<Map<String, Integer>> data) {
        try {
            userTransaction.begin();

            for (Map<String, Integer> item : data) {
                int companyId = item.get("companyId");
                int regionId = item.get("regionId");

                ShippingCompany company = entityManager.find(ShippingCompany.class, companyId);
                CoveredRegion region = entityManager.find(CoveredRegion.class, regionId);

                if (company != null && region != null) {
                    region.getShippingCompanies().add(company);
                    company.getCoveredRegions().add(region);
                    entityManager.persist(region);
                    entityManager.persist(company);
                }
            }

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException |
                 HeuristicMixedException | HeuristicRollbackException e) {
            throw new WebApplicationException("Error adding companies to regions", e, 500);
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Path("/getAllShippingCompany")
    public List<ShippingCompany> getAllShippingCompany() {
        return entityManager.createQuery("SELECT c FROM ShippingCompany c", ShippingCompany.class).getResultList();
    }

    @GET
    @Path("/getShippingRequest/{companyId}")
    public List<Order> getShippingRequest(@PathParam("companyId") int companyId) {
        ShippingCompany company = entityManager.find(ShippingCompany.class, companyId);
        System.out.println("please" + company.toString());
        String jpql = "SELECT o FROM Order o WHERE o.shippingCompany = :company";
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        query.setParameter("company", company);
        List<Order> orders = query.getResultList();
        return orders;
    }

    //    @GET
//    @Path("/{companyId}/processOrder/{orderId}")
//    public String getOrdersByCompanyAndId(
//            @PathParam("companyId") int companyId,
//            @PathParam("orderId") int orderId) {
//
//        ShippingCompany company = entityManager.find(ShippingCompany.class, companyId);
//        String jpql = "SELECT o FROM Order o WHERE o.shippingCompany = :company AND o.orderId = :orderId";
//        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
//        query.setParameter("company", company);
//        query.setParameter("orderId", orderId);
//        List<Order> orders = query.getResultList();
//
//        if (orders.isEmpty()) {
//            return "Error in processing order.";
//        }
//        // create a JMS message with order and customer information
//        MapMessage message = session.createMapMessage();
//        message.setLong("orderId", orderId);
//        message.setString("customerEmail", customerEmail);
//        message.setString("customerName", customerName);
//
//// send the JMS message to the ShippingRequestNotificationQueue
//        Queue queue = (Queue) initialContext.lookup("jms/ShippingRequestNotificationQueue");
//        MessageProducer producer = session.createProducer(queue);
//        producer.send(message);
//        return "order "+orderId+" was successful";
//        // process the orders list and return a response
//        // ...
//    }
    @PUT
    @Path("/{companyId}/processOrder/{orderId}")
    public String getOrdersByCompanyAndId(
            @PathParam("companyId") int companyId,
            @PathParam("orderId") int orderId) throws JMSException, NamingException, NotSupportedException, SystemException {

        // Obtain a reference to the JMS session

        ShippingCompany company = entityManager.find(ShippingCompany.class, companyId);
        String jpql = "SELECT o FROM Order o WHERE o.shippingCompany = :company AND o.orderId = :orderId";
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        query.setParameter("company", company);
        query.setParameter("orderId", orderId);
        List<Order> orders = query.getResultList();

        if (orders.isEmpty()) {
            return "Error in processing order.";
        }
        // create a JMS message with order and customer information
        InitialContext context = new InitialContext();
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = (Queue) context.lookup("java:/jms/queue/ShippingRequestNotificationQueue");
        //Notification notification = new Notification(customer.getUsername()+" Your order numbered" + Integer.toString(order.getOrder_id())+" is Processing");
        MapMessage message = session.createMapMessage();
        message.setString("customerName", orders.get(0).getCustomer().getUsername());

        message.setString("message", orders.get(0).getCustomer().getMessage());
        MessageProducer producer = session.createProducer(queue);
        producer.send(message);
        orders.get(0).getCustomer().setMessage("your order "+ orderId +" is proessing");
        String username=orders.get(0).getCustomer().getUsername();
        System.out.println("message for username "+ username );
        orders.get(0).getCustomer().setMessage("ORDER PROCCESSED");
        String msg=orders.get(0).getCustomer().getMessage();
        System.out.println(msg);
        entityManager.getTransaction().begin();
        Query q = entityManager.createQuery( "UPDATE Customer e SET e.message = :msg WHERE e.username = :username");
        q.setParameter("msg", msg);
        q.setParameter("username", username);
        int updatedCount = q.executeUpdate();
        session.close();
        connection.close();
//        MapMessage message = session.createMapMessage();
//        message.setLong("orderId", orderId);
//        message.setString("customerEmail", orders.get(0).getCustomer().getEmail());
//        message.setString("customerName", orders.get(0).getCustomer().getName());
//
//        // send the JMS message to the ShippingRequestNotificationQueue
//        MessageProducer producer = session.createProducer(queue);
//        producer.send(message);
//
//        // Close the JMS session and connection
//        session.close();
//        connection.close();

        return "order " + orderId + " was successful";
        // process the orders list and return a response
        // ...
    }

}


