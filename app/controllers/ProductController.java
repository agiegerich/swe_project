package controllers;

import constants.R;
import exceptions.EncryptorException;
import exceptions.RoleConfirmationDoesNotExist;
import models.Registration;
import models.Role;
import models.User;
import models.Product;
import play.Logger;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.formdata.ProductDataform;

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class ProductController extends Controller {

    final Form<ProductDataform> productForm = Form.form(ProductDataform.class);

    public Result list() {

        return ok(product.render(Product.findAll(), R.categories, productForm));
    }

    public Result listFilterByCategory(String category) {
        return ok(product.render( Product.findByCategory(category), R.categories, productForm ));
    }

    public Result addProduct(){

        Form<ProductDataform> formData = Form.form(ProductDataform.class).bindFromRequest();

        ProductDataform product = formData.get();

        Product newProduct = new Product(product.id, product.productName, product.category, product.quantity, product.price);

        newProduct.save();

        return redirect(routes.ProductController.list());

    }

    public Result buyProduct(Long productId, int productQuantity) {
        Optional<Product> potentialProduct = Product.findById( productId );

        Map<String, Object> jsonObject = new HashMap<>();

        if (!potentialProduct.isPresent()) {
            Logger.error("No product with that ID!");
            jsonObject.put("success", false);
        } else {
            Product product = potentialProduct.get();
            product.quantity = product.quantity - productQuantity;
            product.save();
            jsonObject.put("success", true);
        }
        return ok( Json.toJson( jsonObject ) );
    }

}