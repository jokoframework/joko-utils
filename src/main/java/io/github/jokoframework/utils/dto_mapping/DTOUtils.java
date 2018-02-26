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
     * y devuelve una lista de DTOs
     *
     * @param entities entidades
     * @param clazz clase
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        List<DTOConvertable> l = (List<DTOConvertable>) entities;
        for (DTOConvertable o : l) {
            list.add((T) o.toDTO());
        }
        return list;
    }

    public static <T extends BaseDTO> T fromEntityToDTO(DTOConvertable entity, T destination) {
        BeanUtils.copyProperties(entity, destination);
        return destination;
    }


    public static <T extends DTOConvertable> T fromDTOToEntity(BaseDTO dto, T entity) {
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    public static <T extends BaseDTO> T fromDTOToDTO(BaseDTO origin, T destination) {
        BeanUtils.copyProperties(origin, destination);
        return destination;
    }

    public static <T extends BaseDTO> T shallowCopy(T origin, T destination) {
        BeanUtils.copyProperties(origin, destination);
        return destination;
    }

    /**
     * Get a safe list of objects or empty list
     *
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
