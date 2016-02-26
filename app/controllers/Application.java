package controllers;

import java.util.UUID;
import javax.persistence.EntityManager;

import play.*;
import play.mvc.*;

import models.Test;

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
        EntityManager em = JPA.em();
        Test t = new Test("albert", 13);
        Logger.debug("Received register user request.");
        return jsonResult(ok());
    }
}
