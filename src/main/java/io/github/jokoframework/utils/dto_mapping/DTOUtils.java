package io.github.jokoframework.utils.dto_mapping;

/**
 * Created by danicricco on 2/26/18.
 */

import org.springframework.beans.BeanUtils;
import org.springframework.beans.support.PagedListHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 * <p>
 * Utilidades para trabajar con DTOs.
 * </p>
 * Los DTOs deben implementar la interfaz
 * {@link BaseDTO} Los elementos que se pueden convertir a DTO deben implementar
 * {@link DTOConvertable}
 *
 * @author danicricco
 *
 */
public class DTOUtils {

    private DTOUtils() {
    }

    /**
     * Recorre una lista de elemenos de tipo DTOConvertable, los conviente a DTO
     * y devuelve una lista de DTOs.
     * Si la lista de entities es vacio o null, devuelve un array vacio/
     * @param entities Soporta null values
     * @param clazz La clase DTO destino
     * @return Siempre devuelve un array, el cual puede estar vacio
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        if(entities!=null){
            List<DTOConvertable> l = (List<DTOConvertable>) entities;
            for (DTOConvertable o : l) {
                list.add((T) o.toDTO());
            }
        }
        return list;
    }

    /**
     * Convierte un entity a un DTO en base a las propiedades. Busca que los
     * nombres de los atributos sean iguales.
     * @param entity El entity original
     * @param destination El DTO d destino
     * @param <T>
     * @return
     */
    public static <T extends BaseDTO> T fromEntityToDTO(DTOConvertable entity, T destination) {
        BeanUtils.copyProperties(entity, destination);
        return destination;
    }

    /**
     * Conviertie un DTO a un entity en base a las propiedades. Busca que los
     * nombres de los atributos sean iguales.
     * @param dto
     * @param entity
     * @param <T>
     * @return
     */
    public static <T extends DTOConvertable> T fromDTOToEntity(BaseDTO dto, T entity) {
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    /**
     * Copia las propiedades de un objeto a otro basado en el nombre d elas
     * mismas.
     * @param origin
     * @param destination
     * @param <T>
     * @return
     */
    public static <T extends BaseDTO> T fromDTOToDTO(BaseDTO origin, T destination) {
        BeanUtils.copyProperties(origin, destination);
        return destination;
    }

    public static <T extends BaseDTO> T shallowCopy(T origin, T destination) {
        BeanUtils.copyProperties(origin, destination);
        return destination;
    }

    /**
     * Get a safe list of objects or empty list.
     * Util para devolver listas en las que esperamos que la lista retornada
     * no sea null, sino una lista vacia.
     * @param all original
     * @param <T> Holder type
     * @return resulting list or empty list
     */
    public static <T> List<T> getListOrEmpty(List<T> all) {
        if (all != null) {
            return all;
        }
        else {
            return Collections.emptyList();
        }
    }
}
