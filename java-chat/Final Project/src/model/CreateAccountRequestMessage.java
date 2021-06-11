package model;

import java.io.Serializable;

public class CreateAccountRequestMessage implements Serializable {
    public String account;
    public String password;

    public CreateAccountRequestMessage(String account,String password) {
        this.account = account;
        this.password = password;
    }
}
