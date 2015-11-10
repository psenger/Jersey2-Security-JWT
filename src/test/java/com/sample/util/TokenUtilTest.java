package com.sample.util;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Test;

import java.security.Key;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.*;

public class TokenUtilTest {
    @Test
    public void testExpiredToken() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 1);

        Key key = MacProvider.generateKey();

        // new KeyPair("publickey","privatekey")
        String token = TokenUtil.getJWTString("Phil", new String[]{"user"}, 11, calendar.getTime(), key);
        assertNotNull(token);
        try {
            Thread.sleep(1000L * 3L);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        assertEquals("Token should have expired", false, TokenUtil.isValid(token, key));
    }
//    @Test
//    public void testOtherKey() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MINUTE, 15);
//
//
//        String token = TokenUtil.getJWTString("Phil", new String[]{"user"}, 12, calendar.getTime(),  "abc" );
//        assertNotNull(token);
//
//        System.out.println("token = " + token);
//    }
//    @Test
//    public void testOtherKey() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MINUTE, 15);
//
//        KeyPairGenerator keyGenerator = null;
//        try {
//            keyGenerator = KeyPairGenerator.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        keyGenerator.initialize(1024);
//        KeyPair kp = keyGenerator.genKeyPair();
//        PublicKey publicKey = kp.getPublic();
//        PrivateKey privateKey = kp.getPrivate();
//
//        String token = TokenUtil.getJWTString("Phil", new String[]{"user"}, 12, calendar.getTime(),  publicKey );
//        assertNotNull(token);
//        try {
//            Thread.sleep(1000L * 2L);                 //1000 milliseconds is one second.
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//
//        assertEquals("Token should have been valid", true, TokenUtil.isValid(token, privateKey));
//    }


    @Test
    public void testNonExpiredToken() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);

        Key key = MacProvider.generateKey();

        // new KeyPair("publickey","privatekey")
        String token = TokenUtil.getJWTString("Phil", new String[]{"user"}, 12, calendar.getTime(), key);
        assertNotNull(token);
        try {
            Thread.sleep(1000L * 2L);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        assertEquals("Token should have been valid", true, TokenUtil.isValid(token, key));
    }

    @Test(expected = NullPointerException.class)
    public void testGetJWTString_invalidUserName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = null;
        String[] roles = new String[]{"user"};
        int version = 0;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        TokenUtil.getJWTString(username, roles, version, expires, key);
    }

    @Test(expected = NullPointerException.class)
    public void testGetJWTString_invalidRoles() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = null;
        int version = 0;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        TokenUtil.getJWTString(username, roles, version, expires, key);
    }

    @Test(expected = NullPointerException.class)
    public void testGetJWTString_invalidExpires() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 0;
        Date expires = null;
        Key key = MacProvider.generateKey();
        TokenUtil.getJWTString(username, roles, version, expires, key);
    }

    @Test(expected = NullPointerException.class)
    public void testGetJWTString_invalidKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 0;
        Date expires = calendar.getTime();
        Key key = null;
        TokenUtil.getJWTString(username, roles, version, expires, key);
    }

    @Test
    public void testGetVersionGood() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        int version1 = TokenUtil.getVersion(jwtString, key);
        assertEquals(version, version1);
    }

    @Test
    public void testGetVersionBad() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        int version1 = TokenUtil.getVersion(jwtString, MacProvider.generateKey());
        assertEquals(-1, version1);
    }

    @Test
    public void testGetRolesGood() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        String[] roles1 = TokenUtil.getRoles(jwtString, key);
        assertEquals(Arrays.toString(roles), Arrays.toString(roles1));
    }

    @Test
    public void testGetRolesBad() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        String[] roles1 = TokenUtil.getRoles(jwtString, MacProvider.generateKey());
        assertEquals(Arrays.toString(new String[]{}), Arrays.toString(roles1));
    }

    @Test
    public void testGetNamesGood() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        String username1 = TokenUtil.getName(jwtString, key);
        assertEquals(username, username1);
    }

    @Test
    public void testGetNamesBad() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 15);
        String username = "Bob";
        String[] roles = new String[]{"user"};
        int version = 3;
        Date expires = calendar.getTime();
        Key key = MacProvider.generateKey();
        String jwtString = TokenUtil.getJWTString(username, roles, version, expires, key);
        String username1 = TokenUtil.getName(jwtString, MacProvider.generateKey());
        assertNull(username1);
    }
}
