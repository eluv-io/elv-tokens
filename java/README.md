# elv-tokens-java

elv-tokens-java is a java library for creating `editor-signed` tokens.
These tokens can be used for authorization in requests to the eluv.io content-fabric.

Editor-signed tokens are described in the [documentation](https://github.com/eluv-io/elv-docs).

## Dependencies


```
  <dependencies>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.11.3</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.11.3</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.11.3</version>
    </dependency>
    <dependency>
      <groupId>org.web3j</groupId>
      <artifactId>crypto</artifactId>
      <version>5.0.0</version>
    </dependency>
    <dependency>
      <groupId>org.web3j</groupId>
      <artifactId>core</artifactId>
      <version>5.0.0</version>
    </dependency>
    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>30.0-jre</version>
    </dependency>
    <dependency>
      <groupId>fr.acinq.bitcoin</groupId>
      <artifactId>secp256k1-jni</artifactId>
      <version>1.3</version>
    </dependency>
  </dependencies>
```

## Usage and Example

A quick code example is shown below:

```
    String privateKeyHex = ..       // hex encoded private key
    String spaceId = ..             // space Id
    String libraryId = ..           // library Id
    String qId = ..                 // content Id
    String delegateId = ..          // delegate Id
    HashMap<String,Object> ctx = .. // context values
        
    TokenFactory.EditorSigned es = new TokenFactory.EditorSigned(
        spaceId, 
        libraryId, 
        qId)
        .withExpiresIn(TokenFactory.HOUR * 4)
        .withDelegationId(delegateId)
        .withContext(ctx);
    System.out.println(es.signEncode(privateKeyHex));

```


#### Sample Code

Class `io.eluv.format.eat.TokenSign` has a simple main for demonstration:

```
java -cp target/elv-tokens-0.0.1-SNAPSHOT.jar:target/libs/* \
 io.eluv.format.eat.TokenSign \
 c205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6 \
 ispc218Pn4tTNJELz8ASyV8o4KRggfoD \
 ilib3FfPwGraXTRgoq2Xu4oC7eJgT5Tj \
 iq__35BUYfYD44N2vZniVHrqaadrh8mC

aessjcBduDG6gjsfTMe7fTZUYEonpSEWkz2dKz1cenGpK8EVyWJzgaZhxtG7bP31aD5EcZXSdJA4SMhSyD5nMSTgroTd16Emv6WHrm1sHwrT3cLRBznhfySoPSqZoY8SNstw53hSaXKyCejy1d2yd839ByUzcNkF9GrwPt2yXSgVKTX6qbrTvLs6sTUgXvmQHyF8CyER5dTfYqToyXDNJzhZ1osYebs7XXCYQJ29kgvc8fSCKy3UDpRj4VpXve3vutRvhPB1eQFDSiuuZ16qJFe2ztqFZyNCxnq71uerxpGLKZafYiE1CUJMaXAPa5hgTgy4uddo6EbASqmuujLBVg5Ciq7S4n2tdDPLD2XjBkbad6T5rKz9nM7Sf1QdhE5A1a5pNWpRpRYyC6bFbXpeQ7d4GqKg
```

The provided token can then be used as `authorization` in URL query (or header):


```
curl http://127.0.0.1:8008/qlibs/ilib3FfPwGraXTRgoq2Xu4oC7eJgT5Tj/q/tqw_DFKqKsfjT1mc51vUNfS7DnGU3xKw29y3h/meta/public/?authorization=aessjcBduDG6gjsfTMe7fTZUYEonpSEWkz2dKz1cenGpK8EVyWJzgaZhxtG7bP31aD5EcZXSdJA4SMhSyD5nMSTgroTd16Emv6WHrm1sHwrT3cLRBznhfySoPSqZoY8SNstw53hSaXKyCejy1d2yd839ByUzcNkF9GrwPt2yXSgVKTX6qbrTvLs6sTUgXvmQHyF8CyER5dTfYqToyXDNJzhZ1osYebs7XXCYQJ29kgvc8fSCKy3UDpRj4VpXve3vutRvhPB1eQFDSiuuZ16qJFe2ztqFZyNCxnq71uerxpGLKZafYiE1CUJMaXAPa5hgTgy4uddo6EbASqmuujLBVg5Ciq7S4n2tdDPLD2XjBkbad6T5rKz9nM7Sf1QdhE5A1a5pNWpRpRYyC6bFbXpeQ7d4GqKg
{"a":"b"}
```

**Note** that tokens are signed via a native `secp256k1` implementation.
 
This behavior is controlled via the `native.secp256k1.disabled` system property. <br>
When its value is 'true' the native implementation is disabled and thus pure java is used. 

On the command line, use `java -Dnative.secp256k1.disabled=true -cp ...`.