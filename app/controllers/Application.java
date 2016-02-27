package controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;

import play.*;
import play.data.Form;
import play.mvc.*;

import models.Registration;

import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;


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
        return ok(index.render());
    }

    public Result badRequest(List<String> errors) {
        return badRequest( badRequest.render(errors) );
    }

    @Transactional
    public Result registerUser() {
        Logger.info("Recieved registration request.");

        // Validate the form.
        Form<Registration> registrationForm = Form.form( Registration.class ).bindFromRequest();
        Map<String, String> data = registrationForm.data();
        for ( String d : data.keySet() ) {
            Logger.debug( d + ": "+ data.get(d) +"\n" );
        }
        if ( registrationForm.hasErrors() ) {
            return badRequest( badRequest.render( Util.getErrorList( registrationForm )) );
        }

        Registration registration = registrationForm.get();
        
        // Add a UUID to the Registration so that we can verify the registration via email.
        registration.uuid = UUID.randomUUID().toString();

        // Save the Registration in the database.
        registration.save();

        // Build an email with the registration link containing the generated UUID.
        Email email = new Email();
        email.setSubject("Registration Confirmation");
        email.setFrom("SGL Mailer <team10mailer@gmail.com>");
        email.addTo( "TO <"+registration.email+">" );
        final String confirmationUrl = request().host() + routes.Application.registrationConfirmation( registration.uuid ).toString();
        email.setBodyText("Copy and paste the following link into your address bar to confirm your registration:\n" + confirmationUrl);

        // Send the email.
        MailerPlugin.send( email );

        return redirect( routes.Application.registrationEmailSent( registration.email ) );
    }

    public Result  registrationConfirmation( String uuid ) {
        return ok( registrationConfirmation.render() );
    }

    public Result registrationEmailSent( String email ) {
        return ok( registrationEmailSent.render( email ) );
    }
}
