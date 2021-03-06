package org.knowm.xchange.btcmarkets.service;

import java.util.Base64;
import javax.crypto.Mac;
import javax.ws.rs.HeaderParam;
import org.knowm.xchange.service.BaseParamsDigest;
import si.mazi.rescu.RestInvocation;

public class BTCMarketsDigest extends BaseParamsDigest {

  public BTCMarketsDigest(String secretKey) {
    super(decodeBase64(secretKey), HMAC_SHA_512);
  }

  @Override
  public String digestParams(RestInvocation inv) {
    final String nonce = inv.getParamValue(HeaderParam.class, "timestamp").toString();
    return digest(inv.getMethodPath(), nonce, inv.getRequestBody());
  }

  String digest(String url, String nonce, String requestBody) {
    Mac mac256 = getMac();
    if (!url.startsWith("/")) {
      url = "/" + url;
    }
    mac256.update(url.getBytes());
    mac256.update("\n".getBytes());
    mac256.update(nonce.getBytes());
    mac256.update("\n".getBytes());
    if (requestBody != null && !requestBody.isEmpty()) {
      mac256.update(requestBody.getBytes());
    }

    return Base64.getEncoder().encodeToString(mac256.doFinal());
  }
}
