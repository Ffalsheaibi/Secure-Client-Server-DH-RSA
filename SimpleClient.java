import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;

public class SimpleClient {
    public static void main(String[] args) {
        try (Socket s = new Socket("127.0.0.1", 5002)) {
            System.out.println("[Client] Connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);

            String serverLine = in.readLine();
            byte[] serverDh = Base64.getDecoder().decode(serverLine.substring(7));

            KeyFactory kf = KeyFactory.getInstance("DH");
            PublicKey serverDhPub = kf.generatePublic(new X509EncodedKeySpec(serverDh));
            DHPublicKey dhk = (DHPublicKey) serverDhPub;
            DHParameterSpec p = dhk.getParams();

            System.out.println("p = " + p.getP());
            System.out.println("g = " + p.getG());

            KeyPairGenerator dhGen = KeyPairGenerator.getInstance("DH");
            dhGen.initialize(p);
            KeyPair dhPair = dhGen.generateKeyPair();

            String dhPub = Base64.getEncoder().encodeToString(dhPair.getPublic().getEncoded());
            out.println("DH_PUB:" + dhPub);

            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(dhPair.getPrivate());
            ka.doPhase(serverDhPub, true);
            ka.generateSecret();

            String rsaLine = in.readLine();
            byte[] rsaPub = Base64.getDecoder().decode(rsaLine.substring(8));

            KeyFactory kfr = KeyFactory.getInstance("RSA");
            PublicKey rsaPublic = kfr.generatePublic(new X509EncodedKeySpec(rsaPub));

            String msg = "Hello from client";
            Cipher enc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            enc.init(Cipher.ENCRYPT_MODE, rsaPublic);
            byte[] c = enc.doFinal(msg.getBytes("UTF-8"));
            String b64 = Base64.getEncoder().encodeToString(c);

            out.println("CIPHERTEXT:" + b64);
            System.out.println("[Client] Sent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
