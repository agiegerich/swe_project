package controllers;

import models.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Locale;

public class Util {
    public static String getStackTrace( Throwable t ){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    public static String formatLongAsDollars( Long cents ) {
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
        String s = n.format(cents / 100.0);
        return s;
    }
}
