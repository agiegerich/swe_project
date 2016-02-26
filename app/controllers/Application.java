package controllers;

import java.util.UUID;
import javax.persistence.EntityManager;

import play.*;
import play.data.Form;
import play.mvc.*;

import models.Registration;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;


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

    @Transactional
    public Result registerUser() {
        Logger.info("Recieved registration request.");

        Form<Registration> registrationForm = Form.form( Registration.class ).bindFromRequest();
        if ( registrationForm.hasErrors() ) {
            return badRequest();  
        }

        Registration registration = registrationForm.get();
        Logger.info( registration.firstName );
        registration.save();
        return redirect( routes.Application.index() );


    }
}
