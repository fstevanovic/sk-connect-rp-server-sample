RP Server Sample

Sample RP server is intended to demonstrate barebone functionality that Relaying Party (RP) app server would need in order to utilize SecureKey briidge.Net Connect platform.

For more information about briidge.net Connect visit [http://developer.securekey.com](http://developer.securekey.com)
 
**Methods exposed to mobile device**

Sample RP server exposes couple of HTTP GET/POST methods to mobile device, as defined in com.securekey.samplerp.web.BriidgeController class.
 
_getProvisioningAutorizationCode.json_
  
Receives authorizationCode from the Connect service so RP can complete initial device provisioning. 

_verifyLogin.json_
  
Simulates RP server login from the mobile device. It's always returns "success".

_getDeviceId.json_  

Receives txnId returned by the mobile SDK from mobile as a result of authenticateDevice method. 
Communicates with briidge.Net service to verify deviceId and returns GetDeviceIdResult which contains deviceInfo if successfull, error otherwise.

_initMobileQuickcode.json_

Receives txnId returned by the mobile SDK from mobile after calling authenticateDevice method. It also receives userId as set by the RP. 
It first communicates with briidge.Net service to obtain deviceId, than makes calls to add user addUser(user, phones) and user's device addDevice(userId, deviceId, verifiedDevice).
Finally, initiates QuickCode setup by calling briidge.Net service initSetQuickCode(userId, context) method and 
returns a new txnId to mobile app to enable completion of user QuickCode setup.

_verifyQuickcode.json_

Receives txnId returned by the mobile SDK from mobile after calling method verifyQuickCode. 
It uses txnId to make a call to briidge.Net service verifyQuickCode(txnId) method, returns VerifyQuickCodeResult JSON object which contains deviceInfo on success, error otherwise.


**Build and run** 

Use Maven or your prefered IDE to build with included pom.xml file.
 
Deploy resulting WAR file to your app server. For example, if you are using your default local instance, URL of the server would be:

[http://localhost:8080/samplerp/](http://localhost:8080/samplerp/)

Replace localhost with the actual machine name or IP address and use that as URL in your mobile app.
 
Make sure that your mobile app is running on the device where it can access your local RP server.


**Configure your own RP**

If you have custom certificate (only JKS supported in this sample code), replace _RP_MobileSamples.jks_ with your own keystore in _src/main/resources/keystore_.
Update _system.properties_ to configure your own _keystoreFile_ and _keystoreFilePassword_ 

