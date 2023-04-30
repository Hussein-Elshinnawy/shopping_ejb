package shop;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private String username;
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;

    private String message;
//    @Transient
//    private ShoppingCart cart = new ShoppingCart();

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Order> orders;



    public Customer() {
    }

    public Customer(String username, String name, String email, String password, String address, String phone) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public ShoppingCart getCart() {
//        return cart;
//    }
//
//    public void setCart(ShoppingCart cart) {
//        this.cart = cart;
//    }
}