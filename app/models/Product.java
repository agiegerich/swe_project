package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;
import java.util.List;

@Entity
public class Product extends Model {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @MaxLength(50)
    @Column(name="productName")
    public String productName;

    @Required
    @MaxLength(50)
    public String category;

    @Required
    public Integer quantity;

    public static Model.Finder<String, Product> find = new Model.Finder<>(Product.class);

    public static List<Product> findAll(){
        List<Product> products = Product.find.all();
        return products;
    }

    public long getId(){
        return this.id;
    }
}


