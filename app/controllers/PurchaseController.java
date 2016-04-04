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
import views.html.purchaseOrder;

import java.util.Optional;
import java.util.Calendar;

/*********************************
	Purchase Order Controller
*********************************/

public class PurchaseController extends Controller {

    final Form<RequestDataform> requestForm = Form.form(RequestDataform.class);

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


        return ok(purchaseOrder.render(Request.findAll(),requestForm, user.get()));
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

        PurchaseOrder newPurchaseOrder = new PurchaseOrder(user.get(),"TODO");

        newPurchaseOrder.save();

		return redirect(routes.PurchaseController.index());
	}


/****************************************************************
    function: addRequestToOrder
        inputs: none
        outputs: refresh purchase order page
        description: adds a request order to the purchase order and displays the request on the purchase order listing.
****************************************************************/

    public Result addRequestToOrder() {
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

        //NEED TO ADD TO PURCHASE ORDER SOMEHOW


        return redirect(routes.PurchaseController.index());
    }

	

}