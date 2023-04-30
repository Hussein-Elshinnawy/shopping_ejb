package shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;

@Stateless
@Entity
@Table(name = "sellingcompanyrep")
public class SellingCompanyRep {
    @Id
    String name;
    String password;

    @OneToMany(mappedBy = "sellingCompanyRep",fetch= FetchType.EAGER)
    @JsonIgnore
    private List<Product> products ;

    public SellingCompanyRep() {

    }

    public SellingCompanyRep(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}