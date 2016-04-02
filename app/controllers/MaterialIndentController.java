package controllers;

import models.MaterialIndent;
import models.Product;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.MaterialIndentDataform;
import views.html.materialIndent;

import java.util.Optional;

public class MaterialIndentController extends Controller {

    final Form<MaterialIndentDataform> requestForm = Form.form(MaterialIndentDataform.class);

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

        Logger.debug("Size of material request indents: " + user.get().materialIndents.size());

        return ok(materialIndent.render(requestForm, user.get().materialIndents.get(0).requestedProduct.getName() ) );
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

        Form<MaterialIndentDataform> formData = Form.form(MaterialIndentDataform.class).bindFromRequest();

        MaterialIndentDataform materialIndent = formData.get();

        // If this product does not already exist create a new empty product;
        Product product = null;
        Optional<Product> potentialProduct = Product.findSpecific(materialIndent.productName, materialIndent.category, materialIndent.price);
        if (!potentialProduct.isPresent()) {
            // quantity should be 0 since the material indent quantity can't be added until after inspection.
            product = new Product(materialIndent.productName, materialIndent.category, 0, materialIndent.price);
            product.save();
        } else {
            product = potentialProduct.get();
        }


        MaterialIndent newMaterialIndent = new MaterialIndent(user.get(), product, materialIndent.quantity);
        newMaterialIndent.save();

        return redirect(routes.MaterialIndentController.index());
    }

}