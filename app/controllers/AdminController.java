package controllers;

import models.Role;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.adminUserView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by agiegerich on 4/18/16.
 */
public class AdminController extends Controller {

    public Result listAllUsers() {
        Optional<Result> invalidResult = checkSessionAndAdmin();
        if (invalidResult.isPresent()) {
            return invalidResult.get();
        }

        return ok( adminUserView.render( User.findAll() ) );
    }

    public Result makeAdmin(long userId) {
        Map<String, Object> jsonObject = new HashMap<>();
        Optional<Result> invalidResult = checkSessionAndAdmin();
        if (invalidResult.isPresent()) {
            return invalidResult.get();
        }

        User userToPromote = User.findById(userId);

        if (userToPromote.role != Role.MANAGER) {
            jsonObject.put("success", false);
            jsonObject.put("message", "This user cannot be promoted because they are not a manager.");
            Logger.error("Non-manager user with id=" + userId + " got promotion request.");
            return ok( Json.toJson(jsonObject) );
        }

        userToPromote.role = Role.ADMINISTRATOR;
        userToPromote.save();


        return ok(Json.toJson(jsonObject));



    }

    public Optional<Result> checkSessionAndAdmin() {
        String email = session("email");
        if (email == null) {
            return Optional.of(Application.sendBadRequest("You must be logged in to view the cart page."));
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
