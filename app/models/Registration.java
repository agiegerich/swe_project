package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

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
    @Column(nullable=false, name="firstName")
    public String firstName;

    @Required
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    @Column(nullable=false, name="dateOfBirth")
    public Date dateOfBirth;

    @Required
    @MaxLength(50)
    @Column(nullable=false, name="lastName")
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
    @Column(nullable=false, name="repeatPassword")
    public String repeatPassword;
    
    @Required
    @Column(nullable=false)
    public Role role;

    @Required
    @Column(nullable=false)
    public Gender gender;

    @Column(name="roleConfirmationId")
    public Integer roleConfirmationId;

    
    public static Model.Finder<String, Registration> find = new Model.Finder<>(Registration.class);

    public Registration( Integer roleConfirmationId ) {
        this.roleConfirmationId = roleConfirmationId;
    }

    public Registration(
            String firstName,
            String lastName, 
            String email, 
            Date dateOfBirth,
            Role role, 
            Gender gender,
            String password, 
            String repeatPassword, 
            Integer roleConfirmationId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
        this.roleConfirmationId = roleConfirmationId;
    }
    
    public static Optional<Registration> findByUuid(String uuid){
        Registration registration = find.where().eq("uuid", uuid).findUnique();
        return registration == null ? Optional.empty() : Optional.of( registration );
    }
}
