package com.store.mycoffeestore.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Accounts {
    private String userName;
    private String password;

    public Accounts(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
