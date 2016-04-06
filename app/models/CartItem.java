package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by agiegerich on 4/3/16.
 */
@Entity
public class CartItem extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public Product product;

    @Column(nullable=false)
    public int quantityInCart;

    public CartItem(Product product, int quantityInCart) {
        this.product = product;
        this.quantityInCart = quantityInCart;
    }

    public Product getProduct() {
        return product;
    }

    public static Model.Finder<String, CartItem> find = new Model.Finder<>(CartItem.class);

    public static CartItem findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }

}
