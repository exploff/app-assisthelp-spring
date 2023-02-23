package org.assisthelp.com.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//Nom de la propriété dans application.properties
@ConfigurationProperties(prefix = "rsa", ignoreUnknownFields = false)
public record RsaKeyConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
