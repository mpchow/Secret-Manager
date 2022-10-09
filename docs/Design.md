# Design Document
Author: Matthew Chow

## Overview
The Secret Manager is a service to handle secret management. There are 2 types of users, Administrators and Applications.
An Administrator can register Applications and Secrets. They can also determine which Secrets an Application is able to view.
Applications use Basic Auth to reach the service and retrieve a secret.

## Goals
There a few goals with this project, due to the limited time there are limitations that are discussed in the limitations section.
- Registering an application
- 

## High Level Design
In order to make the design easy to iterate upon, functionality was split up between the __, Service, and Data Layer

Here's a diagram showing the overall flow of the application
## 

## Limitations and Improvements