package controllers;

import constants.R;
import exceptions.EncryptorException;
import exceptions.RoleConfirmationDoesNotExist;
import models.*;
import play.Logger;
import play.data.Form;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;
import views.formdata.Login;
import views.formdata.RegistrationForm;
import views.formdata.ResetPassword;
import views.html.*;

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class Application extends Controller {

    /**
     * Set the result to be a Json result.

    public Result jsonResult(Result response) {
        response().setContentType("application/json; charset=utf-8");   
        return response;
    }
     */

    public void setup() {
        if (!R.didSetup) {
            try {
                setupTestUsers();
                setupTestProducts();
                R.didSetup = true;
            } catch (EncryptorException e) {
                Logger.error("Failed to setup test data.");
            }
        }
    }

    public void setupTestProducts() {

        String mustangName = "Ford Mustang";
        String automotiveCategory = "automotive";
        Long mustangPrice = new Long(3500000);
        if ( !Product.findSpecific(mustangName, automotiveCategory, mustangPrice).isPresent() ) {
            Product mustang = new Product(mustangName, automotiveCategory, 1, mustangPrice, Size.LARGE);
            mustang.save();
        }

        String mclarenName = "McLaren F1";
        Long mclarenPrice = new Long(60000000);
        if ( !Product.findSpecific(mclarenName, automotiveCategory, mclarenPrice).isPresent() ) {
            Product mclaren = new Product(mclarenName, automotiveCategory, 1, mclarenPrice, Size.LARGE );
            mclaren.save();
        }


        String shirtName = "an old t-shirt";
        String clothingCategory = "clothing";
        Long shirtPrice = new Long(500);
        if (!Product.findSpecific(shirtName, clothingCategory, shirtPrice).isPresent() ) {
            Product shirt = new Product(shirtName, clothingCategory, 5, shirtPrice, Size.SMALL);
            shirt.save();
        }

        String sockName = "a sock";
        Long sockPrice = new Long(100);
        if ( !Product.findSpecific(sockName, clothingCategory, sockPrice).isPresent() ) {
            Product sock = new Product(sockName, clothingCategory, 3, sockPrice, Size.SMALL);
            sock.save();
        }

        String tvName = "flat screen TV";
        String electronicsCategory = "electronics";
        Long tvPrice = new Long(500000);
        if ( !Product.findSpecific(tvName, electronicsCategory, tvPrice).isPresent() ) {
            Product tv = new Product("flat screen TV", electronicsCategory, 10, tvPrice, Size.MEDIUM);
            tv.save();
        }

        String computerName = "computer";
        Long computerPrice = new Long(40000);
        if ( !Product.findSpecific(computerName, electronicsCategory, computerPrice).isPresent() ) {
            Product computer = new Product(computerName, electronicsCategory, 11, computerPrice, Size.MEDIUM);
            computer.save();
        }
    }

    // Inserts test users into the database.
    public void setupTestUsers() throws EncryptorException {
        Logger.info("Inserting test data into database...");
        String password = Encryptor.encrypt(R.AES_KEY, R.AES_IV, "password");
        String admin1Email = "admin1@uiowa.edu";
        if (!User.findByEmail(admin1Email).isPresent()) {
            User admin1 = new User(admin1Email, "Admin1", "Admin1", Gender.FEMALE, new Date(0), password
                    , Role.ADMINISTRATOR);
            admin1.save();
        }
        String admin2Email = "admin2@uiowa.edu";
        if (!User.findByEmail(admin2Email).isPresent()) {
            User admin2 = new User(admin2Email, "Admin2", "Admin2", Gender.MALE, new Date(0), password
                    , Role.ADMINISTRATOR);
            admin2.save();
        }
        String user1Email = "user1@uiowa.edu";
        if (!User.findByEmail(user1Email).isPresent()) {
            User user1 = new User(user1Email, "User1", "User1", Gender.FEMALE, new Date(0), password
                    , Role.USER);
            user1.save();
        }
        String user2Email = "user2@uiowa.edu";
        if (!User.findByEmail(user2Email).isPresent()) {
            User user2 = new User(user2Email, "User2", "User2", Gender.MALE, new Date(0), password
                    , Role.USER);
            user2.save();
        }
        String manager1Email = "manager1@uiowa.edu";
        if (!User.findByEmail(manager1Email).isPresent()) {
            User manager1 = new User(manager1Email, "Manager1", "Manager1", Gender.FEMALE, new Date(0), password
                    , Role.MANAGER);
            manager1.save();
        }
        String manager2Email = "manager2@uiowa.edu";
        if (!User.findByEmail(manager2Email).isPresent()) {
            User manager2 = new User(manager2Email, "Manager2", "Manager2", Gender.MALE, new Date(0), password
                    , Role.MANAGER);
            manager2.save();
        }
    }

    public Result shoppingCart() {
        String email = session("email");
        if (email == null) {
            sendBadRequest("You must be logged in to view the cart page.");
        }
        Optional<User> user = User.findByEmail(email);
        if ( !user.isPresent() ) {
            session().clear();
            return sendBadRequest("Invalid Session: User with email " + email + "does not exist.");
        }

        // If items have been purchased that were in the users shopping cart, subtract from the number of items in the
        // shopping cart.
        long totalValue = 0;
        for ( CartItem item : user.get().getShoppingCart() ) {
            if (item.quantityInCart > item.getProduct().getQuantity() ) {
                item.quantityInCart = item.getProduct().getQuantity();
                item.save();
            }
            totalValue += item.getProduct().getPrice() * item.quantityInCart;
        }

        String totalValueString = Util.formatLongAsDollars(totalValue);

        return ok( shoppingCart.render(user.get().getShoppingCart(), totalValueString, user.get().getCartDeliveryTime()) );
    }

    public Result index() {
        // setup test accounts
        setup();
        String email = session("email");
        if (email == null) {
            return login();
        } else {
            Optional<User> potentialUser = User.findByEmail(email);
            if (!potentialUser.isPresent()) {
                session().clear();
                return sendBadRequest("Invalid Session");
            }
            return ok(welcome.render(potentialUser.get()));
        }
    }

    public Result registration() {
        String email = session("email");
        if (email != null) {
            return sendBadRequest("You must be logged out to register.");
        }
        return ok( registration.render() );
    }

    public Result login() {
        return ok(login.render(Form.form(Login.class)));
    }

    public Result logout() {
        session().clear();
        return index();
    }

    public Result postLogin() {

        Form<Login> formData = Form.form(Login.class).bindFromRequest();

        Map<String, String> tempFormData = formData.data();
        for ( String key : tempFormData.keySet() ) {
            Logger.debug( key + ": " + tempFormData.get( key ) ); 
        }

        if (formData.hasErrors()) {
            flash("error", "Login credentials not valid.");
            return sendBadRequest("Login form was not filled out properly.");
        }

        Login login = formData.get();

        Optional<User> potentialUser = User.findByEmail(login.email);
        if ( !potentialUser.isPresent() ) {
            return sendBadRequest("User with that email does not exist.");
        }

        User user = potentialUser.get();
        try { 
            if (!Encryptor.decrypt( R.AES_KEY, R.AES_IV, user.password).equals( login.password )) {
                return sendBadRequest("Password is incorrect.");
            }
        } catch (EncryptorException e) {
            return sendBadRequest(Util.getStackTrace(e));
        }

        session().clear();
        session("email", user.email);
        return redirect(routes.Application.index());
    }

    /*
    public Result loginSuccess(Long id) {

        User user = User.findById(id);
        return ok( loginSuccess.render(user) );
    }
    */


    public static Result sendBadRequest( String error ) {
        List<String> errorList = new ArrayList<>();
        errorList.add( error );
        return sendBadRequest(errorList);
    }

    public static Result sendBadRequest(List<String> errors) {
        return badRequest( badRequest.render(errors) );
    }

    public Result registerUser() {
        Logger.info("Recieved registration request.");

        // Validate the form.

        RegistrationForm registrationForm = Form.form( RegistrationForm.class ).bindFromRequest().get();

        Registration registration = new Registration(
                registrationForm.firstName,
                registrationForm.lastName,
                registrationForm.email,
                registrationForm.dateOfBirth,
                registrationForm.role,
                registrationForm.gender,
                registrationForm.password,
                registrationForm.repeatPassword,
                registrationForm.roleConfirmationId
        );

        try {
            Logger.debug("Password before encryption: " + registration.password );
            registration.password = Encryptor.encrypt( R.AES_KEY, R.AES_IV, registration.password);
            Logger.debug("Password after encryption : " + registration.password );
            Logger.debug("Password after decryption : " + Encryptor.decrypt( R.AES_KEY, R.AES_IV, registration.password ));
        } catch (EncryptorException e) {
            return sendBadRequest(Util.getStackTrace(e));
        }

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
                    Role.USER );
            
            newUser.save();

            if (correspondingRegistration.role != Role.USER) {
                RoleRequest roleRequest = new RoleRequest(newUser, correspondingRegistration.role);
                roleRequest.save();
            }

            correspondingRegistration.delete();
            
            return ok( registrationConfirmation.render() );
        } else {
            return sendBadRequest("There is no registration request corresponding to this link.");
        }

    }

    public Result registrationEmailSent( String email ) {
        return ok( registrationEmailSent.render( email ) );
    }
    
    public Result resetPassword() {
        return ok( resetPassword.render(Form.form(ResetPassword.class)) );
    }

    public Result resetPasswordPost() {
        Form<ResetPassword> formData = Form.form(ResetPassword.class).bindFromRequest();

        ResetPassword passData = formData.get();

        if (formData.hasErrors()) {
            flash("error", "Email credentials not valid.");
            return sendBadRequest("Change Password form was not filled out properly.");
        }

        Optional<User> potentialUser = User.findByEmail(passData.email);

        if ( !potentialUser.isPresent() ) {
            return sendBadRequest("User with that email does not exist.");
        }

        User user = potentialUser.get();

        SecureRandom random = new SecureRandom();
        Email emailOut = new Email();
        emailOut.setSubject("Change Password Request");
        emailOut.setFrom("SGL Mailer <team10mailer@gmail.com>");
        emailOut.addTo( "TO <"+ user.email +">" );
        final String newPassword = new BigInteger(130, random).toString(32);
        emailOut.setBodyText("Please enter the following as your new password to log in:\n" + newPassword);

        // Send the email.
        MailerPlugin.send( emailOut );
        try {
            final String newPasswordEncrypt = Encryptor.encrypt( R.AES_KEY, R.AES_IV, newPassword);
            user.password = newPasswordEncrypt;
            user.save();
        }  catch (EncryptorException e) {
           return sendBadRequest(Util.getStackTrace(e));
        }

        return redirect( routes.Application.resetPasswordEmailSent( user.email ) );
    }

    public Result resetPasswordEmailSent( String email ) {
        return ok( resetPasswordEmailSent.render( email ) );
    }

    public Result manageAccount() {
        String email = session("email");
        if (email == null) {
            return redirect(routes.Application.index());
        }
        Optional<User> user = User.findByEmail(email);


        return ok( accountSettings.render(user.get()));
    }
/*
    public Result changePassword() {
        String email = session("email");
        Optional<User> potentialUser = User.findByEmail(email);
        Optional<Result> invalidResult = checkSession();
        if (invalidResult.isPresent()) {
            return invalidResult.get();
        }

        User user = potentialUser.get();

        SecureRandom random = new SecureRandom();
        Email emailOut = new Email();
        emailOut.setSubject("Change Password Request");
        emailOut.setFrom("SGL Mailer <team10mailer@gmail.com>");
        emailOut.addTo( "TO <"+ user.email +">" );
        final String newPassword = new BigInteger(130, random).toString(32);
        emailOut.setBodyText("Please enter the following as your new password to log in:\n" + newPassword);

        // Send the email.
        MailerPlugin.send( emailOut );
        try {
            final String newPasswordEncrypt = Encryptor.encrypt( R.AES_KEY, R.AES_IV, newPassword);
            user.password = newPasswordEncrypt;
            user.save();
        }  catch (EncryptorException e) {
           return sendBadRequest(Util.getStackTrace(e));
        }

        return ok( passwordChangeRequest().render());
    }*/
}
