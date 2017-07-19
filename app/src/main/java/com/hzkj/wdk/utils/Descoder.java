package com.hzkj.wdk.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密类
 * 
 * @author Lin
 * 
 */
public class Descoder {

	/**
	 * 加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要加密的值
	 * */
	public static String encode(String key, String value) {
		if (value == null || value.length() == 0)
			return null;

		byte[] data = encode(key.getBytes(), value);
		String result = Base64.encodeToString(data, Base64.DEFAULT);
		return result;
	}

	/**
	 * 加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要加密的值
	 * */
	public static byte[] encode(byte[] key, String value) {
		if (value == null || value.length() == 0)
			return null;

		return encode(key, value.getBytes());
	}

	/**
	 * 加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要加密的值
	 * */
	public static byte[] encode(byte[] key, byte[] value) {
		if (value == null)
			return null;

		SecureRandom random = new SecureRandom();

		try {
			DESKeySpec desKey = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			SecretKey securekey = keyFactory.generateSecret(desKey);

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			return cipher.doFinal(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 解加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要解密的值(需为base64值)
	 * */
	public static String decode(String key, String value) {
		if (value == null || value.length() == 0)
			return null;

		byte[] valueData = Base64.decode(value, Base64.DEFAULT);
		byte[] data = decode(key.getBytes(), valueData);
		return new String(data);
	}

	/**
	 * 解加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要解密的值(经过base64加密)
	 * */
	public static byte[] decode(byte[] key, String value) {
		if (value == null || value.length() == 0)
			return null;

		byte[] valueData = Base64.decode(value, Base64.DEFAULT);
		return decode(key, valueData);
	}

	/**
	 * 解加密字符串
	 * 
	 * @param key
	 *            密钥
	 * @param value
	 *            要解密的值
	 * */
	public static byte[] decode(byte[] key, byte[] value) {
		if (value == null)
			return null;

		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			return cipher.doFinal(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
