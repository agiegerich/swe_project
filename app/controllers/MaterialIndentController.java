package controllers;

import constants.R;
import exceptions.EncryptorException;
import exceptions.RoleConfirmationDoesNotExist;
import models.Registration;
import models.Role;
import models.User;
import models.Product;
import models.MaterialIndent;
import play.Logger;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.formdata.MaterialIndentDataform;

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class MaterialIndentController extends Controller {

    final Form<MaterialIndentDataform> requestForm = Form.form(MaterialIndentDataform.class);

    public Result index(Long id) {

        User user = User.findById(id);
        return ok(materialIndent.render(requestForm, user));
    }

    public Result makeRequest(Long id) {

        User user = User.findById(id);

        Form<MaterialIndentDataform> formData = Form.form(MaterialIndentDataform.class).bindFromRequest();

        MaterialIndentDataform materialIndent = formData.get();

        MaterialIndent newMaterialIndent = new MaterialIndent(user, materialIndent.productName, materialIndent.category, materialIndent.quantity);

        newMaterialIndent.save();

        return redirect(routes.MaterialIndentController.index(id));
    }

}