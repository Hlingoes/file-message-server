package cn.henry.study.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * description: 添加构成JWT的参数，头部+载荷+签名，头部（Header）{"typ": "JWT","alg": "HS256"}
 * {
 * "iss": "Online JWT Builder", //iss(Issuser)：代表这个JWT的签发主体；
 * "iat": 1416797419, //iat(Issued at)：是一个时间戳，代表这个JWT的签发时间；
 * "exp": 1448333419, //exp(Expiration time)：是一个时间戳，代表这个JWT的过期时间；
 * "aud": "www.example.com", //aud(Audience)：代表这个JWT的接收对象；
 * "sub": "jrocket@example.com", //sub(Subject)：代表这个JWT的主体，即它的所有人；
 * "GivenName": "Johnny",
 * "Surname": "Rocket",
 * "Email": "jrocket@example.com",
 * "Role": [ "Manager", "Project Administrator" ]
 * }
 *
 * @author Hlingoes
 * @date 2020/4/3 15:09
 */
public class TokenUtils {
    private static Logger log = LoggerFactory.getLogger(TokenUtils.class);
    private static long overTime = 7 * 24 * 60 * 60 * 1000L;

    /**
     * description: 由字符串生成加密key
     *
     * @param secret
     * @return javax.crypto.SecretKey
     * @author Hlingoes 2020/4/3
     */
    public static SecretKey generalKey(String secret) {
        byte[] encodedKey = Base64.decodeBase64(secret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * description: 解密jwt
     *
     * @param jsonWebToken
     * @param secret
     * @return io.jsonwebtoken.Claims
     * @author Hlingoes 2020/4/3
     */
    public static Claims parseJWT(String jsonWebToken, String secret) {
        SecretKey key = generalKey(secret);
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jsonWebToken)
                .getBody();
        return claims;
    }

    /**
     * description: 创建jwt
     *
     * @param subject
     * @param secret
     * @return java.lang.String
     * @author Hlingoes 2020/4/3
     */
    public static String createJwt(Object subject, String secret) {
        return createJwt(JacksonUtils.object2Str(subject), secret, overTime);
    }

    /**
     * description: 创建jwt
     *
     * @param subject
     * @param secret
     * @param TTLMillis
     * @return java.lang.String
     * @author Hlingoes 2020/4/3
     */
    public static String createJwt(String subject, String secret, long TTLMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        // 生成签名密钥
        Key signingKey = generalKey(secret);
        JwtBuilder builder = Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .claim("userid", uid)
                //iat(issued at): 在什么时候签发的(UNIX时间)，是否使用是可选的；
                .setIssuedAt(now)
                //sub: 该JWT所面向的用户，是否使用是可选的；
                .setSubject(subject)
                //iss: 该JWT的签发者，是否使用是可选的；
//                .setIssuer(issuer)
                //aud: 接收该JWT的一方，是否使用是可选的；
//                .setAudience(audience)
                .signWith(signatureAlgorithm, signingKey);
        // 添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            //exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
            builder.setExpiration(exp);
            //nbf (Not Before)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
            //.setNotBefore(now);
        }
        return builder.compact();
    }
}
