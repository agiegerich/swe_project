package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    @Column(name="firstName")
    public String firstName;

    @Required
    @MaxLength(50)
    @Column(name="lastName")
    public String lastName;
    
    @Required
    @Column(nullable=false)
    public Gender gender;
    
    @Required
    @Column(nullable=false, name="dateOfBirth")
    public Date dateOfBirth;

    @Required
    @MinLength(6)
    @MaxLength(20)
    @Column(nullable=false)
    public String password;

    @Required
    @Column(nullable=false)
    public Role role;

    @OneToMany(mappedBy="requester")
    public List<Request> requests;

    @OneToMany(cascade = CascadeType.ALL)
    public List<CartItem> shoppingCart;


    public int getQuantityInCartByProduct(Product product) {
        for (CartItem item : shoppingCart) {
            if (item.getProduct().getId() == product.getId()) {
                return item.quantityInCart;
            }
        }
        return 0;
    }

    public List<CartItem> getShoppingCart() {
        return shoppingCart;
    }
    
    public static Model.Finder<String, User> find = new Model.Finder<>(User.class);

    public static Optional<User> findByEmail(String email){
        User user = find.where().eq("email", email).findUnique();
        return user == null ? Optional.empty() : Optional.of( user );
    }

    public static User findById(Long id) {
        User user = User.find.where().eq("id", id).findUnique();
        return user;
    }
    
    public User(String email, String firstName, String lastName, Gender gender, Date dateOfBirth, String password, Role role){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.role = role;
        shoppingCart = new ArrayList<>();
    }

}
