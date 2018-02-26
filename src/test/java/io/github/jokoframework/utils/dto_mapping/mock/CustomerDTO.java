package io.github.jokoframework.utils.dto_mapping.mock;

import io.github.jokoframework.utils.dto_mapping.BaseDTO;

import java.util.Date;

/**
 * Created by danicricco on 2/26/18.
 */
public class CustomerDTO implements BaseDTO{


    private String firstName;
    private String lastName;
    private Date birthDate;



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String pFirstName) {
        firstName = pFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String pLastName) {
        lastName = pLastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date pBirthDate) {
        birthDate = pBirthDate;
    }
}
