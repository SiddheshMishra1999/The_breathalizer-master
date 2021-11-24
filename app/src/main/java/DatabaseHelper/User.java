package DatabaseHelper;

import java.util.ArrayList;

public class User {
    public String email, username, first_name, last_name, age, province ,license, values;





    public User(){

    }
    public User(String email, String username, String first_name, String last_name, String age, String province, String license) {
        this.email = email;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.age = age;
        this.province = province;
        this.license = license;
    }

    public User(String email, String username, String first_name, String last_name, String age, String province, String license, String values) {
        this.email = email;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.age = age;
        this.province = province;
        this.license = license;
        this.values = values;
    }
}
