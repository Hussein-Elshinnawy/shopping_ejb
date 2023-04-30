package shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.persistence.*;
import jakarta.transaction.*;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CustomerResource {


    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");

    private final EntityManager entityManager = emf.createEntityManager();

    @Resource
    private UserTransaction userTransaction;

    @EJB
    OrderManager orderManager;


    @GET
    @Path("/welcome")

    public String welcomeMessage() {
        return "Hello Customer!";
    }

    @POST
    @Path("/register")
    public String registerCustomer(Customer customer) {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.persist(customer);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Customer registered successfully!\n" + "Welcome Customer: " + customer.getName();
    }

    @POST
    @Path("/login")
    public String loginCustomer(Customer customer) {
        Customer customerFromDB = entityManager.find(Customer.class, customer.getUsername());
        if (customerFromDB.getPassword().equals(customer.getPassword())) {
            return "Customer logged in successfully!";
        } else {
            return "Customer login failed!";
        }
    }

    // method to get all customers
    @GET
    @Path("/getAllCustomers")
    public List<Customer> getAllCustomers() {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    @GET
    @Path("/{username}")
    public Customer getCustomerByUsername(@PathParam("username") String username) {
        Customer customer = entityManager.find(Customer.class, username);
        return customer;
    }

    @PUT
    @Path("/updateCustomer/{username}")
    public String updateCustomer(@PathParam("username") String username, Customer customer) {

        Customer customerFromDB = entityManager.find(Customer.class, username);
        customerFromDB.setName(customer.getName());
        customerFromDB.setEmail(customer.getEmail());
        customerFromDB.setPassword(customer.getPassword());
        customerFromDB.setAddress(customer.getAddress());
        customerFromDB.setPhone(customer.getPhone());

//        return entityManager.merge(customerFromDB);
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.merge(customerFromDB);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Customer updated successfully!";

    }

    @DELETE
    @Path("/deleteCustomer/{username}")
    public String deleteCustomer(@PathParam("username") String username) {
        Customer customer = entityManager.find(Customer.class, username);
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        entityManager.remove(customer);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "Customer deleted successfully!";
    }


//    @POST
//    @Path("/{username}/purchase/{region}")
//    public String makePurchaseOrder(@PathParam("username") String username, Order order, @PathParam("region") String region) {
//        Customer customer = entityManager.find(Customer.class, username);
//        String queryString = "SELECT sc FROM ShippingCompany sc JOIN sc.coveredRegions cr WHERE cr.region = :region";
//        TypedQuery<ShippingCompany> query = entityManager.createQuery(queryString, ShippingCompany.class);
//        query.setParameter("region", region);
//        if (customer == null) {
//            return "Customer not found!";
//        }
//        if (!customer.getAddress().equals(region)) {
//            return "Customer address does not match the specified region!";
//        }
//
//        // Retrieve the shipping company that covers the provided region
//        List<ShippingCompany> shippingCompanies = query.getResultList();
//        if (shippingCompanies.isEmpty()) {
//            return "Shipping company not found for region: " + region;
//        }
//        ShippingCompany shippingCompany = shippingCompanies.get(0);
//
//        // Check that the customer's address falls within the covered regions of the selected shipping company
//        boolean addressCovered = false;
//        for (CoveredRegion coveredRegion : shippingCompany.getCoveredRegions()) {
//            if (coveredRegion.getRegion().equals(customer.getAddress())) {
//                addressCovered = true;
//                break;
//            }
//        }
//        if (!addressCovered) {
//            return "Customer address is not covered by the selected shipping company for region: " + region;
//        }
//
//        // Create a new order for the customer
//        order.setCustomer(customer);
//        order.setShippingCompany(shippingCompany);
//        // Add the items to the order
//        double totalAmount = 0;
//        List<Product> products = new ArrayList<>();
//        String itemName = order.getItems();
//        Product product = entityManager.createQuery("SELECT p FROM Product p WHERE p.productName = :name", Product.class)
//                .setParameter("name", itemName)
//                .getSingleResult();
//        if (product == null) {
//            return "Product not found: " + itemName;
//        }
//        products.add(product);
//        totalAmount += product.getProductPrice();
//
//        order.setProducts(products);
//        order.setTotal(totalAmount);
//
//        // Persist the order
//        try {
//            userTransaction.begin();
//            entityManager.persist(order);
//            userTransaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred while committing transaction!";
//        }
//
//        return "Order successfully placed!";
//    }


    @POST
    @Path("/{username}/makeCart")
    public int makeCart(@PathParam("username") String username/*, @PathParam("region") String region*/) {
        Order order = new Order();
        Customer customer = entityManager.find(Customer.class, username);
        String region = customer.getAddress();
        String queryString = "SELECT sc FROM ShippingCompany sc JOIN sc.coveredRegions cr WHERE cr.region = :region";
        TypedQuery<ShippingCompany> query = entityManager.createQuery(queryString, ShippingCompany.class);
        query.setParameter("region", region);
        if (customer == null) {
            return 0;
            //return "Customer not found!";
        }
        if (!customer.getAddress().equals(region)) {
            return 0;
            //return "Customer address does not match the specified region!";
        }

        // Retrieve the shipping company that covers the provided region
        List<ShippingCompany> shippingCompanies = query.getResultList();
        if (shippingCompanies.isEmpty()) {
            return 0;
           // return "Shipping company not found for region: " + region;
        }
        ShippingCompany shippingCompany = shippingCompanies.get(0);

        // Create a new order for the customer
        order.setCustomer(customer);
        order.setShippingCompany(shippingCompany);

        // Add the items to the order
//        List<Product> products = new ArrayList<>();
//        String itemName = order.getItems();
//        try {
//            Product product = entityManager.createQuery("SELECT p FROM Product p WHERE p.productName = :name", Product.class)
//                    .setParameter("name", itemName)
//                    .getSingleResult();
//            products.add(product);
//            double totalAmount = 0;
//            totalAmount += product.getProductPrice();
//
//            //order.setProducts(products);
//            order.setTotal(totalAmount);
//
//
//        } catch (NoResultException e) {
//            return "Product not found";

        // Persist the order
        try {
            userTransaction.begin();
            entityManager.persist(order);
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
            //return "Error occurred while committing transaction!";
        }
        return order.getOrderId();
        //return "Order successfully added!";

    }
    @GET
    @Path("/{customer_username}/orders")public List<Order> getCurrentOrders(@PathParam("customer_username") String customer_username) {
        List<Order> orders = new ArrayList<>();
        try {
            // Create a JPQL query to retrieve the orders for the specified customer where bought = 0
            String jpql = "SELECT o FROM Order o WHERE o.customer.username = :username AND o.bought = false";
            TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
            query.setParameter("username", customer_username);
            orders = query.getResultList();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    @GET
    @Path("/{customer_username}/history")
    public List<Order> getPurchasedOrders(@PathParam("customer_username") String customer_username) {
        List<Order> orders = new ArrayList<>();
        try {
            // Create a JPQL query to retrieve the orders for the specified customer where bought = 0
            String jpql = "SELECT o FROM Order o WHERE o.customer.username = :username AND o.bought = true";
            TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
            query.setParameter("username", customer_username);
            orders = query.getResultList();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    //    @POST
//    @Path("/{orderId}/addToCart/{productName}/{quantity}")
//    public String addToCart(@PathParam("orderId") int orderId, @PathParam("productName") String productName, @PathParam("quantity") int quantity) {
//        // Find the product entity
//        System.out.println("addOrderedItem");
//
//        String jpql = "SELECT p FROM Product p WHERE p.productName = :productName AND p.available = true";
//        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
//        query.setParameter("productName", productName);
//        try {
//            Product product = query.getSingleResult();
//
//
//            //Product product = entityManager.find(Product.class, productId);
////        if (product == null) {
////            return "Product not found!";
////        }
//
//
//            // Find the order entity
//            Order order = entityManager.find(Order.class, orderId);
//            if (order == null) {
//                return "Order not found!";
//            }
//
//            // Create the OrderedItem entity
//            OrderedItem orderedItem = new OrderedItem(order, product, quantity);
//
//            // Persist the OrderedItem entity
//            try {
//                userTransaction.begin();
//                entityManager.persist(orderedItem);
//                userTransaction.commit();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "Error occurred while committing transaction!";
//            }
//        }
//        catch (NoResultException e) {
//            return "Product not found";
//        }
//        return "Ordered item successfully added!";
//    }
    @POST
    @Path("/{orderId}/addToCart/{productName}")
    public String addToCart(@PathParam("orderId") int orderId, @PathParam("productName") String productName, AddToCartRequest request) {
        int quantity = request.getQuantity();
        return orderManager.addToCart(orderId, productName, quantity);
    }


    @PUT
    @Path("/{orderId}/purchase")
    public String purchase(@PathParam("orderId") int orderId) {
        try {
            entityManager.getTransaction().begin();
            Query query = entityManager.createQuery("UPDATE Order o SET o.bought = true WHERE o.orderId = :orderId");
            query.setParameter("orderId", orderId);
            int updatedCount = query.executeUpdate();
            entityManager.getTransaction().commit();
            return "order " + orderId + " purchased";
        } catch (NoResultException e) {
            return "no such order";
        }
    }


}

