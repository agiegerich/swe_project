package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;


/************************************
		Purchase Order Model
************************************/

@Entity
public class PurchaseOrder extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Required
    @ManyToOne()
    @Column(nullable=false)
    public User requester;

    @Required
    @Column(nullable=false)	
    public boolean done;


    @Required
    @Column(nullable=false)
    public String supplier;

    @Required
    @Column(nullable=false)
    public Long operatorId;

    @Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    @Column(nullable=false)
    public Date shippingDate;

    @Required
    @Column(nullable=false)
    public Date deliveryDate;

    @OneToMany(mappedBy = "purchaseOrder")
    public List<Request> requests;

    public static Model.Finder<String, PurchaseOrder> find = new Model.Finder<>(PurchaseOrder.class);

    public static List<PurchaseOrder> findAll(){
        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.all();
        return purchaseOrders;
    }

    public static Optional<PurchaseOrder> findById(Long id) {

        PurchaseOrder purchaseOrder = PurchaseOrder.find.where().eq("id", id).findUnique();
        return purchaseOrder == null ? Optional.empty() : Optional.of( purchaseOrder );

    }

	public static List<PurchaseOrder> findOrdersInProgress(User requester) {

        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.where().eq( "requester", requester ).eq( "done", false).findList();
        return purchaseOrders;
    }    


    public static List<PurchaseOrder> findOrderHistory(User requester) {

        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.where().eq( "requester", requester ).eq( "done", true).findList();
        return purchaseOrders;
    }



    public PurchaseOrder(User user,String supplier) {
    	this.requester = user;
    	this.supplier = supplier;
    }
}
