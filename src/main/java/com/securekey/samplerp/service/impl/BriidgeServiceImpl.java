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

package com.securekey.samplerp.service.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.securekey.connect.beans.Response;
import com.securekey.connect.beans.client.CancelRequestRequest;
import com.securekey.connect.beans.client.GenerateQuickCodeRequest;
import com.securekey.connect.beans.client.GetDataRequest;
import com.securekey.connect.beans.client.GetDeviceIdResponse;
import com.securekey.connect.beans.client.GetProvisioningAuthorizationCodeResponse;
import com.securekey.connect.beans.client.NotificationType;
import com.securekey.connect.beans.client.PairInitiationResponse;
import com.securekey.connect.beans.client.ReadCardResponse;
import com.securekey.connect.beans.client.TxnResponse;
import com.securekey.connect.beans.client.VerifyQuickCodeResponse;
import com.securekey.connect.beans.mgmt.AddDeviceRequest;
import com.securekey.connect.beans.mgmt.AddUserRequest;
import com.securekey.connect.beans.mgmt.BaseMgmtRequest;
import com.securekey.connect.beans.mgmt.DeviceFilter;
import com.securekey.connect.beans.mgmt.GetDeviceByIdRequest;
import com.securekey.connect.beans.mgmt.GetDevicesResponse;
import com.securekey.connect.beans.mgmt.GetUserResponse;
import com.securekey.connect.beans.mgmt.PairingRequest;
import com.securekey.connect.beans.mgmt.Phone;
import com.securekey.connect.beans.mgmt.RemoveAllUserDevicesRequest;
import com.securekey.connect.beans.mgmt.RemoveDeviceRequest;
import com.securekey.connect.beans.mgmt.UpdateUserRequest;
import com.securekey.connect.beans.mgmt.VerifyDeviceRequest;
import com.securekey.connect.clientsdk.SKClient;
import com.securekey.samplerp.service.BriidgeService;

/**
 * Implements calls to briidge.Net Connect service
 * 
 * @author      Ivan Bilenjkij (ivan.bilenjkij@securekey.com)
 * 
 */
