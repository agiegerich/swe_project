package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    /**
     * Set the result to be a Json result.
     */
    public Result jsonResult(Result response) {
        response().setContentType("application/json; charset=utf-8");   
        return response;
    }

    public Result index() { 
        return ok(index.render("Your new application is ready."));
    }

    public Result registerUser() {
        return null;
    }

}
