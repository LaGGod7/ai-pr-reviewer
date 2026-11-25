package org.gd.backend.webhook;

import org.gd.backend.config.GitHubProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class WebhookSignatureVerifier {

    public static final String PREFIX = "sha256=";

    private final byte[] secret;

    public WebhookSignatureVerifier(GitHubProperties props) {
        this.secret = props.webhookSecret().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValid(String payload,String signatureHeader){
        if(signatureHeader==null || !signatureHeader.startsWith(PREFIX)){return false;}

        String expected = PREFIX + hmacSha256Hex(payload);
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),signatureHeader.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256Hex(String payload){
        try{
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret,"HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        }catch (Exception e){
            throw new IllegalStateException("HmacSHA256 is not available",e);
        }
    }
}
