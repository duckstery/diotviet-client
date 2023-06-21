package diotviet.server.validators;

import diotviet.server.exceptions.DataInconsistencyException;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.EntityProvider;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Visualize;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class BaseValidator<T> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Model mapper
     */
    @Autowired
    private ModelMapper modelMapper;
    /**
     * Key of GenericType (T)
     */
    private String key;

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
     * @param args
     */
    public void interrupt(String reason, String prefix, String attribute, String... args) {
        throw new ServiceValidationException(reason, prefix, attribute, args);
    }

    /**
     * Check if obj is not null
     *
     * @param obj
     * @return
     */
    public <S> S isExist(S obj) {
        if (Objects.isNull(obj)) {
            throw new DataInconsistencyException("not_exist");
        }

        return obj;
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param target:          Target of validation
     * @param format:          Preserved format
     * @param provider:        Provider to provider template item for validation
     * @param defaultProvider: Provider to provide default entity
     * @return
     */
    public void checkCode(Identifiable target, String format, EntityProvider provider, EntityProvider defaultProvider) {
        // Init holder
        long id = target.getId();
        String code = target.getCode();

        if (Objects.isNull(code) || code.isBlank()) {
            // If code is null, no need for validation
            code = generateCode(format, defaultProvider);
        } else if (id == 0) {
            // Validate for "CREATE"
            if (code.startsWith(format)) {
                // Check if code format is reserved
                interrupt("reserved", getKey(), "code");
            } else if (Objects.nonNull(provider.provide(code))) {
                // Check if code is exist
                interrupt("exists_by", getKey(), "code");
            }
        } else {
            // Validate for "UPDATE"
            // Get first Customer that has matched code
            Identifiable identifiable = provider.provide(code);
            if (Objects.isNull(identifiable) && code.startsWith(format)) {
                // Check if Customer with code is not exist and code format is reserved
                interrupt("reserved", getKey(), "code");
            } else if (identifiable.getId() != id) {
                // Else, check if Customer exist and that Customer is not self
                interrupt("exists_by", getKey(), "code");
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
    // Protected API
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

    /**
     * Assert for object
     *
     * @param isRequired
     */
    protected void assertObject(Object object, String attribute, boolean isRequired) {
        try {
            if (isRequired && Objects.isNull(OtherUtils.invokeGetter(object, attribute))) {
                interrupt("required", getKey(), attribute);
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    /**
     * Assert for string
     *
     * @param isRequired
     * @param min
     * @param max
     */
    protected void assertString(Object object, String attribute, boolean isRequired, int min, int max) {
        assertObject(object, attribute, isRequired);

        try {
            // Cast object to String
            String value = (String) OtherUtils.invokeGetter(object, attribute);

            // String is required
            if (isRequired && value.isBlank()) {
                interrupt("required", getKey(), attribute);
            }

            if (Objects.isNull(value) || value.isEmpty()) {
                return;
            }

            // Assert if string length is less than min
            if (value.length() < min || value.length() > max) {
                interrupt("string_min_max", getKey(), attribute, String.valueOf(min), String.valueOf(max));
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    /**
     * Assert for string
     *
     * @param object
     * @param attribute
     * @param max
     */
    protected void assertStringRequired(Object object, String attribute, int max) {
        assertString(object, attribute, true, 1, max);
    }

    /**
     * Assert for string
     *
     * @param object
     * @param attribute
     * @param min
     * @param max
     */
    protected void assertStringNonRequired(Object object, String attribute, int min, int max) {
        assertString(object, attribute, false, min, max);
    }

    /**
     * Assert for number
     *
     * @param isRequired
     * @param min
     * @param max
     */
    protected void assertInt(Object object, String attribute, boolean isRequired, int min, int max) {
        assertObject(object, attribute, isRequired);

        try {
            // Cast object to String
            Integer value = Integer.valueOf((String) OtherUtils.invokeGetter(object, attribute));

            if (Objects.isNull(value)) {
                return;
            }

            // Assert if string length is less than min
            if (value < min || value > max) {
                interrupt("int_min_max", getKey(), attribute, String.valueOf(min), String.valueOf(max));
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Get key (from ParameterizedType)
     *
     * @return
     */
    private String getKey() {
        // Return cached key
        if (Objects.nonNull(key)) {
            return key;
        }

        try {
            // Get actual type argument of superclass (It's T)
            key = OtherUtils.getTypeArguments(getClass())[0].getSimpleName().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            key = "";
        }

        return key;
    }
}
