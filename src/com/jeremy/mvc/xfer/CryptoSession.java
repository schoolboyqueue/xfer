

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Jeremy
 */
public class CryptoSession {
    private static final String ENCRYPTION_ALGORITHM="RSA";
    private static final String SESSION_KEY_CIPHER="RSA/ECB/PKCS1Padding";
    private static final String DATA_CIPHER="Rijndael/CBC/PKCS5Padding";
    private static final String DATA_KEY_TYPE="Rijndael";
    
    private byte[] iv = new byte[16];
    private byte[] receivedIv = new byte[16];
    private byte[] publicKeyBytes;
    private byte[] privateKeyBytes;
    private byte[] encryptedSessionKeyBytes;
    private byte[] receivedPublicKeyBytes;
    private byte[] sessionKeyBytes;
    private Cipher cipher;
    private Cipher sessionKeyCipher;
    private KeyFactory keyFactory;
    private IvParameterSpec encryptSpec;
    private IvParameterSpec decryptSpec;
    private X509EncodedKeySpec encryptKeySpec;
    private PKCS8EncodedKeySpec decryptKeySpec;
    private KeyGenerator keyGenerator;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Key genSessionKey;
    private SecretKey recSessionKey;
    private SecureRandom random;
    
    public CryptoSession() {
        try {
            cipher = Cipher.getInstance(DATA_CIPHER);
            sessionKeyCipher = Cipher.getInstance(SESSION_KEY_CIPHER);
            keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            keyGenerator = KeyGenerator.getInstance(DATA_KEY_TYPE);
            keyGenerator.init(128);
            random = new SecureRandom();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setRecievedPublicKey(byte[] key) {
        try {
            receivedPublicKeyBytes = key;
            encryptKeySpec = new X509EncodedKeySpec(receivedPublicKeyBytes);
            publicKey = keyFactory.generatePublic(encryptKeySpec);
            sessionKeyCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            genSessionKey = keyGenerator.generateKey();
            encryptedSessionKeyBytes = sessionKeyCipher.doFinal(genSessionKey.getEncoded());
        } catch (InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setRecievedIV(byte[] iv) {
        receivedIv = iv;
        encryptSpec = new IvParameterSpec(receivedIv);
    }
    public byte[] getPublicKey() {
        return publicKeyBytes;
    }
    public byte[] getEncryptedSessionKey() {
        return encryptedSessionKeyBytes;
    }
    public byte[] getIv() {
        return iv;
    }
    public void setSessionkey(byte[] recievedSessionKeyBytes) {
        try {
            sessionKeyCipher.init(Cipher.DECRYPT_MODE, privateKey);
            sessionKeyBytes = sessionKeyCipher.doFinal(recievedSessionKeyBytes);
            recSessionKey = new SecretKeySpec(sessionKeyBytes, DATA_KEY_TYPE);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void genKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            privateKeyBytes = keyPair.getPrivate().getEncoded();
            publicKeyBytes = keyPair.getPublic().getEncoded(); 
            decryptKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(decryptKeySpec);
            random.nextBytes(iv);
            decryptSpec = new IvParameterSpec(iv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public byte[] encrypt(byte[] userBytes) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, genSessionKey, encryptSpec);
            return cipher.doFinal(userBytes);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public byte[] decrypt(byte[] userBytes) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, recSessionKey, decryptSpec);
            return cipher.doFinal(userBytes);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            Logger.getLogger(CryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
