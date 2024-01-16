package com.teamchallenge.bookti.security.jwt;

import com.teamchallenge.bookti.exception.KeyPairException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Component class that contains KeyPair utils.
 *
 * @author Maksym Reva
 */
@Component
@Slf4j
public class KeyPairUtils {

  @Value("${jwt.access-token.private}")
  private String accessTokenPrivateKeyPath;
  @Value("${jwt.access-token.public}")
  private String accessTokenPublicKeyPath;
  @Value("${jwt.refresh-token.private}")
  private String refreshTokenPrivateKeyPath;
  @Value("${jwt.refresh-token.public}")
  private String refreshTokenPublicKeyPath;

  private KeyPair accessTokenKeyPair;
  private KeyPair refreshTokenKeyPair;

  public RSAPublicKey getAccessTokenPublicKey() {
    return (RSAPublicKey) getAccessTokenKeyPair().getPublic();
  }

  public RSAPrivateKey getAccessTokenPrivateKey() {
    return (RSAPrivateKey) getAccessTokenKeyPair().getPrivate();
  }

  public RSAPublicKey getRefreshTokenPublicKey() {
    return (RSAPublicKey) getRefreshTokenKeyPair().getPublic();
  }

  public RSAPrivateKey getRefreshTokenPrivateKey() {
    return (RSAPrivateKey) getRefreshTokenKeyPair().getPrivate();
  }

  private KeyPair getAccessTokenKeyPair() {
    if (Objects.isNull(accessTokenKeyPair)) {
      log.info("Access token pair is null, creating a new one...");
      this.accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
    }
    return accessTokenKeyPair;
  }

  private KeyPair getRefreshTokenKeyPair() {
    if (Objects.isNull(refreshTokenKeyPair)) {
      log.info("Refresh token pair is null, creating a new one...");
      this.refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
    }
    return refreshTokenKeyPair;
  }

  private KeyPair getKeyPair(String publicKeyPath, String privateKeyPath) {
    File publicKeyFile = new File(publicKeyPath);
    File privateKeyFile = new File(privateKeyPath);

    if (!publicKeyFile.exists() && !privateKeyFile.exists()) {
      log.info("Generating new public and private keys: {}, {}", publicKeyPath, privateKeyPath);
      return generateKeyPair(publicKeyPath, privateKeyPath);
    }

    try {
      log.info(
          "Public and private keys found, start loading...: {}, {}", publicKeyPath, privateKeyPath
      );
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");

      byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

      byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
      PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

      return new KeyPair(publicKey, privateKey);
    } catch (NoSuchAlgorithmException e) {
      log.error("No provider to support provided algorithm: {}", e.getMessage());
      throw new KeyPairException(e.getMessage());
    } catch (IOException e) {
      log.error("Failed to read a file: {}", e.getMessage());
      throw new KeyPairException(e.getMessage());
    } catch (InvalidKeySpecException e) {
      log.error("Invalid KeySpec: {}", e.getMessage());
      throw new KeyPairException(e.getMessage());
    }
  }

  private KeyPair generateKeyPair(String publicKeyPath, String privateKeyPath) {
    File dir = new File("access-refresh-token-keys");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      try (var outStream = new FileOutputStream(publicKeyPath)) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
        outStream.write(keySpec.getEncoded());
      }
      try (var outStream = new FileOutputStream(privateKeyPath)) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
        outStream.write(keySpec.getEncoded());
      }
      return keyPair;
    } catch (NoSuchAlgorithmException e) {
      log.error("No provider to support provided algorithm: {}", e.getMessage());
      throw new KeyPairException(e.getMessage());
    } catch (IOException e) {
      log.error("Failed to read a file: {}", e.getMessage());
      throw new KeyPairException(e.getMessage());
    }
  }

}
