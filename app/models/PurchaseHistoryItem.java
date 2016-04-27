package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by agiegerich on 4/27/16.
 */
@Entity
public class PurchaseHistoryItem extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public int quantityPurchased;

    @ManyToOne
    public Product product;

    public PurchaseHistoryItem(Product product, int quantityPurchased) {
        this.product=product;
        this.quantityPurchased=quantityPurchased;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
