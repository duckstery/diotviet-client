package diotviet.server.validators;

import diotviet.server.exceptions.ServiceValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseValidator {

    // ****************************
    // Properties
    // ****************************

    /**
     * Model mapper
     */
    @Autowired
    private ModelMapper modelMapper;

    // ****************************
    // Protected API
    // ****************************

    /**
     * Map object (Record included) to object (Record excluded)
     *
     * @param object
     * @param destinationType
     * @param <S>
     * @return
     */
    public <S> S map(Object object, Class<S> destinationType) {
        return modelMapper.map(object, destinationType);
    }

    /**
     * Interrupt validator with an Exception
     *
     * @param reason
     * @param prefix
     * @param attribute
     */
    public void interrupt(String reason, String prefix, String attribute) {
        throw new ServiceValidationException(reason, prefix, attribute);
    }
}