@Service("briidgeService")
public class BriidgeServiceImpl implements BriidgeService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("#{system['keystoreFile']}")
	private String keyStoreFile;

	@Value("#{system['keystoreFilePassword']}")
	private String keyStoreFilePassword;

	@Value("#{system['briidgeServerUrl']}")
	private String briidgeServerUrl;

	private SKClient skClient;

	@PostConstruct
	private void initBriidgeService() {
		log.info("keyStoreFile " + keyStoreFile);
		log.info("keyStoreFilePassword " + keyStoreFilePassword);
		log.info("briidgeServerUrl " + briidgeServerUrl);
		skClient = new SKClient(briidgeServerUrl, keyStoreFile, keyStoreFilePassword);
	}

	@Override
	public ReadCardResponse cardReadData(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		return skClient.deviceInitiatedCardReadData(getDataRequest);
	}

	@Override
	public ReadCardResponse deviceInitiatedCardReadData(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		return skClient.deviceInitiatedCardReadData(getDataRequest);
	}


	@Override
	public GetDeviceIdResponse getDeviceData(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		return skClient.deviceInitiatedGetDevice(getDataRequest);
	}

	@Override
	public Response cancelRequest(String txnId, String context) throws Exception {

		CancelRequestRequest cancelRequestRequest = new CancelRequestRequest();
		cancelRequestRequest.setTxnId(txnId);

		if (isNotBlank(context)) {
			cancelRequestRequest.setContext(context);
		}

		return skClient.cancelRequest(cancelRequestRequest);
	}

	@Override
	public TxnResponse initSetQuickCode(String userId, String context) throws Exception {

		GenerateQuickCodeRequest generateQuickCodeRequest = new GenerateQuickCodeRequest();
		if (isNotBlank(userId)) {
			generateQuickCodeRequest.setUserId(userId);
		}

		if (isNotBlank(context)) {
			generateQuickCodeRequest.setContext(context);
		}

		return skClient.initSetQuickCode(generateQuickCodeRequest);
	}

	@Override
	public Response setQuickCodeData(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		Response response = skClient.setQuickCodeData(getDataRequest);
		return response;
	}

	@Override
	public TxnResponse addUser(String userId, List<Map<String, String>> phones) throws Exception {

		AddUserRequest addUserRequest = new AddUserRequest();
		addUserRequest.setUserId(userId);

		if (phones != null && !phones.isEmpty()) {
			List<Phone> phoneList = new ArrayList<Phone>();
			for (Map<String, String> entryPhone : phones) {
				for (Map.Entry<String, String> entry : entryPhone.entrySet()) {
					Phone phone = new Phone(entry.getKey(), entry.getValue());
					phoneList.add(phone);
				}
			}
			addUserRequest.setPhone(phoneList.toArray(new Phone[phones.size()]));

		} else {
			addUserRequest.setPhone(new Phone[0]);
		}

		TxnResponse txnResponse = skClient.addUser(addUserRequest);
		return txnResponse;
	}

	@Override
	public GetUserResponse getUser(String userId) throws Exception {

		BaseMgmtRequest baseMgmtRequest = new BaseMgmtRequest();
		baseMgmtRequest.setUserId(userId);
		return skClient.getUser(baseMgmtRequest);
	}

	@Override
	public TxnResponse removeUser(String userId) throws Exception {

		BaseMgmtRequest baseMgmtRequest = new BaseMgmtRequest();
		baseMgmtRequest.setUserId(userId);
		return skClient.removeUser(baseMgmtRequest);
	}

	@Override
	public TxnResponse updateUser(String userId, Boolean allowCreate, List<Map<String, String>> phones) throws Exception {

		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		updateUserRequest.setUserId(userId);

		if (allowCreate != null) {
			updateUserRequest.setAllowCreate(allowCreate);
		}

		if (phones != null && !phones.isEmpty()) {
			List<Phone> phoneList = new ArrayList<Phone>();
			for (Map<String, String> entryPhone : phones) {
				for (Map.Entry<String, String> entry : entryPhone.entrySet()) {
					Phone phone = new Phone(entry.getKey(), entry.getValue());
					phoneList.add(phone);
				}
			}
			updateUserRequest.setPhone(phoneList.toArray(new Phone[phones.size()]));

		} else {
			updateUserRequest.setPhone(new Phone[0]);
		}

		return skClient.updateUser(updateUserRequest);
	}

	@Override
	public TxnResponse verifyDevice(String userId, String deviceId) throws Exception {

		VerifyDeviceRequest verifyDeviceRequest = new VerifyDeviceRequest();
		verifyDeviceRequest.setDeviceId(deviceId);
		verifyDeviceRequest.setUserId(userId);
		return skClient.verifyDevice(verifyDeviceRequest);
	}

	@Override
	public TxnResponse deverifyDevice(String userId, String deviceId) throws Exception {

		VerifyDeviceRequest verifyDeviceRequest = new VerifyDeviceRequest();
		if (isNotBlank(deviceId)) {
			verifyDeviceRequest.setDeviceId(deviceId);
		}
		verifyDeviceRequest.setUserId(userId);
		return skClient.deverifyDevice(verifyDeviceRequest);
	}

	@Override
	public PairInitiationResponse pairDevice(String userId, String language, String context,
			DeviceFilter deviceFilter, Date expiry, Boolean verifyDevice,
			String notificationType, String notificationUrl) throws Exception {

		PairingRequest pairingRequest = new PairingRequest();
		pairingRequest.setUserId(userId);

		if (isNotBlank(language)) {
			pairingRequest.setLanguage(language);
		}

		if (isNotBlank(context)) {
			pairingRequest.setContext(context);
		}

		if (deviceFilter != null) {
			pairingRequest.setDeviceConstraints(deviceFilter);
		}

		pairingRequest.setExpiryDate(expiry);

		if (verifyDevice != null) {
			pairingRequest.setVerifyDevice(verifyDevice);
		}
		if (isNotBlank(notificationType)) {
			pairingRequest.setNotificationType(NotificationType.valueOf(notificationType));
		}
		if (isNotBlank(notificationUrl)) {
			pairingRequest.setNotificationUrl(notificationUrl);
		}

		PairInitiationResponse pairInitiationResponse = skClient.pairDevice(pairingRequest);
		log.info("Pairing code : " + pairInitiationResponse.getPairCode());
		return pairInitiationResponse;
	}

	@Override
	public Response pairDeviceData(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		return skClient.pairDeviceData(getDataRequest);
	}

	@Override
	public GetDevicesResponse getDevices(String userId) throws Exception {

		BaseMgmtRequest baseMgmtRequest = new BaseMgmtRequest();
		baseMgmtRequest.setUserId(userId);
		return skClient.getDevices(baseMgmtRequest);
	}

	@Override
	public TxnResponse getDeviceById(String deviceId) throws Exception {

		GetDeviceByIdRequest deviceByIdRequest = new GetDeviceByIdRequest();
		deviceByIdRequest.setDeviceId(deviceId);
		return skClient.getDeviceById(deviceByIdRequest);
	}

	@Override
	public TxnResponse addDevice(String userId, String deviceId, Boolean verifiedDevice) throws Exception {

		AddDeviceRequest addDeviceRequest = new AddDeviceRequest();
		addDeviceRequest.setDeviceId(deviceId);
		addDeviceRequest.setUserId(userId);
		if (verifiedDevice != null) {
			addDeviceRequest.setVerifiedDevice(verifiedDevice);
		}
		
		TxnResponse txnResponse = skClient.addDevice(addDeviceRequest);
		log.info("Add device transaction id " + txnResponse.getTxnId());
		return txnResponse;
	}

	@Override
	public TxnResponse removeDevice(String userId, String deviceId) throws Exception {

		RemoveDeviceRequest removeDeviceRequest = new RemoveDeviceRequest();
		removeDeviceRequest.setDeviceId(deviceId);
		removeDeviceRequest.setUserId(userId);
		return skClient.removeDevice(removeDeviceRequest);
	}

	@Override
	public TxnResponse removeAllUserDevices(String userId) throws Exception {

		RemoveAllUserDevicesRequest removeAllUserDevicesRequest = new RemoveAllUserDevicesRequest();
		removeAllUserDevicesRequest.setUserId(userId);
		return skClient.removeAllUserDevices(removeAllUserDevicesRequest);
	}

	@Override
	public GetDeviceIdResponse deviceInitiatedGetDevice(String txnId, String userIpAddress) throws Exception {

		//unable to set userIpAddress with the jaav client lib
		//		if (isNotBlank(userIpAddress)) {request.put("userIpAddress", userIpAddress);}

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		GetDeviceIdResponse getDeviceIdResult = skClient.deviceInitiatedGetDevice(getDataRequest);
		log.info("Device id : " + getDeviceIdResult.getDeviceInfo().getDeviceId());
		return getDeviceIdResult;
	}

	@Override
	public GetProvisioningAuthorizationCodeResponse getProvisioningAuthorizationCode() throws Exception {

		GetProvisioningAuthorizationCodeResponse getProvisioningAuthorizationCodeResponse = skClient.getProvisioningAuthorizationCode();
		log.info("Authorization provision code : " + getProvisioningAuthorizationCodeResponse.getTxnId());
		return getProvisioningAuthorizationCodeResponse;
	}

	@Override
	public VerifyQuickCodeResponse verifyQuickCode(String txnId) throws Exception {

		GetDataRequest getDataRequest = new GetDataRequest();
		getDataRequest.setTxnId(txnId);
		VerifyQuickCodeResponse verifyQuickCodeResult = skClient.verifyQuickCodeData(getDataRequest);
		
		log.info("Is verified quick code : " + verifyQuickCodeResult.isVerifiedQuickCode());
		return verifyQuickCodeResult;
	}
}
