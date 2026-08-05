# Boutique User Service

Owns Boutique application user profiles.

## Local endpoints

- `POST /api/v1/users`
- `GET /api/v1/users/{userId}`
- `PUT /api/v1/users/{userId}`
- `POST /api/v1/users/{userId}/deactivate`
- `GET /actuator/health`
- `GET /swagger-ui.html`

Passwords are not stored here. Authentication will later be provided by Amazon Cognito.
