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

package com.securekey.samplerp.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.securekey.connect.beans.Response;
import com.securekey.connect.beans.client.GetDeviceIdResponse;
import com.securekey.connect.beans.client.GetProvisioningAuthorizationCodeResponse;
import com.securekey.connect.beans.client.PairInitiationResponse;
import com.securekey.connect.beans.client.ReadCardResponse;
import com.securekey.connect.beans.client.TxnResponse;
import com.securekey.connect.beans.client.VerifyQuickCodeResponse;
import com.securekey.connect.beans.mgmt.DeviceFilter;
import com.securekey.connect.beans.mgmt.GetDevicesResponse;
import com.securekey.connect.beans.mgmt.GetUserResponse;

/**
 * Declare RP server calls to briidge.Net Connect service
 * 
 * @author      Ivan Bilenjkij (ivan.bilenjkij@securekey.com)
 * 
 */
public interface BriidgeService {


	/**
	 * @param txnId Transaction identifier provided in the initiation response.
	 * 
	 * @return ReadCardResult
	 * 
	 * @see #initCardRead(String, String, String, String, String, Map, String, String)
	 */
	ReadCardResponse cardReadData (String txnId) throws Exception;

	/**
	 * @param txnId Transaction identifier provided in the read card response.
	 * 
	 * @return ReadCardResult
	 * 
	 */
	ReadCardResponse deviceInitiatedCardReadData (String txnId) throws Exception;


	/**
	 * @see #initGetDevice(String, String, String, String, String)
	 * 
	 * @return GetDeviceIdResult
	 * 
	 */
	GetDeviceIdResponse getDeviceData (String txnId) throws Exception;


	/**
	 * This function provides a mechanism for a client to cancel a request
	 * that has been previously initiated. This may be called by the client
	 * if the hosting client page provides a cancellation option and the
	 * client would like to notify SKAP that the request should be
	 * terminated.<br/>
	 * SKAP will stop the processing of the specified request and will not
	 * return any widget events for this request. If the user is currently
	 * interacting with a device application, SKAP may not immediately
	 * terminate the client interaction but will discard the response
	 * provided through the device application when it is submitted.<br/>
	 * Note that this request has no user interaction and therefore does not
	 * require an initiation and data retrieval request.
	 *
	 * @param txnId Transaction identifier for the request to be terminated.
	 * @param context A client provided string that identifies a context for
	 *      this request. This value does not affect the function flow but
	 *      will be recorded in briidge.net Enterprise event log entries.
	 *      This may be useful for some client specific report generation.
	 *      The context string may be up to 30 characters.
	 * 
	 * @return Response
	 */
	Response cancelRequest (String txnId, String context) throws Exception;

	/**
	 * This function provides the mechanism for a client to set up a new
	 * QuickCode for an end user using one of his/her already paired SecureKey
	 * enabled devices. This function currently only supports clients that
	 * also use our Mobile SDK.
	 * <br/>
	 * A client submits a Set QuickCode Request and obtains a transaction ID.
	 * The client will then deliver this transaction ID to its mobile
	 * application running on the targeted user’s device. The mobile
	 * application will complete the QuickCode setup using SecureKey’s Mobile
	 * SDK.  The client can obtain the result of the QuickCode setup from
	 * briidge.net Enterprise server by submitting the data retrieval request
	 * described below.
	 *
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client.
	 *      This is the user for whom the QuickCode is to be set. This user
	 *      must have at least one existing paired device.
	 * @param context A client provided string that identifies a context for
	 *      this request. This value does not affect the function flow but
	 *      will be recorded in briidge.net Enterprise event log entries.
	 *      This may be useful for some client specific report generation.
	 *      The context string may be up to 30 characters
	 *      
	 * @return TxnResponse
	 */
	TxnResponse initSetQuickCode (String userId, String context) throws Exception;

	/**
	 *
	 * @param txnId Transaction identifier provided in the initiation response.
	 * 
	 * @return Response
	 */
	Response setQuickCodeData (String txnId) throws Exception;

	/**
	 *
	 * @param userId The user identifier in the client’s name space. SKAP
	 *     manages userIds independently for each client.
	 * @param phones A map containing phone numbers that the user may be able
	 *     to use during phone or SMS based verification flows (if the client
	 *     supports these types of verification). Map has the following
	 *     properties:
	 *     <dl>
	 *      <dt>key</dt>
	 *      <dd>a descriptive name for this phone number (e.g. “Home”,
	 *      “Mobile”). The value must be unique for the user. The names may
	 *      be specified by the client. SKAP does not associate any meaning
	 *      to the provided names.</dd>
	 *      <dt>number</dt><dd>the phone number</dd>
	 *     </dl>
	 *     
	 * @return TxnResponse
	 */
	TxnResponse addUser (String userId, List<Map<String, String>> phones) throws Exception;

