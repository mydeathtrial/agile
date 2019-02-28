package com.agile.common.util;

import com.agile.common.properties.SecurityProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/27 16:48
 * @Description TODO
 * @since 1.0
 */
public class AesUtil {
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 密钥
     */
    private static String key;
    /**
     * 偏移量
     */
    private static String iv;
    /**
     * 算法模式
     */
    private static String algorithmstr;

    static {
        SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);
        assert securityProperties != null;
        key = securityProperties.getAesKey();
        iv = securityProperties.getAesOffset();
        algorithmstr = securityProperties.getAlgorithmModel();
    }

    /**
     * AES解密
     *
     * @param encrypt 待解密内容
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encrypt) {
        try {
            return aesDecrypt(encrypt, key, iv);
        } catch (Exception e) {
            return encrypt;
        }
    }

    /**
     * AES加密
     *
     * @param content 需加密内容
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String content) {
        try {
            return aesEncrypt(content, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        // 这里的1代表正数
        return new BigInteger(1, bytes).toString(radix);
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     */
    public static byte[] base64Decode(String base64Code) {
        return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
    }


    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey, String encryptIV) throws Exception {
        byte[] raw = encryptKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithmstr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptIV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey, String encryptIV) throws Exception {
        return StringUtils.isEmpty(content) ? null : base64Encode(aesEncryptToBytes(content, encryptKey, encryptIV));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey, String encryptIV) throws Exception {
        byte[] raw = decryptKey.getBytes(StandardCharsets.US_ASCII);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);

        Cipher cipher = Cipher.getInstance(algorithmstr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptIV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey, String encryptIV) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey, encryptIV);
    }
}