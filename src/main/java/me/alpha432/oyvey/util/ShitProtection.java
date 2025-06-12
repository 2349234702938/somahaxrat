package me.alpha432.oyvey.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class ShitProtection {

    private static String getHWID() {
        try {
            String main = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getProperty("os.name");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(main.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static boolean isHWIDValid() {
        try {
            URL url = new URL("https://pastebin.com/raw/aDMrfGFM"); // replace with your pastebin raw URL
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            List<String> allowedHWIDs = reader.lines().collect(Collectors.toList());
            reader.close();

            return allowedHWIDs.contains(getHWID());
        } catch (Exception e) {
            return false;
        }
    }

    public static void checkHWID() {
        if (!isHWIDValid()) {
            JOptionPane.showMessageDialog(null,
                    "Your HWID is not whitelisted dumbass.\nHWID: " + getHWID(),
                    "Access is prohibited from this nigger computer >:(((",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0); // Exit without harming anything
        }
    }
}
