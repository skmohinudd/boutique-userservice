package com.boutique.user.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.boutique.user.dto.CreateUserRequest;
import com.boutique.user.dto.UpdateUserRequest;
import com.boutique.user.service.UserService;
import tools.jackson.databind.JsonNode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

public final class UserLambdaHandler implements RequestStreamHandler {
    private final UserService service = LambdaSupport.bean(UserService.class);

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        try {
            JsonNode event = LambdaSupport.readEvent(input);
            String method = LambdaSupport.method(event);
            String path = LambdaSupport.path(event);

            if ("POST".equals(method) && "/api/v1/users".equals(path)) {
                CreateUserRequest request = LambdaSupport.validate(
                        LambdaSupport.JSON.readValue(
                                LambdaSupport.body(event),
                                CreateUserRequest.class
                        )
                );
                LambdaSupport.respond(output, 201, service.createUser(request));
                return;
            }

            if (path.startsWith("/api/v1/users/")) {
                String suffix = path.substring("/api/v1/users/".length());
                boolean deactivate = suffix.endsWith("/deactivate");
                String idText = deactivate
                        ? suffix.substring(0, suffix.length() - "/deactivate".length())
                        : suffix;
                UUID userId = UUID.fromString(idText);

                if ("GET".equals(method) && !deactivate) {
                    LambdaSupport.respond(output, 200, service.getUser(userId));
                    return;
                }

                if ("PUT".equals(method) && !deactivate) {
                    UpdateUserRequest request = LambdaSupport.validate(
                            LambdaSupport.JSON.readValue(
                                    LambdaSupport.body(event),
                                    UpdateUserRequest.class
                            )
                    );
                    LambdaSupport.respond(output, 200, service.updateUser(userId, request));
                    return;
                }

                if ("POST".equals(method) && deactivate) {
                    LambdaSupport.respond(output, 200, service.deactivateUser(userId));
                    return;
                }
            }

            LambdaSupport.respond(output, 404, Map.of("message", "User route not found"));
        } catch (Throwable failure) {
            try {
                LambdaSupport.fail(output, failure, context);
            } catch (Exception responseFailure) {
                throw new RuntimeException(responseFailure);
            }
        }
    }
}
