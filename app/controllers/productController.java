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

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class productController extends Controller {

    final Form<Product> productForm = Form.form(Product.class);

    public Result list() {
        return ok(product.render(Product.findAll()));
    }

}