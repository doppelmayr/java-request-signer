java-url-signer
===============

Simple java library to assist with signing API requests - as part of oAuth or on its own for simple API authentication layer, for example as described by http://broadcast.oreilly.com/2009/12/principles-for-standardized-rest-authentication.html:

"All REST queries must be authenticated by signing the query parameters sorted in lower-case, alphabetical order using the private credential as the signing token. Signing should occur before URL encoding the query string."

OAuth signature is calculated according to https://dev.twitter.com/docs/auth/creating-signature.

Avoids use of any specific representation of HttpRequest, instead assumes all parameters used in signing are gathered in one String multimap (Map<String, List<String>), to permits multiple parameter values.

Code to get signature for a request based on parameters param1 with values value1 and value2 and timestamp:
OAuth:
```java

        OAuthApiRequestSigner.Builder builder = new OAuthApiRequestSigner.Builder()
            .withEncryptionAlgorithm(encryption).withEncryptionKey(secretEncryptionKey)
            .withMethod("POST")
            .withUrl(url);
        builder.withParameterValue(param1, valueA);       
        builder.withParameterValue(param1, valueB);       
        builder.withParameterValue(param2, valueX);       
        String signature = builder.sign();
```
Basic signature:
```java

	ClassicApiRequestSigner.Builder builder = new ClassicApiRequestSigner.Builder()
	    .withEncryptionAlgorithm(encryption).withEncryptionKey(secretEncryptionKey);
        builder.withParameterValue(param1, valueA);       
        builder.withParameterValue(param1, valueB);       
        builder.withParameterValue(param2, valueX);       
        String signature = builder.sign();
```
If you alreaty have multimap (Map<String, List<String>>) representation of request fields handy, 
```java

	String signature = new OAuthApiRequestSigner().formatAndSign(method, url, paramMap, secretEncryptionKey);
	or
	String signature = new ClassicApiRequestSigner().formatAndSign(paramMap, secretEncryptionKey);
```

Use signature to sign your request using your preferred representation and your choice of signature parameter name.





