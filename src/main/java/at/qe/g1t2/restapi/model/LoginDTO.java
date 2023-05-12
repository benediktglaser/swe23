package at.qe.g1t2.restapi.model;

/**
 * This LoginDTO is sent to accessPoint to tell him the credentials to authenticate
 * via HTTP-Basic.
 */
public class LoginDTO {

    private String id;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
