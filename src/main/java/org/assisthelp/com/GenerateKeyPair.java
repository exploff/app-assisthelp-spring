package org.assisthelp.com;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class GenerateKeyPair {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        //Generateur de clé privée/public RSA => Eviter d'utiliser OpenSSL, à insérer dans le dossier src/main/resources/certificat
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        var keyPair = keyPairGenerator.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        PemWriter pemWriter1 = new PemWriter(new OutputStreamWriter(new FileOutputStream("publicKey.pem")));
        pemWriter1.writeObject(new PemObject("PUBLIC KEY", publicKey));
        pemWriter1.flush();
        pemWriter1.close();
        PemWriter pemWriter2 = new PemWriter(new OutputStreamWriter(new FileOutputStream("privateKey.pem")));
        pemWriter2.writeObject(new PemObject("PRIVATE KEY", privateKey));
        pemWriter2.flush();
        pemWriter2.close();
    }
}
