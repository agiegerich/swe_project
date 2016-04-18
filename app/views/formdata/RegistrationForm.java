package views.formdata;

import models.Gender;
import models.Role;

import java.util.Date;

/**
 * Created by agiegerich on 4/18/16.
 */
public class RegistrationForm {

    public String firstName;
    public Date dateOfBirth;
    public String lastName;
    public String email;
    public String password;
    public String repeatPassword;
    public Role role;
    public Gender gender;
    public Integer roleConfirmationId;
}
