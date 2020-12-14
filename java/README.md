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

#### Build

Using Maven build with:

```
mvn package
```

After the build finished:
* the `elv-tokens-xx.jar` jar is in the `target` folder
* libraries of dependencies are in the `target/libs` folder

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

### Native bindings

**signing**

Tokens are signed via a native `secp256k1` implementation.
 
This behavior is controlled via the `native.secp256k1.disabled` system property. <br>
When its value is 'true' the native implementation is disabled and thus pure java is used. 

To disable the native bindings, on the command line, use `java -Dnative.secp256k1.disabled=true -cp ...`.

**base58**

A native implementation of base58 encoding is provided via pre-built dynamic/shared libraries in the `resources/io/eluv/format/base58/native` folder.

To disable using the native library set the `native.b58.disabled` system property to 'true'. <br>
Otherwise the native library can be used in one of two ways:

Copy the appropriate library either: 
* to one of the folders reported by the system property `java.library.path` or
* to a specific location and pass the absolute path of the library via a system property `native.b58.library`

Or (**only if the library was built with maven**):
* the native library will be extracted to a temporary folder at runtime and used from there.


**Note:** running the class `io.eluv.format.eat.Natives` without arguments prints the availability status of the native libraries to the standard output.


### Benchmarking

The test below show results for Linux without and with native libraries. <br>
Similar results were achieved on MAC.

* without native libraries

```
# java -cp target/elv-tokens-0.0.1-SNAPSHOT.jar:target/libs/* -Dnative.b58.disabled=true -Dnative.secp256k1.disabled=true io.eluv.format.eat.TokenBench
Generated 10000 tokens.
Duration: 10170 millis
token: aessj_3WWhmBg9b4cCUj5ngABmstKKk24xsBkjQq3NVFhpmKnjQbBBp3LAeWL1AXoKCVUHC9NfvWVk77uuakWEJDeN2PTdEWdCZK7BasSAQsoVhs4hUrYNag9DxEuReDWU6ZMFunQChjGdtXkBFryqJ643K1hTxvxmDM15i74V85Q2QeAusfowHg3cwrY9V7P33Uvd7B3mzRNSD2DiJ9RKhXM41vpm7Fzts5bLTp8grZZFnamhoSq2mhaDtz6mZApA1epp6SiY3o2E9LoS8B69wqeNvqEVi11DzJmM22hih6hZCQx9q44gvusthesFA1dMjJi6eVUe4dcXBFc5vLjy63SwStaFhSrV569UviP23PKNyCTFnkBGySFw7UTuBK2uEmSSjadQfEetKGAeQTkLnWQrrUgcFCdBYVaucck3evnLJXrZ3JkUcnX2uLLD73GjN3f4KWjNknBKPWufCuX79BWw3ZtFamTAjX13NB4uL2dSNWLirAFCj3MHQALpfWExSxkJ2Ms4BiXnxQGxXSBvZLJti6dvCPYQ

os-info: Linux/x86_64
java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
== Native b58 library ==
native.b58.disabled=true
native.b58.library=elvb58
default library name: libelvb58.so
io.eluv.format.base58.tmpdir=/tmp
native library disabled.
== secp256k1 ==
native.secp256k1.disabled=true
fr.acinq.secp256k1.tmpdir=null
fr.acinq.secp256k1.lib.path=null
fr.acinq.secp256k1.lib.name=null
default library name: libsecp256k1.so
```

* with native libraries

```
java -cp target/elv-tokens-0.0.1-SNAPSHOT.jar:target/libs/*  io.eluv.format.eat.TokenBench
Generated 10000 tokens.
Duration: 4650 millis
token: aessj_2EtmxkmjqsT4dCoF9KArJvAVp73Hxz6FgsbTzDMtcBUZB5SNSvjxHjnrmtQqwiCXnNY4zycGtQdR54z85j5Ymjv1UAwoPCsxqfrUZtAqjBArtVZGkBo6c5HjZZpHzGtAdfM7NGbZ34s2rNxW4yWcfssHJWAfpUYVNCkuak4uyMYJxrkikbneeBsvRpi9gbBYmt9StrDKjJDuvz7venEMPPz41hPoXJynACp4iTAd7bkeXFcQDYu99krKpSPf46B5UuiuF9RjCuRfW5gAuQRpyzLJ6vHzfHUNnnuqrtz41xMNFbRxEnsiDwyGsPwwogoUB4LWP9johkQFkGU7KYS95KbkoKDcVo7L7QBkdjKqPo3CgUogCe7vm8mfNY3ZfPdphNP6xcAb25AUNjS8p8z7rXRn2sx3j4tq3UTh64LyWapbWXHr1kD93Wpw8HZUgr6QNn7AuaZrphXsTKgUDsiLRerHH6DkKae8VEYsHBNH8sMj8cynm464uKJoPBUKaFvmkKaB1hhp5ybGUEMzQvwSETGt3XLQ

os-info: Linux/x86_64
java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
== Native b58 library ==
native.b58.disabled=false
native.b58.library=elvb58
default library name: libelvb58.so
io.eluv.format.base58.tmpdir=/tmp
native library found.
== secp256k1 ==
native.secp256k1.disabled=false
secp256k1 available: true
fr.acinq.secp256k1.tmpdir=null
fr.acinq.secp256k1.lib.path=null
fr.acinq.secp256k1.lib.name=null
default library name: libsecp256k1.so
```

 
