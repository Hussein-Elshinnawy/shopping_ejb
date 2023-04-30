package shop;
import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "ordereditem")
public class OrderedItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ordered_item_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    // Constructors, getters, and setters

    public OrderedItem() {}

    public OrderedItem(Order order, Product product,int quantity) {
        this.order = order;
        this.quantity=quantity;
        this.product = product;
    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
