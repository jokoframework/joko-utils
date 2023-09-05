# joko-utils
Es un conjunto de clases que apoyan en el desarrollo de un proyecto backend. 
Provee las siguientes funcionalidades:

# Utilización de la ultima versión
    <dependency>
        <groupId>io.github.jokoframework</groupId>
        <artifactId>joko-utils</artifactId>
        <version>0.6.4</version>
    </dependency>
    
## Mapping de objetos
Facilita la conversión de Entities a DTOs y viceversa.

El entity debe implementar la interfaz DTOConvertable y especificar a que 
clase se mapea. Ejemplo:

```java
public class CustomerEntity implements DTOConvertable<CustomerDTO>
```
Esto obligará a la clase a implementar los métodos:
* toDTO . Devuelve un DTO, en el ejemplo de tipo DTOConvertable
* fromDTO. Setea las propiedades del entity basado en la info del DTO 
parámetro. En el ejemplo, el DTO parametor sera del tipo DTOConvertable

### Acelerando la conversión con BaseEntity
La clase BaseEntity posee una implementación particular para toDTO y fromDTO 
en la cual se copian los atributos basados en los nombres. Aquellos atributos
 que no coincidan son simplemente ignorados.
 
 Heredando de la clase y especificando el destino, es suficiente para que el 
 entity no necesite implementar estos métodos.

```java  
public class CustomerEntity extends BaseEntity<CustomerDTO>  
```

### DTOUtils
Se provee la clase DTOUtils que posee varios métodos para realizar mapeos de:
* Un entity a un DTO
* Un DTO a un entity
* Un DTO a un DTO
* Una lista de entities a un DTO

### Ejemplo de implementación
Un ejemplo para comprender mejor como utilizar los métodos de conversión puee
 verse en la clase ConversionTest
 
## Clases utilitarias
* TXUUIDGenerator: Generación de UUIDs que sean URL friendly y altamente random.
* TimeUtils : Utilidades para formateo de fechas

# Changelog
El histórico de versiones puede verse en el [Changelog](CHANGELOG.MD)
Prueba 2023-09-05