	/**
	 *
	 * @param userId The user identifier to be retrieved.
	 * 
	 * @return GetUserResponse 
	 */
	GetUserResponse getUser (String userId) throws Exception;

	/**
	 *
	 * @param userId The user identifier to be removed from the briidge.net
	 *      Enterprise system for the calling client.
	 *      
	 * @return TxnResponse
	 */
	TxnResponse removeUser (String userId) throws Exception;

	/**
	 *
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client.
	 * @param allowCreate Allow the user account to be created if it does not
	 *      already exist. If not specified, the default value is false.
	 * @param phones A list of objects containing phone numbers that the user
	 *      may be able to use during phone or SMS based verification flows
	 *      (if the client supports these types of verification). The objects
	 *      have the following properties:
	 *      <dl>
	 *          <dt>name</dt>
	 *          <dd>a descriptive name for this phone number (e.g. “Home”,
	 *          “Mobile”). The value must be unique for the user. The names
	 *          may be specified by the client. briidge.net Enterprise does
	 *          not associate any meaning to the provided names.</dd>
	 *          <dt>number</dt>
	 *          <dd>the phone number</dd>
	 *      </dl>
	 *      This array replaces the contents of any existing list of phone numbers.
	 *      
	 * @return TxnResponse
	 */
	TxnResponse updateUser (String userId, Boolean allowCreate, List<Map<String, String>> phones)
			throws Exception;


	/**
	 * In order to verify devices for users, the client may wish to perform
	 * its own verification process. For example, the client may request the
	 * user to answer a number of challenge questions on the client’s web
	 * site following an SKAP interaction with an unverified device.
	 * Following the verification of the user, the client would then notify
	 * SKAP that the device associated with the specified request should be
	 * treated as verified.
	 * 
	 * @return TxnResponse
	 */
	TxnResponse verifyDevice (String userId, String deviceId) throws Exception;

	/**
	 * The client may wish to mark a user’s devices as being unverified.
	 * Following this call, any of the de-verified devices will require
	 * re-verification.
	 *
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client.
	 * @param deviceId The device identifier for the SecureKey enabled
	 *      device. This identifier is unique to the device and the
	 *      requesting client. That is, a different client will receive
	 *      a different identifier for the same device. If the deviceId is
	 *      not provided, all devices for the specified user will be
	 *      de-verified.
	 * 
	 * @return TxnResponse
	 */
	TxnResponse deverifyDevice (String userId, String deviceId) throws Exception;

	/**
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client. briidge.net Enterprise will use the provided identifier
	 *      to determine (or add) authentication devices.
	 * @param language Language code in format specified in RFC 5646
	 *      including 2 character language code and optionally a 2 character
	 *      country/region code. E.g. en-CA.
	 * @param context A client provided string that identifies a context for
	 *      this request. This value does not affect the function flow but
	 *      will be recorded in briidge.net Enterprise event log entries.
	 *      This may be useful for some client specific report generation.
	 *      The context string may be up to 30 characters.
	 * @param deviceConstraints Identifies any constraints on what type of
	 *      SecureKey enabled device may be paired. This can include device
	 *      categories and capabilities. See below for the definition of the
	 *      deviceConstraints object structure.
	 * @param expiry The expiry date/time for the pairing request. If the
	 *      pairing has not been completed by the expiry time, the pairing
	 *      request terminates in failure. The date should be formatted as an
	 *      ISO 8601 string using UTC. e.g. 2013-06-04T00:00:00.000Z
	 *      (milliseconds are optional).
	 * @param verifyDevice Indicates whether the device should be marked as
	 *      verified for the specified user. The default value is false.
	 * @param notificationType Indicates how the relying party is to be
	 *      notified upon completion of the pairing request. Upon
	 *      notification, the relying party would request the pairing
	 *      response data from briidge.net Enterprise. The values for
	 *      notificationType are:
	 *      <dl>
	 *          <dt>none</dt>
	 *          <dd>no notification is provided to the relying party upon
	 *          completion of the pairing.</dd>
	 *          <dt>httpPost</dt>
	 *          <dd>an HTTP message is POST’ed to a relying party specified
	 *          endpoint.</dd>
	 *          <dt>notificationChannel</dt>
	 *          <dd>a long polling notification channel will be notified upon
	 *          completion of the pairing. A notification handle will be
	 *          created and returned in the response to this call. Note that
	 *          the notification channel response is intended for online
	 *          pairings as the notification channel will only remain active
	 *          for a short time period (e.g. 15 minutes).</dd>
	 *      </dl>
	 *      The notification message will include the transaction id and a
	 *      status code (success or failure). Full response details can be
	 *      retrieved from briidge.net Enterprise using the transaction id.
	 * @param notificationUrl The HTTP notification endpoint to POST the
	 *      completion notification to. This is only valid if notificationType
	 *      is set to httpPost.
	 * 
	 * @return PairInitiationResponse
	 */
	PairInitiationResponse pairDevice(String userId, String language, String context,
			DeviceFilter deviceFilter, Date expiry, Boolean verifyDevice,
			String notificationType, String notificationUrl)
					throws Exception;

