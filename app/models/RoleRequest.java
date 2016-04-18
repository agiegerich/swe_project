package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by agiegerich on 4/18/16.
 */
@Entity
public class RoleRequest extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;


    @OneToOne
    @Column(nullable=false)
    @JoinColumn(name="user_id")
    public User user;

    @Column(nullable=false)
    public Role role;

    public RoleRequest(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public static Model.Finder<String, RoleRequest> find = new Model.Finder<>(RoleRequest.class);


    public static List<RoleRequest> findAll() {
        return find.all();
    }

    public static RoleRequest findByUserId( long userId ) {
        return find.where().eq("user_id", userId).findUnique();
    }

    public User getUser() {
        return user;
    }
}
