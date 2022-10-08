package org.tjl.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 密码加盐工具类
 * @author tjl
 */
public class SaltUtil {
    // 设置随机盐值最大长度
    // SALT_MIN_LENGTH ~ 32
    private static final int SALT_MAX_LENGTH = 32;
    // 设置随机盐值最小长度
    // 1 ~ SALT_MAX_LENGTH
    private static final int SALT_MIN_LENGTH = 16;

    private static final Map<String ,Integer> algorithmLengthMap = new HashMap<>();
    static {
        algorithmLengthMap.put("MD2",32);
        algorithmLengthMap.put("MD5",32);
        algorithmLengthMap.put("SHA-1",40);
        algorithmLengthMap.put("SHA-224",56);
        algorithmLengthMap.put("SHA-256",64);
        algorithmLengthMap.put("SHA-384",96);
        algorithmLengthMap.put("SHA-512",128);
    }

    private static void verifyConfig() throws Exception {
        if (SALT_MIN_LENGTH > SALT_MAX_LENGTH || SALT_MIN_LENGTH < 1 || SALT_MAX_LENGTH > 32) {
            throw new Exception("illegal Config value.");
        }
    }

    /**
     * 数据加盐
     * @param data 待加盐数据
     * @param algorithm 摘要算法，可选：MD2 / MD5 / SHA-1 / SHA-224 / SHA-256 / SHA-384 / SHA-512
     * @return 加盐后的密文
     * @throws Exception
     */
    public static String saltEncrypt(String data, String algorithm) throws Exception {
        verifyConfig();
        int saltLength = SALT_MAX_LENGTH - (int)(Math.random()*(SALT_MAX_LENGTH-SALT_MIN_LENGTH+1));
        String salt = UUID.randomUUID().toString().replaceAll("-", "").substring(0, saltLength);
        return salt + MDUtil.getMessageDigest((salt + data).getBytes(StandardCharsets.UTF_8), algorithm.toUpperCase());
    }

    /**
     * 密文验证
     * @param data 待验证数据
     * @param encryptedValue 加盐后的密文
     * @param algorithm 摘要算法，可选：MD2 / MD5 / SHA-1 / SHA-224 / SHA-256 / SHA-384 / SHA-512
     * @return 验证结果
     * @throws Exception
     */
    public static boolean verifyEncryptedValue(String data, String encryptedValue, String algorithm) throws Exception {
        try {
            MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
        // 从密文中获取盐值
        String salt = encryptedValue.substring(0, encryptedValue.length() - algorithmLengthMap.get(algorithm.toUpperCase()));
        // 从密文中获取哈希值
        String hash = encryptedValue.substring(salt.length());
        return MDUtil.getMessageDigest((salt + data).getBytes(StandardCharsets.UTF_8), algorithm.toUpperCase()).equals(hash);
    }

    public static void main(String[] args) throws Exception {
        String str = "Hello World.";
        String encryptedValue = saltEncrypt(str, "sha-256");
        // 加盐
        System.out.println(encryptedValue);
        // 验证
        System.out.println(verifyEncryptedValue(str,encryptedValue,"sha-256"));
    }
}
