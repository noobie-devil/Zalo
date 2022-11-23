package hcmute.edu.vn.zalo_05.RequestDto;

public class CreateRoomRequest {
    private String name;
    private String uniqueName;

    public CreateRoomRequest(String name, String uniqueName) {
        this.name = name;
        this.uniqueName = uniqueName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
