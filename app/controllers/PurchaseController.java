package controllers;

import constants.R;
import models.PurchaseOrder;
import models.Request;
import models.Product;
import models.User;
import models.Role;
import models.Vendor;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.RequestDataform;
import views.html.request;
import views.html.requestList;
import views.html.inspection;
import views.html.addProduct;
import views.html.requestHistory;
import views.formdata.AddProductDataform;
import views.formdata.PurchaseOrderDataform;
import views.html.orderHistory;
import views.html.purchaseOrder;
import views.html.currentOrders;
import views.html.addOrderRequests;
import views.html.orderViewRequests;

import java.util.Optional;
import java.util.Calendar;
import java.util.List;

/*********************************
	Purchase Order Controller
*********************************/

public class PurchaseController extends Controller {

    final Form<PurchaseOrderDataform> orderForm = Form.form(PurchaseOrderDataform.class);

    public Result index() {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        List<Vendor> vendors = Vendor.findAll();

        return ok(purchaseOrder.render(orderForm,vendors,user.get()));
    }

	/*************************************************************************
    function: makeOrder
        inputs: none
        outputs: redirect to page
        description: Creates a new process order for a manager/admin account to add requests to.
    *************************************************************************/
	public Result makeOrder () {
		String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        Form<PurchaseOrderDataform> formData = Form.form(PurchaseOrderDataform.class).bindFromRequest();

        PurchaseOrderDataform order = formData.get();

        PurchaseOrder newPurchaseOrder = new PurchaseOrder(user.get(),order.supplier) ;

        newPurchaseOrder.save();

		return redirect(routes.PurchaseController.viewCurrentOrders());
	}


/****************************************************************
    function: addRequestToOrder
        inputs: none
        outputs: refresh purchase order page
        description: adds a request order to the purchase order and displays the request on the purchase order listing.
****************************************************************/

    public Result addRequestToOrder(Long id) {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        Form<RequestDataform> formData = Form.form(RequestDataform.class).bindFromRequest();

        RequestDataform request = formData.get();

        PurchaseOrder purchaseOrder = PurchaseOrder.findById(id).get();

        Logger.debug("User: " + user.get().email);
        Logger.debug("Product name: " + request.productName);
        Logger.debug("Category: " + request.category);
        Logger.debug("Quantity: " + request.quantity);
        Logger.debug("Size: " + request.size);
        Logger.debug("Supplier: " + purchaseOrder.supplier);

        Request newRequest = new Request(
                user.get(),
                request.productName,
                request.category,
                request.quantity,
                request.size,
                purchaseOrder.supplier.name);

        //newRequest.save();

        purchaseOrder.getRequests().add(newRequest);

        purchaseOrder.save();

        return redirect(routes.PurchaseController.addToOrder(id));
    }

/******************************************************************
    function: orderHistory
        inputs: none
        outputs: call to a webpage
        description: Displays all process orders that are completed in a table with certain values displayed to the user
******************************************************************/

	public Result orderHistory() {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        return ok(orderHistory.render(PurchaseOrder.findOrderHistory(user.get()), user.get()));
    }

/**********************************************************************
    function: viewCurrentOrders
        inputs: none
        outputs: call to a webpage
        description: Displays all process orders that are not yet completed in a table with ceratin values displayed
**********************************************************************/

    public Result viewCurrentOrders() {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        return ok(currentOrders.render(PurchaseOrder.findOrdersInProgress(user.get()), user.get()));   
    }

/**********************************************************************
    function: viewRequests
        inputs: Long id, id of the purchase order being viewed
        outputs: call to a webpage
        description: Displays all requests contained in a process order
**********************************************************************/

    public Result viewRequests(Long id) {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        PurchaseOrder purchaseOrder = PurchaseOrder.findById(id).get();

        return ok(orderViewRequests.render(purchaseOrder.requests,purchaseOrder,user.get()));
    }

/**********************************************************************
    function: addToOrder
        inputs: Long id - id of the purchase order being added to
        outputs: call to a webpage
        description: Displays all requests in a process order currently as well as give the option to add more requests
**********************************************************************/

    public Result addToOrder(Long id) {

        final Form<RequestDataform> requestForm = Form.form(RequestDataform.class);

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }

        PurchaseOrder purchaseOrder = PurchaseOrder.findById(id).get();

        return ok(addOrderRequests.render(purchaseOrder.getRequests(),requestForm,purchaseOrder,user.get()));
    }

}