package controllers;

import constants.R;
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

import java.util.Optional;
import java.util.Calendar;
import java.util.List;

import views.html.addVendor;
import views.formdata.VendorDataform;

public class VendorController extends Controller{

    final Form<VendorDataform> vendorForm = Form.form(VendorDataform.class);

	public Result index() {

        String email = session("email");
        Optional<User> user = User.findByEmail(email);
        Optional<Result> invalidResult = checkSession();
        if (invalidResult.isPresent()) {
            return invalidResult.get();
        }
        List<Vendor> existingVendors = Vendor.findAll();

        return ok(addVendor.render(vendorForm,user.get(),existingVendors));
	}

	public Result addNewVendor() {

    	Form<VendorDataform> formData = Form.form(VendorDataform.class).bindFromRequest();

        VendorDataform vendor = formData.get();
        
        Vendor newVendor = new Vendor(vendor.name);
        
        try {
            newVendor.save();    
        } catch (Exception e) {
            return Application.sendBadRequest("That vendor already exists.");    
        }
        
		return redirect(routes.VendorController.index());
	}

    public Optional<Result> checkSession() {
        String email = session("email");
        if (email == null) {
            return Optional.of(Application.sendBadRequest("You must be logged in to view this page."));
        }

        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Optional.of(Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist."));
        }

        if ( user.get().role != Role.ADMINISTRATOR) {
            return Optional.of(Application.sendBadRequest("You must be an admin to view this page."));
        }

        return Optional.empty();
    }
}