package shop;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.*;
import jakarta.transaction.NotSupportedException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/sellingCompanyRep")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellingCompanyRepResource {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");

    private final EntityManager entityManager = emf.createEntityManager();

    @Resource
    private UserTransaction userTransaction;
    @EJB
    private SellingCompanyCreationBean sellingCompanyCreationBean;

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
        return entityManager.createQuery("SELECT c FROM SellingCompanyRep c", SellingCompanyRep.class).getResultList();
    }

    @POST
    @Path("/login")
    public String loginSellingCompany(SellingCompanyRep sellingCompanyRep) {
        SellingCompanyRep sellingCompanyFromDB = entityManager.find(SellingCompanyRep.class, sellingCompanyRep.getName());
        if (sellingCompanyFromDB.getPassword().equals(sellingCompanyRep.getPassword())) {
            return "SellingCompanyRep logged in successfully!";
        } else {
            return "SellingCompanyRep login failed!";
        }
    }

    @POST
    @Path("/createProduct/{name}")
    public String createProduct(@PathParam("name") String name, Product product) {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
        SellingCompanyRep sellingCompanyRep = entityManager.find(SellingCompanyRep.class, name);
        if (sellingCompanyRep == null) {
            return "selling company not found";
        }
        product.setSellingCompanyRep(sellingCompanyRep);
        entityManager.persist(product);
        try {
            userTransaction.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                 IllegalStateException | SystemException e) {
            e.printStackTrace();
        }
        return "product is created  successfully!\n" + "product: " + product.getProductName();
    }

    @GET
    @Path("/getAllProducts/{name}")
    public List<Product> getAllProducts(@PathParam("name") String name) {
        SellingCompanyRep sellingCompanyRep = entityManager.find(SellingCompanyRep.class, name);
        if (sellingCompanyRep == null) {
            return null;
        }
        List<Product> products = sellingCompanyRep.getProducts();
        return products;
        //return entityManager.createQuery("SELECT a FROM Product a where a.sellingCompanyRep = :name", Product.class).getResultList();
    }

    @GET
    @Path("/getAvailableProducts")
    public List<Product> getAvailableProducts() {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.available = true", Product.class).getResultList();
    }

    //    @GET
//    @Path("/getSoldProducts/{name}")
//    public List<Order> getSoldProducts(@PathParam("name") String name) {
//        SellingCompanyRep sellingCompanyRep = entityManager.find(SellingCompanyRep.class, name);
//        if (sellingCompanyRep == null) {
//            return null;
//        }
//        return entityManager.createQuery("SELECT o FROM Order o JOIN o.products p JOIN o.customer c JOIN p.sellingCompanyRep s JOIN o.shippingCompany sc WHERE s = :sellingCompanyRep", Order.class)
//                .setParameter("sellingCompanyRep", sellingCompanyRep)
//                .getResultList();
//    }
//    @GET
//    @Path("/getSoldProducts/{name}")
//    public List<Object[]> getSoldProducts(@PathParam("name") String name) {
//        TypedQuery<Object[]> query = entityManager.createQuery("SELECT o.orderId, c , p, s FROM Order o JOIN o.customer c JOIN o.products p JOIN p.sellingCompanyRep sc JOIN o.shippingCompany s WHERE sc.name = :sellingCompanyRepName", Object[].class);
//        query.setParameter("sellingCompanyRepName", name);
//        return query.getResultList();
//    }
//    @GET
//    @Path("/getSoldProducts/{name}")
//    public List<Product> getSoldProducts(@PathParam("name") String name) {
//        TypedQuery<Product> query = entityManager.createQuery(
//                "SELECT DISTINCT p " +
//                        "FROM Product p " +
//                        "JOIN p.orders o " +
//                        "JOIN o.customer c " +
//                        "WHERE p.available = true " +
//                        "AND o.bought = true " +
//                        "AND p.sellingCompanyRep.name = :sellingCompanyRepName", Product.class);
//        query.setParameter("sellingCompanyRepName", name);
//        return query.getResultList();
//    }
    @GET
    @Path("/getSoldProducts/{name}")
    public List<Object[]> getSoldProducts(@PathParam("name") String name) {
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT o.id, p, o.customer, o.shippingCompany " +
                        "FROM Product p " +
                        "JOIN p.orders o " +
                        "WHERE p.available = true " +
                        "AND o.bought = true " +
                        "AND p.sellingCompanyRep.name = :sellingCompanyRepName", Object[].class);
        query.setParameter("sellingCompanyRepName", name);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

//    @GET
//    @Path("/getSoldProducts/{name}")
//    public List<Object[]> getSoldProducts(@PathParam("name") String name) {
//
//        TypedQuery<Object[]> query = entityManager.createQuery(
//                "SELECT p.productName, SUM(oi.quantity), SUM(oi.quantity * p.productPrice) " +
//                        "FROM Product p " +
//                        "JOIN OrderedItem oi ON oi.productId = p.productId " +
//                        "JOIN Order o ON o.orderId = oi.orderId AND o.bought = true " +
//                        "JOIN SellingCompanyRep r ON r.name = p.name AND r.name = :name " +
//                        "GROUP BY p.productName",
//                Object[].class);
//        query.setParameter("name", name);
//        List<Object[]> resultList = query.getResultList();
//        return resultList;
//    }


}