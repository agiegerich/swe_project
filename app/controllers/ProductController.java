package controllers;

import constants.R;
import models.CartItem;
import models.Product;
import models.PurchaseHistoryItem;
import models.User;
import models.Role;
import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.data.DynamicForm;
import play.data.Form;
import views.formdata.ReplaceQuantity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.ProductDataform;
import views.html.product;
import views.html.*;

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
        return ok(product.render(user.get(), Product.findAll(), R.categories, productForm));
    }

    public Result history() {
        String email = session("email");
        if (email == null) {
            Application.sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }

        /* This isn't what we want to do.
            If a user buys 6 items and they are the last 6 items in stock
            then this loop will make the item in history have 0. We want to
            preserve the number that were purchased in the history.
        for ( CartItem item : user.get().shoppingHistory() ) {
            if (item.quantityInCart > item.getProduct().getQuantity() ) {
                item.quantityInCart = item.getProduct().getQuantity();
                item.save();
            }
        }*/
        return ok(shoppingHistory.render(user.get().getPurchaseHistory()));
    }

    public Result replace(Long id) {
        String email = session("email");
        if (email == null) {
            return Application.sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }


        Product product = PurchaseHistoryItem.findById(id).getProduct();

        if (product.getQuantity() == 0) {

            List<Product> alternates = product.getAlternates();

            return ok(replaceItem.render(alternates, PurchaseHistoryItem.findById(id).quantityPurchased, Form.form(ReplaceQuantity.class)));

        } else {

           List<Product> alternates = new ArrayList<Product>();

            alternates.add(product);

            return ok(replaceItem.render(alternates, PurchaseHistoryItem.findById(id).quantityPurchased, Form.form(ReplaceQuantity.class)));

        }
    }

    public Result addProduct(){

        Form<ProductDataform> formData = Form.form(ProductDataform.class).bindFromRequest();

        ProductDataform product = formData.get();

        Product newProduct = new Product(product.productName, product.category, product.quantity, product.price, product.size);

        newProduct.save();

        return redirect(routes.ProductController.list());

    }
