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

        return redirect( routes.ProductController.list() );

    }

}