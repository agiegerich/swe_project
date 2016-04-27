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
/*
    @OneToMany(cascade = CascadeType.ALL)
    public List<CartItem> shoppingHistory;
*/

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

    public List<CartItem> inCart(){
        List<CartItem> shoppingHistory= new ArrayList<CartItem>();
        for ( CartItem item : this.shoppingCart ) {
            if(item.done == false) shoppingHistory.add(item);
        }
        return shoppingHistory;
    }

    public List<CartItem> shoppingHistory() {
        List<CartItem> shoppingHistory= new ArrayList<CartItem>();
        for ( CartItem item : this.shoppingCart ) {
            if(item.done == true) shoppingHistory.add(item);
        }
        return shoppingHistory;
    }

    public String getCartDeliveryTime() {
        List<CartItem> cart = getShoppingCart();
        Size largestSize = Size.SMALL;
        for (CartItem item : cart) {
            if (item.getProduct().size == Size.LARGE) {
                largestSize = Size.LARGE;
                break;
            } else if (item.getProduct().size == Size.MEDIUM) {
                largestSize = Size.MEDIUM;
            }
        }

        if (largestSize == Size.SMALL) {
            return "1-3 days";
        } else if (largestSize == Size.MEDIUM) {
            return "3-5 days";
        } else {
            return "5-7 days";
        }
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

    public static List<User> findAll() {
        List<User> users = User.find.all();
        return users;
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

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
