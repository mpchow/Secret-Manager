# Secret Manager
HTTP Spring Boot service built with Gradle to handle secret management. Administrators can register applications, secrets, and decide which applications can access which secrets. An application can access a permitted secret through Basic Auth with the shared token given by the Administrator.   

1. [How to run](#how-to-run)
2. [API Specification](#api-specification)

You can find further information in the design doc under `/docs`

## How to run
**Preinstallation**  
Secret Manager is built on Spring Boot 2.7.4 and Java 17.  
Steps to install the latest versions can be found below.   
[Gradle installation](https://gradle.org/install/) (If on macOS can use homebrew)  
[Java installation](https://www.oracle.com/java/technologies/downloads/)

**Usage**
1. Clone the repository
2. Run `./gradle bootRun` in the top level directory
3. The service can be found at `http://localhost:8080`

**Testing**  
Run `gradle test` from the top level directory

## API Specification
### /application
**POST**  
Registers a new application to the service and responds with an id and secret token to be used for Basic Auth. If the application is already registered will return an error.

Body:
```
{  
    name: String  
}  
```
Response Codes: 200, 400, 500
Response:
```
{
    id: String
    token: String
}
```

### /secret/{id}
**GET**  
Retrieves the requested secret value if the application has permission. Requires Basic Auth

Response Codes: 200, 401, 404, 500  
Response:  
``` 
{
    secret: String    
}
```

### /secret
**POST**  
Registers a new secret to the service.

Body:
```
{  
    id: String  
    secretVal: String  
}  
```


Response Codes: 200, 400, 500

### /access
**POST**  
Provides an application access to a secret  
Body:

```
{
    id: String
    secretId: String
}
```

Response Codes: 200, 400, 404, 500
