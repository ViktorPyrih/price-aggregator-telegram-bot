package ua.edu.cdu.vu.price.aggregator.telegram.bot.encryption;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class EncryptionTest {

    private static final int ITERATIONS = 1000;
    private static final String KEY = "JASYPT_ENCRYPTOR_PASSWORD";
    private static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";

    private static final StandardPBEStringEncryptor ENCRYPTOR = new StandardPBEStringEncryptor();

    @BeforeAll
    static void init() {
        ENCRYPTOR.setPassword(System.getenv(KEY));
        ENCRYPTOR.setAlgorithm(ALGORITHM);
        ENCRYPTOR.setKeyObtentionIterations(ITERATIONS);
        ENCRYPTOR.setIvGenerator(new RandomIvGenerator());
        ENCRYPTOR.setSaltGenerator(new RandomSaltGenerator());
    }

    @ParameterizedTest
    @ValueSource(strings = "")
    void encrypt(String value) {
        System.out.println(ENCRYPTOR.encrypt(value));
    }

    @ParameterizedTest
    @ValueSource(strings = "")
    void decrypt(String value) {
        System.out.println(ENCRYPTOR.decrypt(value));
    }
}
