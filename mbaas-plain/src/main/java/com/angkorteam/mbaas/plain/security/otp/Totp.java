package com.angkorteam.mbaas.plain.security.otp;

import com.angkorteam.mbaas.plain.security.otp.api.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by socheat on 4/3/16.
 */
public class Totp {

    private final String secret;
    private final Clock clock;
    private static final int DELAY_WINDOW = 1;

    /**
     * Initialize an OTP instance with the shared secret generated on Registration process
     *
     * @param secret Shared secret
     */
    public Totp(String secret) {
        this.secret = secret;
        clock = new Clock();
    }

    /**
     * Initialize an OTP instance with the shared secret generated on Registration process
     *
     * @param secret Shared secret
     * @param clock  Clock responsible for retrieve the current interval
     */
    public Totp(String secret, Clock clock) {
        this.secret = secret;
        this.clock = clock;
    }

    /**
     * Prover - To be used only on the client side
     * Retrieves the encoded URI to generated the QRCode required by Google Authenticator
     *
     * @param name Account name
     * @return Encoded URI
     */
    public String uri(String name) {
        try {
            return String.format("otpauth://totp/%s?secret=%s", URLEncoder.encode(name, "UTF-8"), secret);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the current OTP
     *
     * @return OTP
     */
    public String now() {
        return leftPadding(hash(secret, clock.getCurrentInterval()));
    }

    /**
     * Verifier - To be used only on the server side
     * <p>
     * Taken from Google Authenticator with small modifications from
     * {@see <a href="http://code.google.com/p/google-authenticator/source/browse/src/com/google/android/apps/authenticator/PasscodeGenerator.java?repo=android#212">PasscodeGenerator.java</a>}
     * <p>
     * Verify a timeout code. The timeout code will be valid for a time
     * determined by the interval period and the number of adjacent intervals
     * checked.
     *
     * @param otp Timeout code
     * @return True if the timeout code is valid
     * <p>
     * Author: sweis@google.com (Steve Weis)
     */
    public boolean verify(String otp) {

        long code = Long.parseLong(otp);
        long currentInterval = clock.getCurrentInterval();

        int pastResponse = Math.max(DELAY_WINDOW, 0);

        for (int i = pastResponse; i >= 0; --i) {
            int candidate = generate(this.secret, currentInterval - i);
            if (candidate == code) {
                return true;
            }
        }
        return false;
    }

    private int generate(String secret, long interval) {
        return hash(secret, interval);
    }

    private int hash(String secret, long interval) {
        byte[] hash = new byte[0];
        try {
            //Base32 encoding is just a requirement for google authenticator. We can remove it on the next releases.
            hash = new Hmac(Hash.SHA1, Base32.decode(secret), interval).digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Base32.DecodingException e) {
            e.printStackTrace();
        }
        return bytesToInt(hash);
    }

    private int bytesToInt(byte[] hash) {
        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);

        return binary % Digits.SIX.getValue();
    }

    private String leftPadding(int otp) {
        return String.format("%06d", otp);
    }

}