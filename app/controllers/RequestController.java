package controllers;

import constants.R;
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

import java.util.Optional;
import java.util.Calendar;

public class RequestController extends Controller {

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

        Logger.debug("Size of material request indents: " + user.get().requests.size());

        return ok(request.render(requestForm, user.get()));
    }

    public Result makeRequest() {
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

        Request newRequest = new Request(user.get(), request.productName, request.category, request.quantity);

        newRequest.save();

        return redirect(routes.RequestController.index());

/*

        // If this product does not already exist create a new empty product;
        Product product = null;
        Optional<Product> potentialProduct = Product.findSpecific(request.productName, request.category, request.price);
        if (!potentialProduct.isPresent()) {
            // quantity should be 0 since the material indent quantity can't be added until after inspection.
            product = new Product(request.productName, request.category, 0, request.price);
            product.save();
        } else {
            product = potentialProduct.get();
        }


        Request newRequest = new Request(user.get(), product, request.quantity);
        newRequest.save();

        return redirect(routes.RequestController.index());
*/
    }

    public Result list() {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role != Role.ADMINISTRATOR) {
            return Application.sendBadRequest("Invalid Session");
        }

        return ok(requestList.render(Request.findAll(), R.categories, user.get()));
    }

    public Result ship(Long id) {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role != Role.ADMINISTRATOR) {
            return Application.sendBadRequest("Invalid Session");
        }

        Request request = Request.findById(id).get();

        request.operator = user.get();

        request.shippingDate = Calendar.getInstance().getTime();

        request.save();

        return redirect(routes.RequestController.list());
    }

}