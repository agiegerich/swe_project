package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by agiegerich on 4/2/16.
 */
@Entity
public class Vendor extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Column(unique=true)
    public String name;


    @OneToMany(mappedBy="preferredVendor")
    public List<Product> preferredSuppliedProducts;

}
