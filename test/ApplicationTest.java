import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Date;
import java.util.Optional;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.avaje.ebean.Ebean;
import org.junit.*;
import controllers.Encryptor;
import controllers.Util;
import constants.R;
import models.Gender;
import models.Registration;
import models.Role;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.db.evolutions.Evolutions;
import play.db.Database;
import play.db.Databases;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


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
        String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
        String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
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


    @Test
    public void renderTemplate() {
        Content html = views.html.index.render();
        assertEquals("text/html", contentType(html));
    }

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
            assertEquals( registration, newRegistration.get() );
        }
    }

}
