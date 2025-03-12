package domain;

import java.util.UUID;

public class Owner {
    private int ownerId;
    private String name;
    private String phoneNumber;

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", phone_number='" + phoneNumber + '\'' +
                ", uuid=" + ownerUuid +
                '}';
    }

    private UUID ownerUuid;

    public Owner() {
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Owner(String name, int ownerId, String phoneNumber) {
        this.name = name;
        this.ownerId = ownerId;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
