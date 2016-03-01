package models;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class RoleConfirmation extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @Email
    @Column(unique=true, nullable=false)
    public String email;

    @Required
    @Column(nullable=false)
    public Role role;

    @Required
    @Column(nullable=false, unique=true)
    public int roleConfirmationId;

    private static Model.Finder<String, RoleConfirmation> find = new Model.Finder<String, RoleConfirmation>(String.class, RoleConfirmation.class);

    public static Optional<RoleConfirmation> findByEmail( String email ) {
        RoleConfirmation roleConfirmation = find.where().eq("email", email).findUnique();
        return roleConfirmation == null ? Optional.empty() : Optional.of( roleConfirmation );
    }

    public void setRole(Role role) {
        if ( role == Role.USER ) {
            throw new IllegalArgumentException( "Only managers and administrators need to have their role confirmed." );
        }

        this.role = role;
    }
}
