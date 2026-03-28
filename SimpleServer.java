import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;

public class SimpleServer {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(5002)) {
            System.out.println("[Server] Listening on 5002");
            Socket s = ss.accept();
            System.out.println("[Server] Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);

            KeyPairGenerator dhGen = KeyPairGenerator.getInstance("DH");
            dhGen.initialize(2048);
            KeyPair dhPair = dhGen.generateKeyPair();

            String dhPub = Base64.getEncoder().encodeToString(dhPair.getPublic().getEncoded());
            out.println("DH_PUB:" + dhPub);

            String line = in.readLine();
            byte[] clientDh = Base64.getDecoder().decode(line.substring(7));
            KeyFactory kf = KeyFactory.getInstance("DH");
            PublicKey clientDhPub = kf.generatePublic(new X509EncodedKeySpec(clientDh));

            DHPublicKey dhk = (DHPublicKey) clientDhPub;
            DHParameterSpec p = dhk.getParams();
            System.out.println("p = " + p.getP());
            System.out.println("g = " + p.getG());

            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(dhPair.getPrivate());
            ka.doPhase(clientDhPub, true);
            ka.generateSecret();

            KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
            rsaGen.initialize(2048);
            KeyPair rsaPair = rsaGen.generateKeyPair();

            String rsaPub = Base64.getEncoder().encodeToString(rsaPair.getPublic().getEncoded());
            out.println("RSA_PUB:" + rsaPub);

            String encLine = in.readLine();
            byte[] encBytes = Base64.getDecoder().decode(encLine.substring(11));

            Cipher dec = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            dec.init(Cipher.DECRYPT_MODE, rsaPair.getPrivate());
            byte[] msg = dec.doFinal(encBytes);

            System.out.println("Decrypted: " + new String(msg, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