/*
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
*/
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
        Optional<User> optUser = User.findByEmail(email);
        if ( !optUser.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }
        User user = optUser.get();

        long totalCost = 0;

        StringBuilder purchaseReceipt = new StringBuilder();
        purchaseReceipt.append("Purchase Receipt: \n\n");
        for (CartItem cartItem : user.getShoppingCart()) {
            // Subtract from the number of products.
            Product productToPurchase = cartItem.getProduct();

            buyProduct(user, productToPurchase, cartItem.quantityInCart);
            // Delete from the users cart
            cartItem.delete();

            // Build the receipt email
            purchaseReceipt.append("Item Name: " + productToPurchase.getName() + "\n");
            purchaseReceipt.append("Quantity : " + cartItem.quantityInCart + "\n");
            purchaseReceipt.append("Item Cost:" + productToPurchase.getFormattedPrice() + "\n\n");
            totalCost += cartItem.quantityInCart * productToPurchase.getPrice();
        }

        purchaseReceipt.append("Total Cost: " + Util.formatLongAsDollars( totalCost ));

        // Build an email with the registration link containing the generated UUID.
        Email emailToSend = new Email();
        emailToSend.setSubject("Purchase Receipt");
        emailToSend.setFrom("SGL Mailer <team10mailer@gmail.com>");
        emailToSend.addTo("TO <" + email + ">");
        emailToSend.setBodyText(purchaseReceipt.toString());
        MailerPlugin.send(emailToSend);
        return redirect(routes.ProductController.list());
    }

    public void buyProduct(User user, Product productToPurchase, int quantity) {
        productToPurchase.setQuantity( productToPurchase.getQuantity() - quantity );
        productToPurchase.save();

        // Add to the users purchase history.
        PurchaseHistoryItem purchaseHistoryItem = new PurchaseHistoryItem(productToPurchase, quantity);
        user.getPurchaseHistory().add(purchaseHistoryItem);
        user.save();
    }

    public Result replaceProduct(Long productId, Integer quantity) {

        String email = session("email");
        if (email == null) {
            Application.sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> optUser = User.findByEmail(email);
        if ( !optUser.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }

        Form<ReplaceQuantity> formData = Form.form(ReplaceQuantity.class).bindFromRequest();

        ReplaceQuantity quantityForm = formData.get();

        Product product = Product.findById(productId).get();

        if ( quantityForm.replaceQuantity > quantity) {
            return Application.sendBadRequest("Input wrong quantity");
        }

        User user = User.findByEmail(email).get();

        Product productToPurchase = Product.findById(productId).get();

        if ( productToPurchase.getQuantity() - quantityForm.replaceQuantity < 0) {
            return Application.sendBadRequest("Input wrong quantity");
        }

        productToPurchase.setQuantity( productToPurchase.getQuantity() - quantityForm.replaceQuantity );
        productToPurchase.save();

        // Add to the users purchase history.
        PurchaseHistoryItem purchaseHistoryItem = new PurchaseHistoryItem(productToPurchase, quantityForm.replaceQuantity);
        user.getPurchaseHistory().add(purchaseHistoryItem);
        user.save();

        return redirect(routes.ProductController.history());
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
            for (CartItem cartItem : user.getShoppingCart()) {
                if ( cartItem.getProduct().getId() == potentialProduct.get().getId()) {
                    newItem = cartItem;
                }
            }

            if (newItem != null ){
                Logger.debug("addToCart: Item was not in cart");
                newItem.quantityInCart += productQuantity;
                newItem.save();
            } else {
                Logger.debug("addToCart: Item was in cart");
                Product product = potentialProduct.get();
                CartItem cartItem = new CartItem(product, productQuantity);
                user.getShoppingCart().add(cartItem);
                user.save();
            }
        }
        return ok(Json.toJson(jsonObject));
    }

    public Result deleteCartItem(Long cartItemId) {
        Logger.debug("Deleting cart item with id: " + cartItemId);
        CartItem cartItem = CartItem.findById(cartItemId);
        cartItem.delete();
        return ok( Json.toJson(new HashMap<String, String>()));
    }

    public Result manageProduct() {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role == Role.USER) {
            return Application.sendBadRequest("Invalid Session");
        }

        return ok(productManage.render(Product.findAll(), R.categories));
    }



    public Result manageAlternates(Long productId) {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role == Role.USER) {
            return Application.sendBadRequest("Invalid Session");
        }

        Product product = Product.findById(productId).get();

        List<Product> products = Product.findAll();

        products.remove(product);

        List<Product> alternates = product.getAlternates();

        for (Product item : product.getAlternates()){
            if( alternates.contains(item) ) {
                products.remove(item);
            }
        }

        return ok(alternatesManage.render(product, products, alternates));
    }

    public Result addAlternates(Long productId, Long alternatesProductId) {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role == Role.USER) {
            return Application.sendBadRequest("Invalid Session");
        }

        Product product = Product.findById(productId).get();

        Product alternate = Product.findById(alternatesProductId).get();

        product.getAlternates().add(alternate);

        product.save();

        alternate.getAlternates().add(product);

        alternate.save();

        return redirect(routes.ProductController.manageAlternates(productId));
    }

    public Result deleteAlternates(Long productId, Long alternatesProductId) {

        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent()) {
            session().clear();
            return Application.sendBadRequest("Invalid Session");
        }
        if( user.get().role == Role.USER) {
            return Application.sendBadRequest("Invalid Session");
        }

        Product product = Product.findById(productId).get();

        Product alternate = Product.findById(alternatesProductId).get();

        product.getAlternates().remove(alternate);

        product.save();

        alternate.getAlternates().remove(product);

        alternate.save();

        return redirect(routes.ProductController.manageAlternates(productId));
    }
}