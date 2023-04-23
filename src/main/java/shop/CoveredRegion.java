package shop;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;

@Stateless
@Entity
@Table(name = "coveredregion")
public class CoveredRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int regionId;
    private String region;

    @ManyToMany(mappedBy = "coveredRegions")
    private List<ShippingCompany> shippingCompanies;
    public CoveredRegion(int regionId, String region, List<ShippingCompany> shippingCompanies) {
        this.regionId=regionId;
        this.region = region;
        this.shippingCompanies = shippingCompanies;
    }

    public CoveredRegion() {

    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getId() {
        return regionId;
    }

    public void setId(int regionId) {
        this.regionId = regionId;
    }


    public List<ShippingCompany> getShippingCompanies() {
        return shippingCompanies;
    }

    public void setShippingCompanies(List<ShippingCompany> shippingCompanies) {
        this.shippingCompanies = shippingCompanies;
    }
}
