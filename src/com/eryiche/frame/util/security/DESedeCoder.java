package com.eryiche.frame.util.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 3DES加密算法组件
 * @author EX-XIAOFANQING001
 *
 */
public abstract class DESedeCoder {
	
	public static final String KEY_ALGORITHM = "DESede";
	
	public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";
	
	/**
	 * 二进制密钥还原成密钥对象
	 * @param keyBytes
	 * @return
	 * @throws Exception 
	 */
	private static Key toKey(byte[] keyBytes) throws Exception {
		DESedeKeySpec dks = new DESedeKeySpec(keyBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		return keyFactory.generateSecret(dks);
	}
	
	/**
	 * 生成一个对称加密的密钥
	 * @return 
	 * @throws Exception 
	 */
	public static byte[] initKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		
		// 初始化密钥长度
		keyGenerator.init(168);
		
		return keyGenerator.generateKey().getEncoded();
	}
	
	/**
	 * 用3DES算法对指定的数据用指定的密钥加密
	 * @param data 要加密的数据
	 * @param keyBytes 加密密钥
	 * @return 加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] keyBytes) throws Exception {
		Key key = toKey(keyBytes);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	/**
	 * 用3DES算法对指定的数据解密
	 * @param data 要解密的密文
	 * @param keyBytes 密钥
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] keyBytes) throws Exception {
		Key key = toKey(keyBytes);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
}
