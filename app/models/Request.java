package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.MaxLength;

import javax.persistence.*;
import java.util.Optional;
import java.util.Date;
import java.util.List;

/*
    Request model is the Material Indent model
*/

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
    @Column(nullable=false)
    public boolean done;

    @Required
    @Column(nullable=false, name="productName")
    public String productName;

    @Required
    @Column(nullable=false)
    public String category;
/*
    @Required
    @ManyToOne()
    @Column(nullable=false, name="purchaseOrder")
    public PurchaseOrder purchaseOrder;
*/
    @Required
    @Column(nullable=true, name="requestedQuantity")
    public int requestedQuantity;

    @Required
    @Column(name="acceptedQuantity")
    public Integer acceptedQuantity;

    @Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    @Column(name="shippingDate")
    public Date shippingDate;

    @Required
    @Column(name="deliveryDate")
    public Date deliveryDate;

    @Required
    @ManyToOne
    public User operator;

    @Required
    @MaxLength(200)
    public String feedback;

    public static Model.Finder<String, Request> find = new Model.Finder<>(Request.class);

    public static List<Request> findAll(){
        List<Request> requests = Request.find.all();
        return requests;
    }

    public static Optional<Request> findById(Long id) {

        Request request = Request.find.where().eq("id", id).findUnique();
        return request == null ? Optional.empty() : Optional.of( request );

    }

    public static List<Request> findCurrentByRequester(User requester) {

        List<Request> requests = Request.find.where().eq( "requester", requester ).eq( "done", false).findList();
        return requests;
    }

    public static List<Request> findClosedByRequester(User requester) {

        List<Request> requests = Request.find.where().eq( "requester", requester ).eq( "done", true).findList();
        return requests;
    }

    public Request(User user, String productName, String category, int requestedQuantity) {
        this.requester = user;
        this.done = false;
        this.productName = productName;
        this.category = category;
        this.requestedQuantity = requestedQuantity;
    }

}