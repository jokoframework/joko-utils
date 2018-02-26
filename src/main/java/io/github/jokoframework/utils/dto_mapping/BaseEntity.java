package io.github.jokoframework.utils.dto_mapping;

import java.lang.reflect.ParameterizedType;

/**
 * <p>
 *     Provee un entity de conversion basica a DTO asumiendo que los
 *     parametros del Entity son un superconjunto de los parametros del DTO.
 *     El mapping se basa en el nombre
 * </p>
 */
public abstract class BaseEntity<T extends BaseDTO> implements DTOConvertable{


    public T toDTO(){
        try {
            //Crea una instancia del objeto que se parametrizo en la subclase
            Object obj = ((Class) ((ParameterizedType) this.getClass().
                    getGenericSuperclass()).getActualTypeArguments()[0])
                    .newInstance();

            T castedObj=(T)obj;
            return DTOUtils.fromEntityToDTO(this,castedObj);
        } catch (InstantiationException pE) {
            throw new IllegalStateException(pE);
        } catch (IllegalAccessException pE) {
            throw new IllegalStateException(pE);
        }

    }
}
