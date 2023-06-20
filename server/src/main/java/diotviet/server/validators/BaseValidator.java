package diotviet.server.validators;

import diotviet.server.exceptions.DataInconsistencyException;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.views.EntityProvider;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Visualize;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

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

    /**
     * Check if obj is not null
     *
     * @param obj
     * @return
     */
    public <T> T isExist(T obj) {
        if (Objects.isNull(obj)) {
            throw new DataInconsistencyException("not_exist");
        }

        return obj;
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param target: Target of validation
     * @param format: Preserved format
     * @param provider: Provider to provider template item for validation
     * @param defaultProvider: Provider to provide default entity
     * @return
     */
    public void checkCode(Identifiable target, String format, EntityProvider provider, EntityProvider defaultProvider) {
        // Init holder
        long id = target.getId();
        String code = target.getCode();
        String key = target.getClass().getSimpleName().toLowerCase();

        if (Objects.isNull(code)) {
            // If code is null, no need for validation
            code = generateCode(format, defaultProvider);
        } else if (id == 0) {
            // Validate for "CREATE"
            if (code.startsWith(format)) {
                // Check if code format is reserved
                interrupt("reserved", key, "code");
            } else if (Objects.nonNull(provider.provide(code))) {
                // Check if code is exist
                interrupt("exists_by", key, "code");
            }
        } else {
            // Validate for "UPDATE"
            // Get first Customer that has matched code
            Identifiable identifiable = provider.provide(code);
            if (Objects.isNull(identifiable) && code.startsWith(format)) {
                // Check if Customer with code is not exist and code format is reserved
                interrupt("reserved", key, "code");
            } else if (identifiable.getId() != id) {
                // Else, check if Customer exist and that Customer is not self
                interrupt("exists_by", key, "code");
            }
        }

        target.setCode(code);
    }

    /**
     * Check if img source is default, then set it to null
     *
     * @param object
     */
    public void checkImageSrc(Visualize object) {
        try {
            if (object.getSrc().equals("/files/default.jpeg")) {
                object.setSrc(null);
            } else if (object.getSrc().contains("/files/")) {
                object.setSrc(object.getSrc().substring(7));
            }
        } catch (Exception ignored) {
        }
    }


    // ****************************
    // Private API
    // ****************************

    /**
     * Generate code
     *
     * @param format
     * @param provider
     * @return
     */
    protected String generateCode(String format, EntityProvider provider) {
        // Get Product with "largest" code
        Identifiable identifiable = provider.provide(format + "%");
        // Get number part from code
        String alphanumeric = Objects.isNull(identifiable) ? "0" : identifiable.getCode().substring(2);

        return String.format("%s%05d", format, Integer.parseInt(alphanumeric) + 1);
    }
}
