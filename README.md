# Secret Manager
HTTP Spring Boot service to handle secret management. Administrators can register applications, secrets, and decide which applications can access which secrets. An application can access a permitted secret through Basic Auth with the shared token given by the Administrator.   

## How to run

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
Response:   
``` secretVal: String```

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



