package controllers;

import constants.R;
import models.Product;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.ProductDataform;
import views.html.product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductController extends Controller {

    final Form<ProductDataform> productForm = Form.form(ProductDataform.class);

    public Result list() {

        return ok(product.render(Product.findAll(), R.categories, productForm));
    }

    public Result addProduct(){

        Form<ProductDataform> formData = Form.form(ProductDataform.class).bindFromRequest();

        ProductDataform product = formData.get();

        Product newProduct = new Product(product.productName, product.category, product.quantity, product.price);

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