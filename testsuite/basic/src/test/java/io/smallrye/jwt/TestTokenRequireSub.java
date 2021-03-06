package io.smallrye.jwt;

import static org.eclipse.microprofile.jwt.tck.TCKConstants.TEST_GROUP_JWT;
import static org.eclipse.microprofile.jwt.tck.TCKConstants.TEST_ISSUER;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.jwt.tck.util.TokenUtils;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;

public class TestTokenRequireSub extends Arquillian {
    @Test(groups = TEST_GROUP_JWT, description = "validate sub")
    public void defaultSubAvailable() throws Exception {
        HashMap<String, Long> timeClaims = new HashMap<>();
        String token = TokenUtils.generateTokenString("/Token1.json", null, timeClaims);
        PublicKey publicKey = TokenUtils.readPublicKey("/publicKey.pem");
        if (publicKey == null) {
            throw new IllegalStateException("Failed to load /publicKey.pem resource");
        }

        JWTAuthContextInfo contextInfo = new JWTAuthContextInfo((RSAPublicKey) publicKey, TEST_ISSUER);
        JWTCallerPrincipalFactory factory = JWTCallerPrincipalFactory.instance();
        JsonWebToken jwt = factory.parse(token, contextInfo);
        String sub = jwt.getSubject();
        Assert.assertEquals(sub, "24400320");
    }

    @Test(groups = TEST_GROUP_JWT, description = "validate sub fail", expectedExceptions = ParseException.class)
    public void defaultSubNotAvailable() throws Exception {
        HashMap<String, Long> timeClaims = new HashMap<>();
        String token = TokenUtils.generateTokenString("/TokenSubPath.json", null, timeClaims);
        PublicKey publicKey = TokenUtils.readPublicKey("/publicKey.pem");
        if (publicKey == null) {
            throw new IllegalStateException("Failed to load /publicKey.pem resource");
        }

        JWTAuthContextInfo contextInfo = new JWTAuthContextInfo((RSAPublicKey) publicKey, TEST_ISSUER);
        JWTCallerPrincipalFactory factory = JWTCallerPrincipalFactory.instance();
        factory.parse(token, contextInfo);
    }

    @Test(groups = TEST_GROUP_JWT, description = "no sub validation")
    public void noSubValidation() throws Exception {
        HashMap<String, Long> timeClaims = new HashMap<>();
        String token = TokenUtils.generateTokenString("/TokenSubPath.json", null, timeClaims);
        PublicKey publicKey = TokenUtils.readPublicKey("/publicKey.pem");
        if (publicKey == null) {
            throw new IllegalStateException("Failed to load /publicKey.pem resource");
        }

        JWTAuthContextInfo contextInfo = new JWTAuthContextInfo((RSAPublicKey) publicKey, TEST_ISSUER);
        contextInfo.setRequireNamedPrincipal(false);
        JWTCallerPrincipalFactory factory = JWTCallerPrincipalFactory.instance();
        JsonWebToken jwt = factory.parse(token, contextInfo);
        String sub = jwt.getSubject();
        Assert.assertNull(sub);
    }
}
