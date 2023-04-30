package shop;


import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "admins")
public class Admin {
    @Id
    private String username;
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;


    public Admin() {
    }

    public Admin(String username, String name, String email, String password, String address, String phone) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
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


}