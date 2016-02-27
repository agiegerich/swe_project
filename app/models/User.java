package models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;


import play.db.ebean.Model;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

@Entity
public class User extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @Email
    @Column(unique=true)
    public String email;
    
    @Required
    @MaxLength(50)
    public String firstName;

    @Required
    @MaxLength(50)
    public String lastName;
    
    /* TODO
     * I think Aashay said we shouldn't have a company name.
    @Required
    @MaxLength(50)
    public String companyName;
    */
    
    @Required
    @Column(nullable=false)
    public Gender gender;
    
    @Required
    @Column(nullable=false)
    public Date dateOfBirth;

    @Required
    @MinLength(6)
    @MaxLength(20)
    @Column(nullable=false)
    public String password;

    @Required
    @Column(nullable=false)
    public Role role;

    
    public static Model.Finder<String, User> find = new Finder(String.class, User.class);
    
    public User(String email, String firstName, String lastName, String companyName, Gender gender, Date dateOfBirth, String password, Role role){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        //this.companyName = companyName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.role = role;
        this.save();
    }

}
