package io.github.jokoframework.utils.dto_mapping;

/**
 * <p>
 * Interfaz para realizar conversion de objetos desde Entity a DTO.
 * </p>
 * <p>
 *     Se debe especificar el metodo toDTO devolviendo el objeto que se haya
 *     definido como parametro de la clase.
 * </p>
 * <p>
 *     Si el entity posee un superconjunto de los atributos del DTO se puede
 *     evaluar extender de la clase {@link BaseEntity}
 * </p>
 *
 */
public interface DTOConvertable<T extends BaseDTO> {
    /**
     *
     * @return El DTO al que la clase hace referencia
     */
    T toDTO();

    /**
     * Asigna los valores del DTO a las propiedades del entity
     * @param dto
     */
    void fromDTO(T dto);
}
