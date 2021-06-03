# jackson-PIImasker
Sensible information (such us emails, IDs or phone numbers) is a serious problem 
to deal with. If you have objects that are logged or write into files where 
masking is required, and you are already using Jackson ObjectMapper solution in 
your application, this library might be of some help.

## How to use
Lets suppose you have an entity that has an email as part of its attributes:
```java
class EntityA {
    String email;
    ...
}
```
Well, with this class you can annotate this attribute as sensible data like this:
```java
import com.github.javiercanillas.pii.PIIString;

class EntityA {
    @PIIString
    String id;
    
    public String getId() { return this.id; }
    public void setId(String value) { this.id = value; }
}
```
And it will be masked when writing using a Jackson ObjectMapper like this:
```java
ObjectMapper mapper = new ObjectMapper();
final ObjectWriter pIIWriter = mapper.writerWithView(PIIMasked.class);
final ObjectWriter normalWriter = mapper.writer();
final EntityA obj = new EntityA();
obj.setId("abcd1234");

pIIWriter.writeValueAsString(obj);
```
And it will produce:
```json
{
  "email": "********"
}
```
Furthermore, you can customize the masking character and if you want to 
leave some last characters. 
```java
    @PIIString(keepLastCharacters = 6, maskCharacter = '-')
    String id;
```

The produced json in this case would be:
```json
{
  "email": "--cd1234"
}
```

## How to install

### Gradle

Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```groovy
	dependencies {
	        implementation 'com.github.javiercanillas:jackson-piimasker:1.0.0'
	}
```

### Maven
Step 1. Add the JitPack repository to your build file

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Step 2. Add the dependency

```xml
	<dependency>
	    <groupId>com.github.javiercanillas</groupId>
	    <artifactId>jackson-piimasker</artifactId>
	    <version>1.0.0</version>
	</dependency>
```