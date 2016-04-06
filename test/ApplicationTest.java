import com.avaje.ebean.Ebean;
import constants.R;
import controllers.Encryptor;
import controllers.Util;
import models.*;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import play.test.FakeApplication;
import play.test.Helpers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {
    public static FakeApplication app;
    public static String createDdl = "";
    public static String dropDdl = "";

    @BeforeClass
    public static void startApp() throws IOException {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);

        // Reading the evolution file
        String evolutionContent = FileUtils.readFileToString(
            app.getWrappedApplication().getFile("conf/evolutions/default/1.sql"));

        // Splitting the String to get Create & Drop DDL
        String[] splitEvolutionContent = evolutionContent.split("# --- !Ups");
        String[] upsDowns = splitEvolutionContent[1].split("# --- !Downs");
        createDdl = upsDowns[0];
        dropDdl = upsDowns[1];

    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @Before
    public void createCleanDb() {
        Ebean.execute(Ebean.createCallableSql(dropDdl));
        Ebean.execute(Ebean.createCallableSql(createDdl));
    }


//    @Test
//    public void renderTemplate() {
//        assertEquals("text/html", views.html.index.render().contentType());
//    }

    @Test
    public void encryptorTest() {
        String pass = "password";
        try {
            String encrypted = Encryptor.encrypt( R.AES_KEY, R.AES_IV, pass );
            String decrypted = Encryptor.decrypt( R.AES_KEY, R.AES_IV, encrypted );
            assertEquals( pass, decrypted );
        } catch (Exception e) {
            fail( Util.getStackTrace( e ) );
        }
    }

    @Test
    public void getRegistrationByUuidTest() {

        String uuid = UUID.randomUUID().toString();
        Registration registration = new Registration(uuid, "test", "user", "test@email.com", new Date(), Role.USER, 
                Gender.MALE, "password", "password", 1);
        registration.save();

        Optional<Registration> newRegistration = Registration.findByUuid( uuid );
        if (!newRegistration.isPresent() ) {
            fail("No registration with that uuid.");
        } else {
            assertEquals( registration.id, newRegistration.get().id );
        }
    }

    @Test
    public void getUserByEmailTest() {
        String email = "test@email.com";
        User user = new User( email, "test", "test", Gender.MALE, new Date(), "test", Role.USER);
        user.save();
        Optional<User> newUser =  User.findByEmail(email);

        if (!newUser.isPresent()) {
            fail("No user with that email");
        }

        assertEquals( newUser.get().id, user.id );
    }

    @Test
    public void getProductByIdTest() {
        Product product = new Product("test", "electronics", 3, new Long(600) );
        product.save();

        Optional<Product> opt = Product.findById(product.id);
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get().getId(), (long) product.id);
    }

    @Test
    public void getAllProductsTest() {
        Product product1 = new Product("test", "electronics", 3, new Long(600) );
        Product product2 = new Product("test", "electronics", 3, new Long(6000) );
        Product product3 = new Product("test", "electronics", 3, new Long(60000) );

        product1.save();
        product2.save();
        product3.save();

        List<Product> products = Product.findAll();
        Assert.assertEquals(products.size(), 3);
    }

    @Test
    public void shoppingCartTest() {
        User user = new User("test@gmail.com", "test", "test", Gender.FEMALE, new Date(0), "test", Role.USER);
        Product product1 = new Product("test", "electronics", 3, new Long(600) );
        user.getShoppingCart().add( new CartItem(product1, 2) );
        Assert.assertEquals(user.getShoppingCart().get(0).id, product1.id);
    }
}
