Challenger platform API class and examples for Java
===

## Event tracking example

This code prepares a call to Challenger server on event happened to a client identified by {client_id}:

~~~~
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');

myChallenger.setOwnerId('{owner_id}'); // Optional
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');
myChallenger.addParam('multiple', '{multiple}'); // Optional

bool resp = myChallenger.trackEvent('{event}');
~~~~

## Delete client example

This code prepares a call to Challenger to delete particular client {client_id}:

~~~~
import ChallengerPlatform.*;

// ... your code ...

Challenger myChallenger = new Challenger('{your.challenger.domain}');
myChallenger.setClientId('{client_id}');
myChallenger.setKey('{secret_key}');

bool resp = myChallenger.deleteClient();
~~~~



# Performance widgets
## Web version

Using the Java helper functions provided with Challenger to get widget HTML is as easy as that:

~~~~
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
~~~~

## Mobile app version

This code creates an encrypted URL for mobile ready widget. It should be passed to mobile app and opened in WebView.

~~~~
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
~~~~
