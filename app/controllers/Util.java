package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.data.Form;
import play.data.validation.ValidationError;

public class Util {

    public static String hyperlink( String url ) {
        String hyperlink = 
            "<a href=\""+url+"\">" +
                url +
            "</a>";

        return hyperlink;
    }

    public static String htmlWrap( String html ) {
        String htmlWrap =
            "<html>"+
                "<body>"+
                    html +
                "</body>" +
            "</html>";

        return htmlWrap;
    }

    /**
     * Takes a form and returns a list of its errors.
     * @author Albert Giegerich
     */
    public static <T> List<String> getErrorList( Form<T> form ) {
        Map<String, List<ValidationError>> errorMap = form.errors();
        List<String> errorList = new ArrayList<>();
        for (String errorMapKey : errorMap.keySet() ) {
            List<ValidationError> validationErrors = errorMap.get( errorMapKey );
            for ( ValidationError error : validationErrors ) {
                errorList.add( error.message() );
            }
        }

        return errorList;
    }
}
