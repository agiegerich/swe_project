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
    @Required
    @Column(length=32)
    public String uuid;

    @Required
    @MaxLength(50)
    public String firstName;

    @Required
    @MaxLength(50)
    public String lastName;

    @Required
    @Email
    public String email;

    @Required
    @MinLength(6)
    @MaxLength(20)
    public String password;

    @Required
    @MinLength(6)
    @MaxLength(20)
    public String repeatPassword;
    
    public Role role;

    public enum Role {
        user, supplier
    }
    
    public static Model.Finder<String, Registration> find = new Model.Finder<String, Registration>(String.class, Registration.class);
    
    public boolean Registration(String firstName, String lastName, String email, Role role, String password, String repeatPassword){
        if(password == repeatPassword){
            String uuid = UUID.randomUUID().toString();
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.role = role;
            this.save();
            return true;
        }else return false;
    }
    
    public Registration findByUuid(String uuid){
        return find.where().eq("uuid", uuid).findUnique();
    }
    
    public void removeRegistration(Registration user){
        user.delete();
    }

}
