package io.github.jokoframework.utils.dto_mapping;

import io.github.jokoframework.utils.dto_mapping.mock.CustomerDTO;
import io.github.jokoframework.utils.dto_mapping.mock.CustomerEntity;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by danicricco on 2/26/18.
 */
public class ConversionTest {


    /**
     * Prueba convertir un Entity a un DTO y comprueba que los valores son
     * los esperados
     */
    @Test
    public void testToDTOAndBack(){
        //1) Crea un entity de prueba
        CustomerEntity customer = createCustomer();
        CustomerDTO dto = customer.toDTO();

        //2) Comprueba que los valores se han copiado correctamente
        checkValuesOfMockObject(customer, dto);

        //3) Carga los valores del DTO a un entity
        CustomerEntity customerEntity2 = new CustomerEntity();
        customerEntity2.fromDTO(dto);

        //Como el ID no es parte del DTO lo setea explicitamente
        customerEntity2.setId(customer.getId());
        Assert.assertEquals(customer,customerEntity2);

    }

    private void checkValuesOfMockObject(CustomerEntity pCustomer, CustomerDTO pCustomerDTO) {
        Assert.assertEquals(pCustomer.getFirstName(), pCustomerDTO.getFirstName());
        Assert.assertEquals(pCustomer.getLastName(), pCustomerDTO.getLastName());
        Assert.assertEquals(pCustomer.getBirthDate(), pCustomerDTO.getBirthDate());
    }

    @Test
    public void testConvertToList(){
        ArrayList<CustomerEntity> entities=new ArrayList<CustomerEntity>();
        int numberOfCustomers=5;
        //Agrega varios customer
        for(int i=0;i<numberOfCustomers;i++){
            entities.add(createCustomer());
        }

        List<CustomerDTO> dtoList = DTOUtils.fromEntityToDTO(entities,
                CustomerDTO.class);
        for(int i=0;i<numberOfCustomers;i++){
            CustomerEntity entity = entities.get(i);
            CustomerDTO dto = dtoList.get(i);
            checkValuesOfMockObject(entity, dto);
        }

    }

    private CustomerEntity createCustomer(){
        SimpleDateFormat format=new SimpleDateFormat("dd/mm/yyyy");
        try {
            return new CustomerEntity(30l,"Thomas","Hobbes",format.parse
                    ("5/04/1588"));
        } catch (ParseException pE) {
            throw new RuntimeException(pE);
        }
    }
}
