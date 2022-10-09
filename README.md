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

1. Clone the repository
2. Run `./gradle bootRun` in the top level directory
3. The service can be found at `http://localhost:8080`

## API Specification
### /application
**POST**  
Registers a new application to the service. If the application is already registered, replaces the secretToken  

Body:
```
{  
    id: String  
    secretToken: String  
}  
```
Response Codes: 200, 400, 500

### /secret/{id}
**GET**  
Retrieves the requested secret value if the application has permission. Requires Basic Auth

Response Codes: 200, 401, 404, 500  
Response: (As a String)   
``` secretVal```

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
