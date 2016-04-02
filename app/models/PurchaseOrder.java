package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by agiegerich on 4/2/16.
 */
@Entity
public class PurchaseOrder extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;


    @OneToMany(mappedBy = "purchaseOrder")
    public List<MaterialIndent> materialIndents;

}
