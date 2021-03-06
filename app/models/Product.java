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
import java.util.Set;

@Entity
// Make it so  there cannot be a single product with the same name and price and category.
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"name", "price", "category"})
)
public class Product extends Model {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @MaxLength(50)
    @Column(name="name", nullable = false)
    public String name;

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

    @ManyToOne
    public Vendor preferredVendor;

    @Column(nullable=false)
    public Size size;

/*
    @OneToMany(mappedBy="requestedProduct")
    public List<Request> requests;
*/
    @ManyToMany
    @JoinTable(name="product_alternate_associations",
                    joinColumns = @JoinColumn(name="product_a", referencedColumnName="id"),
                    inverseJoinColumns = @JoinColumn(name="product_b", referencedColumnName = "id"))
    public List<Product> alternates;

    public static Model.Finder<String, Product> find = new Model.Finder<>(Product.class);

    public static List<Product> findAll(){
        List<Product> products = Product.find.all();
        return products;
    }

    public static Optional<Product> findSpecific(String name, String category, Long price) {
        Product product = Product.find.where().eq("name", name).eq("category", category).eq("price", price).findUnique();
        return product == null ? Optional.empty() : Optional.of( product );
    }

    public static Optional<Product> findById(Long id) {
        Product product = Product.find.where().eq("id", id).findUnique();
        return product == null ? Optional.empty() : Optional.of(product);

    }

    public long getId() {
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return this.price;
    }

    public List<Product> getAlternates() {
        return this.alternates;
    }

    public String getFormattedPrice() {
        return Util.formatLongAsDollars( this.price );
    }

    public Vendor getPreferredVendor() {
        return preferredVendor;
    }

    public Product(String productName, String category, Integer quantity, Long price, Size size){
        this.name = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.size = size;
    }
}


