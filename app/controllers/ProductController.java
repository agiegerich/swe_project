package controllers;

import constants.R;
import models.CartItem;
import models.Product;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.ProductDataform;
import views.html.product;

import java.util.*;

public class ProductController extends Controller {

    final Form<ProductDataform> productForm = Form.form(ProductDataform.class);

    public Result list() {
        String email = session("email");
        if (email == null) {
            Application.sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }
        return ok( product.render(user.get(), Product.findAll(), R.categories, productForm) );
    }

    public Result addProduct(){

        Form<ProductDataform> formData = Form.form(ProductDataform.class).bindFromRequest();

        ProductDataform product = formData.get();

        Product newProduct = new Product(product.productName, product.category, product.quantity, product.price);

        newProduct.save();

        return redirect(routes.ProductController.list());

    }

    public Result buyProduct(Long productId, int productQuantity) {
        Optional<Product> potentialProduct = Product.findById(productId);

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
        return ok(Json.toJson(jsonObject));
    }

    public class IdQuantity {
        IdQuantity(Long id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        Long id;
        int quantity;
    }

    public Result checkoutCart() {
        String email = session("email");
        if (email == null) {
            Application.sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }

        for (CartItem cartItem : user.get().getShoppingCart()) {

            // Subtract from the number of products.
            Product productToPurchase = cartItem.getProduct();
            productToPurchase.setQuantity( productToPurchase.getQuantity() - cartItem.quantityInCart );
            productToPurchase.save();
            cartItem.delete();
        }

        return redirect(routes.ProductController.list());
    }

    public Result addToCart(Long productId, int productQuantity) {
        Logger.debug("POST for addToCart");
        Optional<Product> potentialProduct = Product.findById( productId );

        Map<String, Object> jsonObject = new HashMap<>();

        String email = session("email");

        if (!potentialProduct.isPresent()) {
            Logger.error("No product with that ID!");
            jsonObject.put("success", false);
        } else {
            Logger.debug("addToCart: " + email);
            Optional<User> optUser = User.findByEmail(email);
            User user = optUser.get();
            CartItem newItem = null;
            // Check if the item is already in the cart
            Product product = potentialProduct.get();
            CartItem cartItem = new CartItem(product, productQuantity);
            user.getShoppingCart().add(cartItem);
            user.save();
        }
        return ok(Json.toJson(jsonObject));
    }

    public Result deleteCartItem(Long cartItemId) {
        Logger.debug("Deleting cart item with id: " + cartItemId);
        CartItem cartItem = CartItem.findById(cartItemId);
        cartItem.delete();
        return ok( Json.toJson(new HashMap<String, String>()));
    }

}