//package ru.alferatz.auth.service;
//
//
//import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.keygen.BytesKeyGenerator;
//import org.springframework.security.crypto.keygen.KeyGenerators;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.Charset;
//import java.time.LocalDateTime;
//import ru.alferatz.auth.model.SecureToken;
//import ru.alferatz.auth.repository.VerificationTokenRepository;
//
//@Service
//@RequiredArgsConstructor
//public class DefaultSecureTokenService {
//
//  private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
//  private static final Charset US_ASCII = Charset.forName("US-ASCII");
//
//  @Value("${jdj.secure.token.validity}")
//  private int tokenValidityInSeconds;
//
//  private final VerificationTokenRepository verificationTokenRepository;
//
//  public SecureToken createSecureToken() {
//    String tokenValue = new String(
//        Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()),
//        US_ASCII); // this is a sample, you can adapt as per your security need
//    SecureToken secureToken = new SecureToken();
//    secureToken.setToken(tokenValue);
//    secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));
//    this.saveSecureToken(secureToken);
//    return secureToken;
//  }
//
//  public void saveSecureToken(SecureToken token) {
//    verificationTokenRepository.save(token);
//  }
//
//  public SecureToken findByToken(String token) {
//    return verificationTokenRepository.findByToken(token);
//  }
//
//  public void removeToken(SecureToken token) {
//    verificationTokenRepository.delete(token);
//  }
//
//  public void removeTokenByToken(String token) {
//    verificationTokenRepository.removeByToken(token);
//  }
//
//  public int getTokenValidityInSeconds() {
//    return tokenValidityInSeconds;
//  }
//}
