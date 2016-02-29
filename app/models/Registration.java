package models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

import play.db.ebean.Model;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

@Entity
public class Registration extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    // Note: @Required should not be used here because @Required is used for form validation and the UUID is
    //       generated on the server side, NOT sent with the form.
    @Column(length=36, unique=true, nullable=false)
    public String uuid;

    @Required
    @MaxLength(50)
    @Column(nullable=false)
    public String firstName;

    /*
    @Required
    @Column(nullable=false)
    public Date dateOfBirth;
    */

    @Required
    @MaxLength(50)
    @Column(nullable=false)
    public String lastName;

    @Required
    @Email
    @Column(unique=true, nullable=false)
    public String email;

    @Required
    @MinLength(6)
    @MaxLength(20)
    @Column(nullable=false)
    public String password;

    @Required
    @MinLength(6)
    @MaxLength(20)
    @Column(nullable=false)
    public String repeatPassword;
    
    @Required
    @Column(nullable=false)
    public Role role;

    @Required
    @Column(nullable=false)
    public Gender gender;

    @Column(nullable=false)
    public int roleConfirmationId;

    
    public static Model.Finder<String, Registration> find = new Model.Finder<String, Registration>(String.class, Registration.class);
    
    // TODO
    // This method appears to be a constructor but is actually an object method. Cannot be used to construct an object.
    /*
    public boolean Registration(String firstName, String lastName, String email, Role role, String password, String repeatPassword){
        if(password == repeatPassword){
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.role = role;
            this.save();
            return true;
        }else return false;
    }
    */
    
    public Registration findByUuid(String uuid){
        return find.where().eq("uuid", uuid).findUnique();
    }
    
    // TODO
    // What is the point of this method? Why not just call user.delete() instead of user.removeRegistration( user )?
    /*
    public void removeRegistration(Registration user){
        user.delete();
    }
    */

}
