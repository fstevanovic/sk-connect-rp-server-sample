/* Copyright (c) 2014 SecureKey Technologies Inc. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.securekey.samplerp.web;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.securekey.connect.beans.JsonObject;
import com.securekey.connect.beans.client.GetDeviceIdResponse;
import com.securekey.connect.beans.client.GetProvisioningAuthorizationCodeResponse;
import com.securekey.connect.beans.client.PairInitiationResponse;
import com.securekey.samplerp.service.BriidgeService;

/**
 * Implements mobile to RP server calls 
 * 
 * @author      Ivan Bilenjkij  (ivan.bilenjkij@securekey.com)
 * @author      Fedja Stevanovic (fedja.stevanovic@securekey.com)
 * @version     3.1
 * 
 *
 */
@Controller
public class BriidgeController {


	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired private BriidgeService briidgeService;

	@RequestMapping(value = "getProvisioningAuthorizationCodeSimple", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String getProvisioningAuthorizationCodeSimple () throws Exception {
		
		final GetProvisioningAuthorizationCodeResponse authCode = this.briidgeService.getProvisioningAuthorizationCode();
		return authCode.getTxnId();
	}

	@RequestMapping(value = "getProvisioningAuthorizationCode.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject getProvisioningAuthorizationCode () throws Exception {
		return this.briidgeService.getProvisioningAuthorizationCode();
	}
        
	@RequestMapping(value = "getDeviceId.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject getDeviceId (@RequestParam("txnId") String txnId) throws Exception {

		return this.briidgeService.deviceInitiatedGetDevice(txnId, null);
	}

	@RequestMapping(value = "getCardReadData.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject getCardReadData (@RequestParam("txnId") String txnId) throws Exception {

		return this.briidgeService.cardReadData(txnId);
	}

	@RequestMapping(value = "getDeviceInitiatedCardReadData.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject getDeviceInitiatedCardReadData (@RequestParam("txnId") String txnId) throws Exception {

		return this.briidgeService.deviceInitiatedCardReadData(txnId);
	}

	@RequestMapping(value = "initMobileQuickcode.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject initMobileQuickcode (@RequestParam("txnId") String txnId,
			@RequestParam("userId") String userId) throws Exception {

		final GetDeviceIdResponse digc = this.briidgeService.deviceInitiatedGetDevice(txnId, null);
		if (digc.getError() !=null) {
			return digc;
		} else {
			this.briidgeService.addUser(userId, null);
			this.briidgeService.addDevice(userId, digc.getDeviceInfo().getDeviceId(), true);
			return this.briidgeService.initSetQuickCode(userId, null);
		}
	}

	@RequestMapping(value = "verifyLogin.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Map<String,String> verifyLogin (@RequestBody Map<String, String> request) {

		log.info("Login for user: " + request.get("userId"));
		return Collections.singletonMap("status", "Success");
	}

	@RequestMapping(value = "verifyQuickcode.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody JsonObject verifyQuickcode (@RequestParam("txnId") String txnId) throws Exception {

		return this.briidgeService.verifyQuickCode(txnId);
	}

	@RequestMapping(value = "getPairingCode.json", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String getPairingCode (@RequestParam("userId") String userId) throws Exception {

		this.briidgeService.addUser(userId, null);

		// we won't set language or other parameters except mandatory expiry data (30 min)
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 30);
		Date expiryDate = calendar.getTime();

		final PairInitiationResponse pd = this.briidgeService.pairDevice(userId, "", "", null, expiryDate, Boolean.TRUE, "", "");

		if (pd.getError() != null) {
			return "ERROR: " + pd.getError() + " " + pd.getErrorDescription();
		} else {
			return "PAIRING CODE: " +  pd.getPairCode();
		}
	}

	@RequestMapping(value = "verifyJWT.json", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String verifyJWT (@RequestParam("jwt") String jwt) throws Exception {

		JWSObject jws = JWSObject.parse(jwt);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(jws.getHeader().getX509CertURL().toString());
		request.addHeader("Accept", "text/plain");

		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			if(entity != null) {	

				String pemFileContent = entity == null ? null : EntityUtils.toString(entity);	
				PemReader pemReader = new PemReader(new StringReader(pemFileContent));
				byte[] pubK = pemReader.readPemObject().getContent();
				pemReader.close();		
				Certificate serverCert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(pubK));
				pemReader.close();

				if (serverCert instanceof X509Certificate) {

					X509Certificate cert = (X509Certificate) serverCert;
					PublicKey publicKey = cert.getPublicKey();
					if (publicKey instanceof RSAPublicKey) {
						JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
						if (jws.verify(verifier)) {
							return "{\"status\":\"jwt_verified\"}";
						} else {
							return "{\"status\":\"jwt_verify_fail\"}";
						}
					} else {
						return "{\"status\":\"jwt_pub_key_not_rsa\"}";
					}

				} else {
					return "{\"status\":\"jwt_pem_not_cert\"}";
				}
			} else {
				return "{\"status\":\"jwt_pem_download_fail\"}";
			}
		} catch (IOException e) {
			return "{\"status\":\"jwt_pem_download_fail\"}";
		}
	}
}
