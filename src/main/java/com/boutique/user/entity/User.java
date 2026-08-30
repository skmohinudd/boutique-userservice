package com.boutique.user.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_cognito_sub", columnNames = "cognito_sub")
        }
)
public class User {

    @Id
    private UUID id;

    @Column(name = "cognito_sub", length = 100)
    private String cognitoSub;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected User() {
    }

    public User(
            UUID id,
            String cognitoSub,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            UserStatus status
    ) {
        this.id = id;
        this.cognitoSub = cognitoSub;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateProfile(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void linkCognitoIdentity(String cognitoSub) {
        if (this.cognitoSub != null && !this.cognitoSub.equals(cognitoSub)) {
            throw new IllegalStateException("User is already linked to a different Cognito identity.");
        }
        this.cognitoSub = cognitoSub;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public UUID getId() { return id; }
    public String getCognitoSub() { return cognitoSub; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
