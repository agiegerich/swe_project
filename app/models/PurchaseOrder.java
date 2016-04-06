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
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    @Column
    public Date shippingDate;

    @Required
    @Column
    public Date deliveryDate;

    @OneToMany(mappedBy = "purchaseOrder")
    public List<Request> requests;

    public static Model.Finder<String, PurchaseOrder> find = new Model.Finder<>(PurchaseOrder.class);

/*********************************************************************************
    function: findAll
        inputs: none
        outputs: List of PurchaseOrder objects
        description: Finds all purchase orders listed in the database
*********************************************************************************/

    public static List<PurchaseOrder> findAll(){
        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.all();
        return purchaseOrders;
    }

/*********************************************************************************
    function: findById
        inputs: Long id - id of purchase order to be found
        outputs: Optional<PurchaseOrder> - PurchaseOrder object of id, null if not found
        description: Searches through the database for a purchase order with the id of the input id, returns null if it does not exist
*********************************************************************************/

    public static Optional<PurchaseOrder> findById(Long id) {

        PurchaseOrder purchaseOrder = PurchaseOrder.find.where().eq("id", id).findUnique();
        return purchaseOrder == null ? Optional.empty() : Optional.of( purchaseOrder );

    }

/*********************************************************************************
    function: findOrdersInProgress
        inputs: User requester - user object that initiated the order
        outputs: List of PurchaseOrder objects - all orders started by the input user
        description: Finds all purchase orders, still in progress, initiated by the input user and returns the list.
*********************************************************************************/

	public static List<PurchaseOrder> findOrdersInProgress(User requester) {

        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.where().eq( "requester", requester ).eq( "done", false).findList();
        return purchaseOrders;
    }    

/*********************************************************************************
    function: findOrdersInProgress
        inputs: User requester - user object that initiated the order
        outputs: List of PurchaseOrder objects - all orders started by the input user
        description: Finds all purchase orders, that have been completed, initiated by the input user and returns the list.
*********************************************************************************/

    public static List<PurchaseOrder> findOrderHistory(User requester) {

        List<PurchaseOrder> purchaseOrders = PurchaseOrder.find.where().eq( "requester", requester ).eq( "done", true).findList();
        return purchaseOrders;
    }

/*********************************************************************************
    function: PurchaseOrder
        inputs: User user - User object represented the user initiating the order
                String supplier - Name of the supplier being ordered from
        outputs: none
        description: PurchaseOrder object constructor
*********************************************************************************/

    public PurchaseOrder(User user,String supplier) {
    	this.requester = user;
        this.done = false;
    	this.supplier = supplier;
    }
}
