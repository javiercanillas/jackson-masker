# jackson-masker
Sensitive information (such us emails, IDs or phone numbers) is a serious problem 
to deal with. If you have objects that are logged or write into files where 
masking is required, and you are already using Jackson ObjectMapper solution in 
your application, this library might be of some help.

[![Java CI with Maven](https://github.com/javiercanillas/jackson-masker/actions/workflows/maven-build.yml/badge.svg)](https://github.com/javiercanillas/jackson-masker/actions/workflows/maven-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=javiercanillas_jackson-masker&metric=alert_status)](https://sonarcloud.io/dashboard?id=javiercanillas_jackson-masker)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=javiercanillas_jackson-masker&metric=coverage)](https://sonarcloud.io/dashboard?id=javiercanillas_jackson-masker)
---
## Types supported
- String
- List\<String\>
- Set\<String\>
- String[]
- Map<?, String>

**Note:** If you consider a type might be missing or you like it on this list, feel free to contact [me](https://github.com/javiercanillas/jackson-masker/issues).

## How to use
Lets suppose you have an entity that has an email as part of its attributes:
```java
class EntityA {
    String email;
    ...
}
```
Well, with this class you can annotate this attribute as sensitive data like this:

```java
import MaskString;

class EntityA {
    @MaskString
    String id;

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
```
And it will be masked when writing using a Jackson ObjectMapper like this:
```java
ObjectMapper mapper = new ObjectMapper();
final ObjectWriter maskedWriter = mapper.writerWithView(io.github.javiercanillas.jackson.masker.view.Masked.class);
final EntityA obj = new EntityA();
obj.setId("abcd1234");

maskedWriter.writeValueAsString(obj);
```
And it will produce:
```json
{
  "id": "********"
}
```
Furthermore, you can customize the masking character and if you want to 
leave some forst and last characters. 
```java
    @MaskString(keepInitialCharacters = 1, keepLastCharacters = 4, maskCharacter = '-')
    String id;
```

The produced json in this case would be:
```json
{
  "id": "a---1234"
}
```

More examples on test and [wiki](https://github.com/javiercanillas/jackson-masker/wiki)

## How to install
If you prefer to use maven central releases, you can find it [here](https://mvnrepository.com/artifact/io.github.javiercanillas/jackson-masker). Also, if you support [Jitpack.io](https://jitpack.io/) you can find it [here](https://jitpack.io/#javiercanillas/jackson-masker)
