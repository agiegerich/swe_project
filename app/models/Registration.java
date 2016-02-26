package models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import play.db.ebean.Model;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

@Entity
public class Registration extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Required
    public String firstName;

    @Required
    public String lastName;

    @Required
    public String email;

    @Required
    public Date dateOfBirth;

    @Required
    public String password;

    @Required
    public String repeatPassword;

    public String uuid;

    @Required
    public String role;

    public int executiveId;

}
