package controllers;

import exceptions.RoleConfirmationDoesNotExist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;

import play.*;
import play.data.Form;
import play.mvc.*;

import models.Registration;
import models.Role;
import models.User;

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

    public Result sendBadRequest( String error ) {
        List<String> errorList = new ArrayList<>();
        errorList.add( error );
        return sendBadRequest( errorList );
    }

    public Result sendBadRequest(List<String> errors) {
        return badRequest( badRequest.render(errors) );
    }

    @Transactional
    public Result registerUser() {
        Logger.info("Recieved registration request.");

        // Validate the form.
        Form<Registration> registrationForm = Form.form( Registration.class ).bindFromRequest();

        Map<String, String> data = registrationForm.data();

        for ( String key : data.keySet() ) {
            Logger.debug( key + ": " + data.get(key) + "\n" );
        }


        if ( registrationForm.hasErrors() ) {
            return sendBadRequest( ApplicationHelpers.getErrorList( registrationForm ) );
        }


        Registration registration = registrationForm.get();

        if ( registration.role != Role.USER ) {
            try {
                ApplicationHelpers.confirmRoleAndDelete( registration.email, registration.role, registration.roleConfirmationId );
            } catch ( RoleConfirmationDoesNotExist e ) {
                return sendBadRequest( "The role ID you entered is not correct." );
            }
        }

        
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

    public Result registrationConfirmation( String uuid ) {
        Optional<Registration> potentialCorrespondingRegistration = Registration.findByUuid( uuid );
        if ( potentialCorrespondingRegistration.isPresent() ) {

            Registration correspondingRegistration = potentialCorrespondingRegistration.get();

            User newUser = new User( 
                    correspondingRegistration.email, 
                    correspondingRegistration.firstName,
                    correspondingRegistration.lastName,
                    correspondingRegistration.gender,
                    correspondingRegistration.dateOfBirth,
                    correspondingRegistration.password,
                    correspondingRegistration.role );
            
            newUser.save();
            correspondingRegistration.delete();
            
            return ok( registrationConfirmation.render() );
        } else {
            return sendBadRequest("There is no registration request corresponding to this link.");
        }

    }

    public Result registrationEmailSent( String email ) {
        return ok( registrationEmailSent.render( email ) );
    }
}
