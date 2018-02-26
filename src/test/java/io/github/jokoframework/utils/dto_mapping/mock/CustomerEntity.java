package io.github.jokoframework.utils.dto_mapping.mock;

import io.github.jokoframework.utils.dto_mapping.BaseDTO;
import io.github.jokoframework.utils.dto_mapping.DTOConvertable;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;

import java.util.Date;

/**
 * Created by danicricco on 2/26/18.
 */
public class CustomerEntity implements DTOConvertable<CustomerDTO>{

    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;

    public CustomerEntity(){

    }
    public CustomerEntity(Long pId, String pFirstName, String pLastName, Date pBirthDate) {
        id = pId;
        firstName = pFirstName;
        lastName = pLastName;
        birthDate = pBirthDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

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

    public CustomerDTO toDTO() {
        return DTOUtils.fromEntityToDTO(this,new CustomerDTO());
    }

    @Override
    public boolean equals(Object pO) {
        if (this == pO) return true;
        if (!(pO instanceof CustomerEntity)) return false;

        CustomerEntity that = (CustomerEntity) pO;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        return birthDate != null ? birthDate.equals(that.birthDate) : that.birthDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }
}
