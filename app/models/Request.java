package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Request extends Model {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @ManyToOne()
    @Column(nullable=false)
    public User requester;

    @Required
    @ManyToOne()
    @Column(nullable=false)
    public PurchaseOrder purchaseOrder;

    @Required
    @ManyToOne()
    @Column(nullable=false)
    public Product requestedProduct;

    @Required
    @Column(nullable=true)
    public Integer requestedQuantity;

    @Column(name="dateOfDelivered")
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date dateOfDelivered;

    public static Model.Finder<String, Request> find = new Model.Finder<>(Request.class);

    public Request(User user, Product product, Integer quantity) {
        this.requester = user;
        this.requestedProduct = product;
        this.requestedQuantity = quantity;
    }

}