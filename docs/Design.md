# Design Document
Author: Matthew Chow

## Overview
The Secret Manager is a service to handle secret management. There are 2 types of users, Administrators and Applications.
An Administrator can register Applications and Secrets. They can also determine which Secrets an Application is able to view.
Applications use Basic Auth to reach the service and retrieve a secret.

## Goals
There a few goals with this project, due to the limited time there are limitations that are discussed in the limitations section.
- Application registration which will return an application id and secret shared token
- Secret registration
- Granting application access to a secret
- Application retrieving a secret by authenticating with Basic Auth

An example flow would be as follows
- Admin registers a new application `app-foobar`, secret shared token `secret123` and id `123` are generated and returned
- Admin registers a new secret `postgres-user-transaction-pwd` with value `super-secret-password`
- Admin gives `app-foobar` access to `postgres-user-transaction-pwd`
- `app-foobar` retrieves the secret for `postgres-user-transaction-pwd` with the secret shared token and id

## High Level Design
In order to make the design easy to iterate upon, functionality was split up between the Presentation, Service, and Data Layer. This was done to make code that is simple and reusable. It also follows Spring's design pattern.

**Presentation Layer**  
This refers to the controller classes. The controller classes simply determine how the response data should be formatted and presented while accessing the Service Layer.

**Service Layer**  
This refers to the service classes. The service classes handle all business logic like authentication, entity creation, permission updates.

**Data Layer**  
This refers to the model classes and repository interfaces. These handle persisting data to the database.  
For simplicity the h2 database was chosen since it is automatically configured by Spring Boot during runtime.

**Note:** A diagram should be here but I ran out of time to create one.

## Data Models

## API Specification
The endpoints were split up based on what operation and data they need to manipulate. `/secret` and `/application` are created to handle all CRUD methods for the respective model.

### /application
**POST**  
Registers a new application to the service and responds with an id and secret token to be used for Basic Auth. If the application is already registered will return an error.

Body:
```
{  
    name: String  
}  
```
Response Codes:
```
200: Application is successfully registered
400: Request body is not properly formed or application is already registered
500: Internal server error
```

Response:
```
{
    id: String
    token: String
}
```

### /secret/{id}
**GET**  
Retrieves the requested secret value if the application has permission. Requires Basic Auth using the returned `id` and `token
from the `/application` endpoint
Response Codes:
```
200: Successfully authenticated and found corresponding secret
401: No auth provided or credentials are invalid
404: Requested secret is not found
500: Internal server error
```
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
```
200: Secret is successfully registered
400: Request body is not properly formed or application is already registered
500: Internal server error
```

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

**Data Models**




## Limitations, and Improvements
These are a list of known limitations of the service and different ways they could be improved on.

| Limitation                                                           | Improvement                                                                                                     |
|----------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| The endpoints the Admin accesses are unsecured                       | Secure the Admin endpoints<br/> Unsure which auth method to utilize but further investigation would be required |
| The secret value is stored in plaintext in the database (not secure) | Encrypt the secret value in the database <br/> This could be done by providing an encryption key once           |
| Applications cannot be updated                                       | Add endpoints to allow for updating                                                                             |
| Secrets and Applications cannot be deleted                           | Add endpoints to allow for deletion                                                                             |