	/**
	 *
	 * @param txnId Transaction identifier provided in the initiation
	 *      response.
	 * 
	 * @return Response
	 */
	Response pairDeviceData (String txnId) throws Exception;


	/**
	 *
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client.
	 *      
	 * @return GetDevicesResponse
	 */
	GetDevicesResponse getDevices (String userId) throws Exception;

	/**
	 *
	 * @param deviceId The device identifier for the SecureKey enabled
	 *      device. This identifier is unique to the device and the
	 *      requesting client. That is, a different client will receive
	 *      a different identifier for the same device. If the deviceId is
	 *      not provided, all devices for the specified user will be
	 *      de-verified.
	 * 
	 * @return TxnResponse
	 */
	TxnResponse getDeviceById (String deviceId) throws Exception;

	
	/**
	 * Adds a device to a user
	 *
	 * @param userId The user identifier in the client’s name space.
	 *      briidge.net Enterprise manages userIds independently for each
	 *      client.
	 * @param deviceId The device identifier for the SecureKey enabled
	 *      device. This identifier is unique to the device and the
	 *      requesting client. That is, a different client will receive
	 *      a different identifier for the same device. If the deviceId is
	 *      not provided, all devices for the specified user will be
	 *      de-verified.
	 * @param verifiedDevice Regarding the process the device could be already verified
	 *      
	 * @return TxnResponse
	 */
	TxnResponse addDevice (String userId, String deviceId, Boolean verifiedDevice) throws Exception;

	
	/**
	 * Removes a device from a user.
	 *
	 * @param userId The user id.
	 * @param deviceId The device id.
	 * 
	 * @return TxnResponse
	 */
	TxnResponse removeDevice (String userId, String deviceId) throws Exception;

	/**
	 * Removes all devices from a user
	 * @param userId The user id
	 * @return JSON string with following content:
	 *      <table>
	 *          <tr>
	 *              <th>property</th>
	 *              <th>type</th>
	 *              <th>required</th>
	 *              <th>description</th>
	 *          </tr>
	 *          <tr>
	 *              <td>error</td>
	 *              <td>string</td>
	 *              <td>no</td>
	 *              <td><dl>
	 *                  <dt>invalid_credentials</dt>
	 *                  <dd>client credentials are not recognized.</dd>
	 *                  <dt>unknown_user</dt>
	 *                  <dd>the user id is unknown</dd>
	 *              </dl></td>
	 *          </tr>
	 *          <tr>
	 *              <td>errorDescription</td>
	 *              <td>string</td>
	 *              <td>no</td>
	 *              <td>An optional textual description that may provide
	 *              additional information on the cause of the error.
	 *              This text is not intended for display to end users but
	 *              rather to aid in problem determination. This property is
	 *              optional and will only be provided in error conditions.
	 *              </td>
	 *          </tr>
	 *      </table>
	 *      
	 * @return TxnResponse	 
	 */
	TxnResponse removeAllUserDevices (String userId) throws Exception;

