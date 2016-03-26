package models;

import com.avaje.ebean.Model;
import controllers.Util;
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
    @Column(name="productName", nullable = false)
    public String productName;

    @Required
    @MaxLength(50)
    @Column(nullable=false)
    public String category;

    @Required
    @Column(nullable=false)
    public Integer quantity;

    // price in cents
    @Required
    @Column(nullable=false)
    public Long price;

    public static Model.Finder<String, Product> find = new Model.Finder<>(Product.class);

    public static List<Product> findAll(){
        List<Product> products = Product.find.all();
        return products;
    }

    public static List<Product> findByCategory( String categoryToFind ) {
        List<Product> products = Product.find.where().eq( "category", categoryToFind ).findList();
        return products;
    }

    public long getId(){
        return this.id;
    }

    public String getProductName(){
        return this.productName;
    }

    public String getCategory() {
        return this.category;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public String getFormattedPrice() {
        return Util.formatLongAsDollars( this.price );
    }

    public Product(long id, String productName, String category, Integer quantity, Long price){
        this.id = id;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }
}


