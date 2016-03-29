package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
public class Inspection extends Model {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
/*
    @ManyToOne
    @Column(nullable=false, name="materialIndent")
    public MaterialIndent materialIndent;
*/
    @Column(name="dateOfDelivered")
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date dateOfDelivered;

    public Integer acceptedNumber;

    public String feedback;

    public static Model.Finder<String, Inspection> find = new Model.Finder<>(Inspection.class);

}