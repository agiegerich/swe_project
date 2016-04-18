package controllers;

import models.Role;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Optional;

import views.html.adminUserView;

/**
 * Created by agiegerich on 4/18/16.
 */
public class AdminController extends Controller {

    public Result listAllUsers() {
        String email = session("email");
        if (email == null) {
            Application.sendBadRequest("You must be logged in to view the cart page.");
        }

        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return Application.sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }

        if ( user.get().role != Role.ADMINISTRATOR) {
            return Application.sendBadRequest("You must be an admin to view this page.");
        }

        return ok( adminUserView.render() );
    }

}
