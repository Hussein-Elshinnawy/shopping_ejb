package shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "coveredregion")
public class CoveredRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int regionId;
    private String region;
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "region_company",
            joinColumns = @JoinColumn(name = "regionId"),
            inverseJoinColumns = @JoinColumn(name = "companyId"))
    @JsonIgnoreProperties("shippingCompanies")
    private List<ShippingCompany> shippingCompanies = new ArrayList<>();
    public CoveredRegion(int regionId, String region) {
        this.regionId=regionId;
        this.region = region;

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
