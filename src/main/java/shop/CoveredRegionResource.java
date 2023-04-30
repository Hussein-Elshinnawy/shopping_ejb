package shop;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;
import jakarta.transaction.NotSupportedException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/coveredregion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoveredRegionResource {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql");
    private final EntityManager entityManager = emf.createEntityManager();
    @Resource
    private UserTransaction userTransaction;

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
        return "shipping company created successfully!\n" + " welcome " + region.getRegion() + "your company id is" ;
    }
    @PUT
    @Path("/updateCoveredRegion/{id}")
    public String updateCoveredRegion(@PathParam("id") int regionId, CoveredRegion region) {
        try {
            CoveredRegion coveredRegionFromDB = entityManager.find(CoveredRegion.class, regionId);
            coveredRegionFromDB.setRegion(region.getRegion());

            try {
                userTransaction.begin();
            } catch (NotSupportedException | SystemException e) {
                throw new RuntimeException(e);
            }
            entityManager.merge(coveredRegionFromDB);
            try {
                userTransaction.commit();
            } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                     IllegalStateException | SystemException e) {
                e.printStackTrace();
            }
            return "coveredRegionFromDB updated successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "coveredRegionFromDB update failed!";
        }
    }

    @DELETE
    @Path("/deleteCoveredRegion/{id}")
    public String deleteCoveredRegion(@PathParam("id") int regionId) {
        try {
            CoveredRegion region = entityManager.find(CoveredRegion.class, regionId);
            try {
                userTransaction.begin();
            } catch (NotSupportedException | SystemException e) {
                throw new RuntimeException(e);
            }
            entityManager.remove(region);
            try {
                userTransaction.commit();
            } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException |
                     IllegalStateException | SystemException e) {
                e.printStackTrace();
            }
            return "CoveredRegion deleted successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "CoveredRegion deletion failed!";
        }
    }

    @GET
    @Path("/{regionId}")
    public CoveredRegion getCoveredRegionId(@PathParam("regionId") int regionId) {
        CoveredRegion region = entityManager.find(CoveredRegion.class, regionId);
        return region;
    }

    @GET
    @Path("/getAllCoveredRegion")
    public List<CoveredRegion> getAllCoveredRegion() {
        return entityManager.createQuery("SELECT c FROM CoveredRegion c", CoveredRegion.class).getResultList();
    }
}
