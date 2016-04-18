package vtech.xmb.grabber.db.services;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class MD5Test {

  @Test
  public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    String md5AsString = DigestUtils.md5Hex("asd".getBytes("UTF-8"));

    assertEquals("7815696ecbf1c96e6894b779456d330e", md5AsString);

  }
}
