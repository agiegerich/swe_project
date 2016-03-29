package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
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
public class MaterialIndent extends Model {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Required
    @ManyToOne
    public User requester;

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
/*
    @OneToMany
    public List<Inspection> inspections;
*/
    @Column(name="dateOfDelivered")
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date dateOfDelivered;

    public static Model.Finder<String, MaterialIndent> find = new Model.Finder<>(MaterialIndent.class);

    public MaterialIndent(User user, String productName, String category, Integer quantity) {
        this.requester = user;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
    }

}