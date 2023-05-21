package tv.memoryleakdeath.hex.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class CryptoUtil {
    private CryptoUtil() {
    }

    public static SecretKey generateKey(byte[] salt, String algorithm, String password, String keyAlgo) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), keyAlgo);
    }

    public static IvParameterSpec generateIv(int size) {
        byte[] iv = new byte[size];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] generateSalt(int size) {
        byte[] salt = new byte[size];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static SecretKey generateToken(int size, String algo) throws NoSuchAlgorithmException {
        KeyGenerator gen = KeyGenerator.getInstance(algo);
        gen.init(size, new SecureRandom());
        return gen.generateKey();
    }
}
