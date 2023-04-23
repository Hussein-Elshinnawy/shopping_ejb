package shop;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
@Entity
@Table(name = "shippingcompany")
public class ShippingCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int companyId;
    private String name;

    @ManyToMany(mappedBy = "shippingCompanies",fetch=FetchType.EAGER)
    private List<CoveredRegion> coveredRegions = new ArrayList<>();

    public ShippingCompany(int companyId,String name ) {

       this.companyId=companyId;
        this.name = name;
    }

    public ShippingCompany() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return companyId;
    }

    public void setId(int companyId) {
        this.companyId = companyId;
    }


    public List<CoveredRegion> getCoveredRegions() {
        return coveredRegions;
    }

    public void setCoveredRegions(List<CoveredRegion> coveredRegions) {
        this.coveredRegions = coveredRegions;
    }


}
