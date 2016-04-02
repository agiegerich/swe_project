package constants;

import java.util.Arrays;
import java.util.List;

public class R {
    public static boolean didSetup = false;

    // AES values must be 16 alphanumeric characters (128 bits)
    public static final String AES_KEY="abcdefghijklmnozqrstuvwxyzabcdefghijklmnozqrstuvwxyz";
    public static final String AES_IV ="team10forever1234567";

    public static final List<String> categories = Arrays.asList( "all", "automotive", "clothing", "electronics");
}
