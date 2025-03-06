package domain;

import java.util.UUID;

public class Owner {
    private int ownerId;
    private String name;
    private String phone_number;

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", phone_number='" + phone_number + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    private UUID uuid;

    public Owner() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Owner(String name, int ownerId, String phone_number) {
        this.name = name;
        this.ownerId = ownerId;
        this.phone_number = phone_number;
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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