	/**
	 * briidge.net Enterprise also facilitates device initiated requests.
	 * Under this model, the device first initiates contact with the
	 * briidge.net Enterprise server (via briidge.net Enterprise mobile SDK).
	 * The briidge.net Enterprise server executes and stores the result of
	 * the request and issues a transaction ID returning it to the device.
	 * The device can then notify the RP’s server with the transaction ID
	 * obtained. The service providing application can then retrieve the
	 * result of the request using the transaction ID. It is noteworthy that,
	 * as with Web API integration, briidge.net Enterprise abstracts the
	 * communication to the SecureKey enabled devices from the client so it
	 * does not need to be concerned with the various technologies required
	 * to communicate with different devices. It should also be noted that
	 * the nature of these requests are standalone. They do not require nor
	 * belong to a briidge.net Enterprise session.
	 * <br/>
	 * The device initiated flow can be described by the following steps.
	 * <ol>
	 *     <li>RP mobile application initiates request (e.g. Get Device ID)
	 *     from mobile SDK.</li>
	 *     <li>Mobile SDK initiates request with briidge.net Enterprise
	 *     server.</li>
	 *     <li>briidge.net Enterprise server communicates with mobile SDK to
	 *     verify the device credential.</li>
	 *     <li>briidge.net Enterprise server generates a transaction
	 *     identifier for response retrieval and passes it to mobile SDK.</li>
	 *     <li>Mobile SDK passes transaction identifier to the RP mobile
	 *     application.</li>
	 *     <li>RP mobile application passes the transaction identifier to the
	 *     RP application server.</li>
	 *     <li>RP Application server makes call to briidge.net Enterprise
	 *     server to retrieve the response data.</li>
	 *     <li>Based on the result, RP application server takes appropriate
	 *     action with the mobile application.</li>
	 * </ol>
	 * The following diagram shows the interactions between the client’s
	 * mobile device and the briidge.net Enterprise server. Please refer to
	 * the Mobile SDK Integration Guide for more details.
	 *
	 * @param txnId The transaction id provided to the device.
	 * @param userIpAddress The IP address of the device that the user
	 *      connects to the client. If provided, SKAP will verify that this
	 *      IP address matches the IP address that the device connected to
	 *      the SKAP server. If there is a mismatch, a warning will be
	 *      returned in the response.
	 *
	 * @return JSON string with following content:
	 *      <table>
	 *          <tr>
	 *              <th>property</th>
	 *              <th>type</th>
	 *              <th>required</th>
	 *              <th>description</th>
	 *          </tr>
	 *          <tr>
	 *              <td>deviceInfo</td>
	 *              <td>DeviceInfo object</td>
	 *              <td>yes</td>
	 *              <td>The deviceInfo element provides information regarding
	 *              the SecureKey enabled device used.<br/>
	 *              See section 2.6.1 for details on the DeviceInfo
	 *              properties.<br/>
	 *              This property is only applicable to successful
	 *              authentication processes that utilize a SecureKey enabled
	 *              device.<br/>
	 *              briidge.net Enterprise will verify the device by
	 *              generating and validating a cryptogram for the device
	 *              during the processing of this function.</td>
	 *          </tr>
	 *          <tr>
	 *              <td>error</td>
	 *              <td>String</td>
	 *              <td>no</td>
	 *              <td><dl>
	 *                  <dt>unknown_txn</dt>
	 *                  <dd>the transaction identifier supplied
	 *                  by the client is not recognized by briidge.net
	 *                  Enterprise</dd>
	 *                  <dt>system_error</dt><dd>an unexpected error occurred</dd>
	 *              </dl></td>
	 *          </tr>
	 *          <tr>
	 *              <td>errorDescription</td>
	 *              <td>String</td>
	 *              <td>no</td>
	 *              <td>An optional textual description that may provide
	 *              additional information on the cause of the error. This
	 *              text is not intended for display to end users but rather
	 *              to aid in problem determination.<br/>
	 *              This property is optional and will only be provided in
	 *              error conditions.</td>
	 *          </tr>
	 *          <tr>
	 *              <td>warnings</td>
	 *              <td>array</td>
	 *              <td>no</td>
	 *              <td>Non-fatal errors or warnings concerning the completed
	 *              authentication. More items may be added in the future.
	 *              <dl>
	 *                  <dt>client_ip_mismatch</dt><dd>the IP address of the user
	 *                  that was provided by the client in the response
	 *                  retrieval request does not match the ip address of
	 *                  the client device that performed the authentication.</dd>
	 *              </dl></td>
	 *          </tr>
	 *      </table>
	 * 
	 * @return GetDeviceIdResult
	 */
	GetDeviceIdResponse deviceInitiatedGetDevice (String txnId, String userIpAddress) throws Exception;

	/**
	 * This function provides a mechanism for a client to verify an end users
	 * QuickCode. The requests is initiated by the clients mobile app by
	 * calling the Briidge SDK. The SDK will return a transaction id which
	 * should be passed to the client’s server which can be used to retrieve
	 * the result of the verification.
	 *
	 * @param txnId Transaction identifier provided by the SDK.
	 * 
	 * @return VerifyQuickCodeResult
	 */
	VerifyQuickCodeResponse verifyQuickCode (String txnId) throws Exception;

	/**
	 * Returns a code to use for provisioning a Mobile SDK for the first time or after a reset.
	 * 
	 * @return GetProvisioningAuthorizationCodeResponse
	 */
	GetProvisioningAuthorizationCodeResponse getProvisioningAuthorizationCode () throws Exception;
}
