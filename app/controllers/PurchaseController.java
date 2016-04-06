package controllers;

import constants.R;
import models.PurchaseOrder;
import models.Request;
import models.Product;
import models.User;
import models.Role;
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
import views.html.orderHistory;
import views.html.purchaseOrder;
import views.html.currentOrders;

import java.util.Optional;
import java.util.Calendar;

/*********************************
	Purchase Order Controller
*********************************/

public class PurchaseController extends Controller {

    final Form<RequestDataform> requestForm = Form.form(RequestDataform.class);

    public Result index(Long id) {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }


        return ok(purchaseOrder.render(Request.findAll(),requestForm, PurchaseOrder.findById(id).get(), user.get()));
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

        //NEED TO MAKE ORDER HANDLE SUPPLIER

        PurchaseOrder newPurchaseOrder = new PurchaseOrder(user.get(),"Test");

        newPurchaseOrder.save();

		return redirect(routes.PurchaseController.index(newPurchaseOrder.id));
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

        Request newRequest = new Request(user.get(), request.productName, request.category, request.quantity, request.supplier);

        newRequest.save();

        PurchaseOrder purchaseOrder = PurchaseOrder.findById(id).get();

        purchaseOrder.requests.add(newRequest);

        purchaseOrder.save();

        //NEED TO ADD TO PURCHASE ORDER SOMEHOW

        return redirect(routes.PurchaseController.index(id));
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

}