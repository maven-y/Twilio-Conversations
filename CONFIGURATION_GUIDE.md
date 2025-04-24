# Twilio Conversations Demo App Configuration Guide

## Configuration Methods

There are two ways to configure the token service URL for the Twilio Conversations demo app:

### Method 1: Using gradle.properties

1. Create or edit `gradle.properties` file in your project root directory
2. Add the following line:
   ```
   ACCESS_TOKEN_SERVICE_URL=https://<your-account>.twil.io/token-service
   ```
   Replace `<your-account>` with your Twilio account identifier

### Method 2: Using Gradle Command Line

Run the following command when building the app:
```
./gradlew app:assembleDebug -PACCESS_TOKEN_SERVICE_URL=https://<your-account>.twil.io/token-service
```

## Setting Up Token Service

### Option 1: Using Twilio Functions (Recommended for Testing)

1. **Create Twilio Function**
   - Go to [Twilio Functions](https://www.twilio.com/console/functions/manage)
   - Click "Create Function"
   - Select "Blank" template
   - Add `/token-service` to the PATH
   - Uncheck "Check for valid Twilio signature"

2. **Add Function Code**
   ```javascript
   let users = {
       user00: "password00", // Change these passwords
       user01: "password01"  // Change these passwords
   };

   exports.handler = function(context, event, callback) {
       if (!event.identity || !event.password) {
           let response = new Twilio.Response();
           response.setStatusCode(401);
           response.setBody("No credentials");
           callback(null, response);
           return;
       }

       if (users[event.identity] != event.password) {
           let response = new Twilio.Response();
           response.setStatusCode(401);
           response.setBody("Wrong credentials");
           callback(null, response);
           return;
       }
       
       let AccessToken = Twilio.jwt.AccessToken;
       let token = new AccessToken(
         context.ACCOUNT_SID,
         context.TWILIO_API_KEY,
         context.TWILIO_API_SECRET, {
           identity: event.identity,
           ttl: 3600
         });

       // Add Conversations Grant
       let conversationGrant = new AccessToken.ChatGrant({ 
           serviceSid: context.CONVERSATIONS_SERVICE_SID 
       });
       token.addGrant(conversationGrant);

       // Add Voice Grant
       let voiceGrant = new AccessToken.VoiceGrant({
           outgoingApplicationSid: context.TWIML_APP_SID,
           incomingAllow: true
       });
       token.addGrant(voiceGrant);

       // Add Video Grant
       let videoGrant = new AccessToken.VideoGrant();
       token.addGrant(videoGrant);

       callback(null, token.toJwt());
   };
   ```

3. **Configure Environment Variables**
   Go to [Twilio Functions Configure](https://www.twilio.com/console/functions/configure) and add:
   ```
   ACCOUNT_SID=AC...your-account-sid...
   TWILIO_API_KEY=SK...your-api-key...
   TWILIO_API_SECRET=your-api-secret
   CONVERSATIONS_SERVICE_SID=IS...your-service-sid...
   TWIML_APP_SID=AP...your-twiml-app-sid...
   PUSH_CREDENTIAL_SID=CR...your-push-credential-sid...
   ```

4. **Get Required Values**
   - **ACCOUNT_SID**: Find in [Twilio Console](https://www.twilio.com/console)
   - **TWILIO_API_KEY** and **TWILIO_API_SECRET**: 
     - Create at [API Keys](https://www.twilio.com/console/chat/project/api-keys)
     - API Key starts with "SK..."
   - **CONVERSATIONS_SERVICE_SID**: 
     - Go to [Conversations Console](https://www.twilio.com/console/conversations/configuration/defaults)
     - Click "View Service" next to "Default Conversation Service"
     - Copy "Service SID" (starts with "IS...")
   - **TWIML_APP_SID**:
     - Create at [TwiML Apps](https://www.twilio.com/console/voice/twiml/apps)
     - Configure with your Voice URL
   - **PUSH_CREDENTIAL_SID**: 
     - Create at [Push Credentials](https://www.twilio.com/console/conversations/push-credentials)
     - Use Firebase Cloud Messaging Server key

### Option 2: Custom Token Service

Create your own token service that:
1. Accepts GET requests with parameters:
   ```
   ?identity=<USER_PROVIDED_USERNAME>&password=<USER_PROVIDED_PASSWORD>
   ```
2. Returns:
   - Valid Twilio access token for valid credentials
   - HTTP 401 for invalid credentials

## Testing Configuration

1. **Test Token Service**
   Access your token service URL with test credentials:
   ```
   https://<your-account>.twil.io/token-service?identity=user00&password=password00
   ```
   Should return a JWT token if configured correctly.

2. **Test App Login**
   - Username: `user00`
   - Password: `password00`

## Troubleshooting

If you encounter "UNAUTHORIZED" errors:
1. Verify all environment variables are set correctly
2. Check API Key permissions
3. Ensure Service SID matches your Conversations service
4. Verify token service URL is correct
5. Test token service directly in browser

## Security Notes

1. Change default passwords in the function code
2. Keep API Secret secure
3. Use HTTPS for token service
4. Implement proper user authentication
5. Set appropriate token TTL

## Additional Resources

- [Twilio Functions Documentation](https://www.twilio.com/docs/functions)
- [Twilio Conversations Documentation](https://www.twilio.com/docs/conversations)
- [Firebase Cloud Messaging Setup](https://firebase.google.com/docs/cloud-messaging) 