# joko-utils
Es un conjunto de clases que apoyan en el desarrollo de un proyecto backend. 
Provee las siguientes funcionalidades:

## Mapping de objetos
Facilita la conversion de Entities a DTOs y viceversa.

El entity debe implementar la interfaz DTOConvertable y especificar a que 
clase se mapea. Ejemplo:

```java
public class CustomerEntity implements DTOConvertable<CustomerDTO>
```
Esto obligará a la clase a implementar los metodos:
* toDTO . Devuelve un DTO, en el ejemplo de tipo DTOConvertable
* fromDTO. Setea las propiedades del entity basado en la info del DTO 
parametro. En el ejemplo, el DTO parametor sera del tipo DTOConvertable

### Acelerando la conversion con BaseEntity
La clase BaseEntity posee una implementacion particular para toDTO y fromDTO 
en la cual se copian los atributos basados en los nombres. Aquellos atributos
 que no coincidan son simplemente ignorados.
 
 Heredando de la clase y especificando el destino, es suficiente para que el 
 entity no necesite implementar estos metodos.

```java  
public class CustomerEntity extends BaseEntity<CustomerDTO>  
```

### DTOUtils
Se provee la clase DTOUtils que posee varios metodos para realizar mapeos de:
* Un entity a un DTO
* Un DTO a un entity
* Un DTO a un DTO
* Una lista de entities a un DTO

### Ejemplo de implementacion
Un ejemplo para comprender mejor como utilizar los metodos de conversion puee
 verse en la clase ConversionTest
 
## Clases utilitarias
* TXUUIDGenerator: Generacion de UUIDs que sean URL friendly y altamente random.
* TimeUtils : Utilidades para formateo de fechas

# Changelog
El historico de versiones puede verse en el [Changelog](CHANGELOG.MD)