package models;

import com.avaje.ebean.Model;
import controllers.Util;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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

	public static Model.Finder<String, Vendor> find = new Model.Finder<>(Vendor.class);

    public static List<Vendor> findAll(){
        List<Vendor> vendors = Vendor.find.all();
        return vendors;
    }

    public static Optional<Vendor> findByName(String name) {
        Vendor vendor = Vendor.find.where().eq("name", name).findUnique();
        return vendor == null ? Optional.empty() : Optional.of( vendor );
    }

    public String getName() {
        return this.name;
    }

    public Vendor(String name) {
    	this.name = name;
    }

}
