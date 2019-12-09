package com.agile.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class AesUtil {
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";

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
        return StringUtil.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
    }


    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey, String encryptIV, String algorithmstr) throws Exception {
        byte[] raw = encryptKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithmstr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptIV.getBytes(StandardCharsets.UTF_8));
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
    public static String aesEncrypt(String content, String encryptKey, String encryptIV, String algorithmstr) throws Exception {
        return StringUtil.isEmpty(content) ? null : base64Encode(aesEncryptToBytes(content, encryptKey, encryptIV, algorithmstr));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey, String encryptIV, String algorithmstr) throws Exception {
        byte[] raw = decryptKey.getBytes(StandardCharsets.US_ASCII);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);

        Cipher cipher = Cipher.getInstance(algorithmstr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptIV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes, StandardCharsets.UTF_8);
    }

    public static String aesEncrypt(String content, String encryptKey, String encryptIV) throws Exception {
        return aesEncrypt(content, encryptKey, encryptIV, "AES/CBC/PKCS5Padding");
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey, String encryptIV) throws Exception {
        return aesDecryptByBytes(encryptBytes, decryptKey, encryptIV, "AES/CBC/PKCS5Padding");
    }

    public static String aesDecrypt(String encryptStr, String decryptKey, String encryptIV) {
        return aesDecrypt(encryptStr, decryptKey, encryptIV, "AES/CBC/PKCS5Padding");
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey, String encryptIV, String algorithmstr) {
        try {
            return StringUtil.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey, encryptIV, algorithmstr);
        } catch (Exception e) {
            return encryptStr;
        }
    }
}
