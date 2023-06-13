package diotviet.server.utils;

import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Other utility
 */
public abstract class OtherUtils {

    // ****************************
    // Public API
    // ****************************

    /**
     * Try to get o1, if it's null, get o2 instead
     *
     * @param o1
     * @param o2
     * @return
     */
    public static Object get(Object o1, Object o2) {
        return Objects.nonNull(o1) ? o1 : o2;
    }

    /**
     * Sort
     *
     * @param list
     * @return
     */
    public static List<String> sort(List<String> list) {
        // Sort list
        Collections.sort(list);

        return list;
    }

    /**
     * Hash
     *
     * @param bytes
     * @return
     */
    public static String hash(byte[] bytes) {
        // Output
        String hash = "";

        try {
            // Digest the file at path
            byte[] messageDigest = MessageDigest
                    .getInstance("SHA-512")
                    .digest(bytes);

            // Convert message digest into hex value and append 0 to make it 256bit
            hash = new String(Hex.encode(messageDigest));
        } catch (NoSuchAlgorithmException e) {
            // This should not happen
            e.printStackTrace();
        }

        return hash;
    }
}
