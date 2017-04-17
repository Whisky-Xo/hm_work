package com.crm.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class RSAUtils {
	
	private static final String DEFAULT_PUBLIC_KEY=   
	        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfZY+P3NjOMmb2ieYg0WmQn1MzSfQkwz8wD2AR" + "\r" +  
	        "7VLFwOyJwkT7SqB/GVkWsQ5yhjyMZm9z+TPtxoZafYi2rdPZSRdSSMMSDn8OfY2Dy/aWiOZT3lUf" + "\r" +  
	        "aOOQ+jH2KCgET9Ytu64kgH7Jhs/RTzP34pWhds1bNEmRzxSdKyY6gy/SFwIDAQAB" + "\r";  
	      
	    private static final String DEFAULT_PRIVATE_KEY=  
	        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN9lj4/c2M4yZvaJ5iDRaZCfUzNJ" + "\r" +  
	        "9CTDPzAPYBHtUsXA7InCRPtKoH8ZWRaxDnKGPIxmb3P5M+3Ghlp9iLat09lJF1JIwxIOfw59jYPL" + "\r" +  
	        "9paI5lPeVR9o45D6MfYoKARP1i27riSAfsmGz9FPM/filaF2zVs0SZHPFJ0rJjqDL9IXAgMBAAEC" + "\r" +  
	        "gYEAwpU34ts+jPwh6wRaSqOdC7d7ROVZntviIf6Cc5r/yfgtECEC7M8n1Q1DKBy4tNBv1Os0kROz" + "\r" +  
	        "Q5z0UcWeW2A1cSDMPzzM3ppJvZBzvproq6Y9g7BJH+xaRhR2JL5PbNqWuuONwZM6aG1DVd/nD12z" + "\r" +  
	        "UJnpWbFfGjGMbGrhSUuHAaECQQD6RVLBixoA/NeSy3BVFCvUEBmz/UGWZDdSZWecuerlvkT4H5P7" + "\r" +  
	        "fkGHxEV7qBNqP9hemhQXggkNEXjVL2HB8mNtAkEA5IK+7VLu49YGFCLk64qme3bgJF22p+trK0l+" + "\r" +  
	        "LsbvMjeHNEeF6douKW9OUZiPcDJrIJY101Fbg5xKtZYs6ySVEwJBAPD25iMrRzJEP1s7PUDtVvWr" + "\r" +  
	        "OtQtt4SRoSJYOFaSOzRQ6h7saJLwkS+jLjNNNMRMDIupVkb8ELLga7L3F+yg8FECQFpJJnLSf7zZ" + "\r" +  
	        "hVFTcCt4fsrtbyYvOMokBBX4VbjZtQycT/liAREiuXZ5mfI3WwUzhow7jUzPKl9X01Tn3xuROq0C" + "\r" +  
	        "QCui5TDvTTFPEO9RllfUyG1OFL3EIMgvjzJIzAMVXNPzQQihHGutL7W6ShA5yqNngkhF0XyxrWv4" + "\r" +  
	        "nEAq3bCH7xE=" + "\r";
	
	    /** 
	     * 私钥 
	     */  
	    private RSAPrivateKey privateKey;  
	  
	    /** 
	     * 公钥 
	     */  
	    private RSAPublicKey publicKey;  
 
      /**
       * 得到公钥
       * @param key 密钥字符串（经过base64编码）
       * @throws Exception
       */
      public static PublicKey getPublicKey(String key) throws Exception {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
 
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
      }
      /**
       * 得到私钥
       * @param key 密钥字符串（经过base64编码）
       * @throws Exception
       */
      public static PrivateKey getPrivateKey(String key) throws Exception {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
 
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
      }
 
//      /**
//       * 得到密钥字符串（经过base64编码）
//       * @return
//       */
//      public static String getKeyString(Key key) throws Exception {
//            byte[] keyBytes = key.getEncoded();
//            String s = (new BASE64Encoder()).encode(keyBytes);
//            return s;
//      }
//      
      /** 
       * 从字符串中加载公钥 
       * @param publicKeyStr 公钥数据字符串 
       * @throws Exception 加载公钥时产生的异常 
       */  
      public void loadPublicKey(String publicKeyStr) throws Exception{  
          try {  
              BASE64Decoder base64Decoder= new BASE64Decoder();  
              byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);  
              KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
              X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
              this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);  
          } catch (NoSuchAlgorithmException e) {  
              throw new Exception("无此算法");  
          } catch (InvalidKeySpecException e) {  
              throw new Exception("公钥非法");  
          } catch (IOException e) {  
              throw new Exception("公钥数据内容读取错误");  
          } catch (NullPointerException e) {  
              throw new Exception("公钥数据为空");  
          }  
      }  
 
      /** 
       * 从字符串中加载私钥 
       * @param publicKeyStr 私钥数据字符串 
       * @throws Exception 加载私钥时产生的异常 
       */
      public void loadPrivateKey(String privateKeyStr) throws Exception{  
          try {  
              BASE64Decoder base64Decoder= new BASE64Decoder();  
              byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
              PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
              KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
              this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
          } catch (NoSuchAlgorithmException e) {  
              throw new Exception("无此算法");  
          } catch (InvalidKeySpecException e) {  
              throw new Exception("私钥非法");  
          } catch (IOException e) {  
              throw new Exception("私钥数据内容读取错误");  
          } catch (NullPointerException e) {  
              throw new Exception("私钥数据为空");  
          }  
      }  
      
      public static String etCipher(String json) {
    	  
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//        //密钥位数
//        keyPairGen.initialize(1024);
//        //密钥对
//        KeyPair keyPair = keyPairGen.generateKeyPair();
	  RSAUtils rsaEncrypt= new RSAUtils(); 
	  String cipherStr = "";
        // 公钥
	  try {
		rsaEncrypt.loadPublicKey(RSAUtils.DEFAULT_PUBLIC_KEY);

        // 私钥
		rsaEncrypt.loadPrivateKey(RSAUtils.DEFAULT_PRIVATE_KEY);

//        String publicKeyString = getKeyString(publicKey);
//        System.out.println("public:\n" + publicKeyString);
//
//        String privateKeyString = getKeyString(privateKey);
//        System.out.println("private:\n" + privateKeyString);

        //加解密类
        Cipher cipher = Cipher.getInstance("RSA");//Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //明文
        byte[] plainText = json.getBytes();

        //加密
        cipher.init(Cipher.ENCRYPT_MODE, rsaEncrypt.getPublicKey());
        byte[] enBytes = cipher.doFinal(plainText);
        
        
        //密文字符串
        cipherStr = (new BASE64Encoder()).encode(enBytes);
        
        //通过密钥字符串得到密钥
//        publicKey = getPublicKey(DEFAULT_PUBLIC_KEY);
//        privateKey = getPrivateKey(DEFAULT_PRIVATE_KEY);

//
//        byte[] keyBytes;
//        keyBytes = (new BASE64Decoder()).decodeBuffer(cipherStr);
//        //解密
//        cipher.init(Cipher.DECRYPT_MODE, rsaEncrypt.getPrivateKey());
//        byte[]deBytes = cipher.doFinal(keyBytes);
	  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } 
		return cipherStr;


  }
      
      public static String detCipher(String json) {
 
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//            //密钥位数
//            keyPairGen.initialize(1024);
//            //密钥对
//            KeyPair keyPair = keyPairGen.generateKeyPair();
    	  RSAUtils rsaEncrypt= new RSAUtils(); 
    	  String cipherStr = "";
            // 公钥
    	  try {
			rsaEncrypt.loadPublicKey(RSAUtils.DEFAULT_PUBLIC_KEY);
 
            // 私钥
			rsaEncrypt.loadPrivateKey(RSAUtils.DEFAULT_PRIVATE_KEY);
 
            //加解密类
            Cipher cipher = Cipher.getInstance("RSA");//Cipher.getInstance("RSA/ECB/PKCS1Padding");
 
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(json);
            //解密
            cipher.init(Cipher.DECRYPT_MODE, rsaEncrypt.getPrivateKey());
            byte[]deBytes = cipher.doFinal(keyBytes);
            cipherStr = new String(deBytes);
    	  } catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		  } 
			return cipherStr;
 
      }
      
      
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(RSAPrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(RSAPublicKey publicKey) {
		this.publicKey = publicKey;
	}
      
	public static void main(String[] args) throws UnsupportedEncodingException {
		String t = URLEncoder.encode(etCipher("2088802786380613"),"UTF-8");
		System.out.println(detCipher(URLDecoder.decode(t,"UTF-8")));
	}
 
}
