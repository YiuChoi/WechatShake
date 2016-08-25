package name.caiyao.wechatshake;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import static name.caiyao.wechatshake.SecurityUtils.KEY_ALGORITHM;
import static name.caiyao.wechatshake.SecurityUtils.SIGNATURE_ALGORITHM;
import static name.caiyao.wechatshake.SecurityUtils.decryptBASE64;
import static name.caiyao.wechatshake.SecurityUtils.encryptBASE64;

/**
 * Created by 蔡小木 on 2016/8/25 0025.
 */

public class Test {
    public static void main(String[] args) {
        String privateKey = "AAABACiSm0jsvv+1CY+mnZ0v3RzC0cQosNR9/35umKjPD0UOBqdnqNqatEoVytHD" +
                "oEIMjDeIGtg+Fb0LbD6HTcpKfThirTav4Lv+8rXGxrXwEfS2p+YAcyP4erZNJc3n" +
                "jscS7A+6QNSDHfNadv9QefOQ2RZhJselSKR+qVpenqt3jnYu/QO2gbpWh6iiDo2y" +
                "r5PzlCBYYYykJgVdLYYU0bZl2i4rGbNh1lLHhBuUilI7epL/bEuV+ve1H1Hi4jVd" +
                "fGh+iksq8QQwcE1JOUZkrv3ZkR2kTB1pGXfyQBgFpTB02F2QAWo7PywwOrMqqjBV" +
                "XYQhJ53Vbbu7jYmuR+V5reM3J20AAACBAPwfnE4civIwYOd8oIcuarOZeqd5kOXR" +
                "R4IS9oWMsNtnNSFSTSOB1QSTABZ032nG1H7A+K3gI4AjMc8wkVcmgT6CLqdt1mzM" +
                "SqCtEOmOOuZpbMPjBnxrq8ADHzqR/kwefb6on/jAO0Q4hnbQQInC2mf4Wua4nBqH" +
                "Gt3eYxUkJk3FAAAAgQC+iKn3JCHF0Cg3DhLvPSTGpxgGWIywznvzRqF94w2+zhoP" +
                "MARfzwh2Ug28FwLu5Y2M6bUYXoyJBvwCVqgnclRN1QMBOagTsRweV9C4Z4x24LcV" +
                "b7Y6lovL70S1ibKLCQgNjp07ZWKzVXojPfyUNxugQeNWz+vzCKM7yquM7jwH9wAA" +
                "AIEA1xt39ED2oOxfWXsFM4aCp2uZdIqDo7mNNGFOJpwLfqZgHbbtC3Hj8wSNNDBe" +
                "Umt/oIo4dGuvEEJRiAY3lLGiB9wfjq7RRVrtGoPbBfUp2R2rcquKpCnW0qrSiNb2" +
                "JjoYFCixLzbRXGxNXyS49MO3q6D5EHTdvYn1JH+6HRb8APQ=";

        try {
            System.out.println(new String(encryptByPrivateKey("ss".getBytes(),privateKey)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        return encryptBASE64(signature.sign());
    }

    /**
     * 用私钥加密
     * @param data	加密数据
     * @param key	密钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data,String key)throws Exception{
        //解密密钥
        byte[] keyBytes = decryptBASE64(key);
        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 用私钥解密 * @param data 	加密数据
     * @param key	密钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data,String key)throws Exception{
        //对私钥解密
        byte[] keyBytes = decryptBASE64(key);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

}
