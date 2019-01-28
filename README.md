Challenger platform API class and examples for Java
===

NB: Due to 256 bit keys usage it may be needed to install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files. For Java version 7 (https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) or version 8 (https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

In example below:

 - `your.challenger.domain` - is the domain of your Challenger implementation
 - `secret_key` - a unique key provided by Challenger to encrypt data exchange
 - `owner_id` - a unique identifier provided by Challenger (optional)
 - `client_id` - the identifier of the client performing action
 - `event_id` - the identifier of the corresponding event in Challenger platform.
 - `multiple` - for quantifiable challenges (ex. get 1 point for every 1 euro spent). Provide value to multiple points with.

## Event tracking example

This code prepares a call to Challenger server on event happened to a client identified by {client_id}:

```java
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');

myChallenger.setOwnerId('{owner_id}'); // Optional
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');
myChallenger.addParam('multiple', '{multiple}'); // Optional

bool resp = myChallenger.trackEvent('{event}');
```

## Delete client example

This code prepares a call to Challenger to delete particular client {client_id}:

```java
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');

bool resp = myChallenger.deleteClient();
```

N.B. This function is accessible for in-house deployments only.

# Performance widgets

In examples below:
 - `your.challenger.domain` - is the domain of your Challenger implementation.
 - `client_id` - the identifier of the client performing action
 - `secret_key` - a unique key provided by Challenger to encrypt data exchange
 - `param1`, `param2`, ... - optional parameters to pass to the widget (For example name of the client). List of parameters Challenger can map:
   - `expiration` (in format 0000-00-00 00:00:00) - required param
   - `name`
   - `surname`
   - `email`
   - `phone`
   - `birthday` (in format 0000-00-00)
 - `value1`, `value2`,  ... - values of optional parameters.

## Web version

Using the Java helper functions provided with Challenger to get widget HTML is as easy as that:

```java
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');
myChallenger.addParam('expiration', '0000-00-00 00:00:00'); // Required
myChallenger.addParam('name', 'John'); // Optional
myChallenger.addParam('surname', 'Smith'); // Optional
myChallenger.addParam('{param1}', '{value1}'); // Optional
myChallenger.addParam('{param2}', '{value2}'); // Optional

try{
   String resp = myChallenger.getWidgetHtml(); // Return HTML snippet
}catch(Exception ex){
   // Error happened.
}
```

N.B. This function is accessible for in-house deployments only.

## Mobile app version

This code creates an encrypted URL for mobile ready widget. It should be passed to mobile app and opened in WebView.

```java
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');
myChallenger.addParam('expiration', '0000-00-00 00:00:00'); // Required
myChallenger.addParam('{param1}', '{value1}');
myChallenger.addParam('{param2}', '{value2}');
myChallenger.addParam('mobile', true); // Pass it to get mobile version of the widget

try{
   String resp = myChallenger.getWidgetUrl(); // Return HTML snippet
}catch(Exception ex){
   // Error happened.
}
```

N.B. This function is accessible for in-house deployments only.
