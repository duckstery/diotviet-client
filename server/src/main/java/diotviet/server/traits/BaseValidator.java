package diotviet.server.traits;

import diotviet.server.exceptions.BadRequestException;
import diotviet.server.exceptions.DataInconsistencyException;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.EntityProvider;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import diotviet.server.views.Visualize;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.stream.IntStream;

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
     * @return
     */
    public T map(Object object, Class<T> destinationType) {
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
     * Interrupt validator with an Exception
     *
     * @param reason
     * @param payload
     */
    public void interrupt(String reason, String payload) {
        interrupt(reason, "", payload);
    }

    /**
     * Interrupt validator with a DataInconsistencyException
     *
     * @param key
     */
    public void inconsistent(String key) {
        throw new DataInconsistencyException(key);
    }

    /**
     * Interrupt validator with a BadRequestException
     *
     * @param key
     */
    public void abort(String key) {
        throw new BadRequestException(key);
    }

    /**
     * Check if obj is not null
     *
     * @param obj
     * @return
     */
    public <S> S isExist(S obj) {
        if (Objects.isNull(obj)) {
            inconsistent("inconsistent_data");
        }

        return obj;
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param target:          Target of validation
     * @param format:          Preserved format
     * @param lengthOfNumber:  Length of number after preserved format
     * @param provider:        Provider to provider template item for validation
     * @param defaultProvider: Provider to provide default entity
     * @return
     */
    public void checkCode(Identifiable target, String format, int lengthOfNumber, EntityProvider<Identifiable, String> provider, EntityProvider<Identifiable, String> defaultProvider) {
        // Init holder
        long id = target.getId();
        String code = target.getCode();

        if (StringUtils.isBlank(code)) {
            // If code is null, no need for validation
            code = generateCode(format, lengthOfNumber, defaultProvider);
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
            // Check if identifiable is null
            boolean identifiableIsNull = Objects.isNull(identifiable);
            if (identifiableIsNull && code.startsWith(format)) {
                // Check if Customer with code is not exist and code format is reserved
                interrupt("reserved", getKey(), "code");
            } else if (!identifiableIsNull && identifiable.getId() != id) {
                // Else, check if Customer exist and that Customer is not self
                interrupt("exists_by", getKey(), "code");
            }
        }

        target.setCode(code);
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
    public void checkCode(Identifiable target, String format, EntityProvider<Identifiable, String> provider, EntityProvider<Identifiable, String> defaultProvider) {
        checkCode(target, format, 5, provider, defaultProvider);
    }

    /**
     * Check if img source is default, then set it to null
     *
     * @param object
     */
    public void checkImageSrc(Visualize object) {
        try {
            // Source holder
            String src = object.getSrc();

            if (StringUtils.isBlank(src) || src.equals("/files/default.jpeg")) {
                object.setSrc("default.jpeg");
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Optimistic lock check
     *
     * @param lockable
     * @param olRepo
     */
    public void checkOptimisticLock(Lockable lockable, OptimisticLockRepository olRepo) {
        // Check if Lockable is newly added
        if (lockable.getId() <= 0L) {
            // Set lockable to 0
            lockable.setVersion(0L);
            return;
        }

        // Check if lockable is a valid version of Entity
        if (!olRepo.existsByIdAndVersion(lockable.getId(), lockable.getVersion())) {
            inconsistent("invalid_lock");
        }

        // Increase lock
        lockable.setVersion(lockable.getVersion() + 1);
    }

    /**
     * Mass optimistic lock check for multiple id
     *
     * @param ids
     * @param versions
     * @param olRepo
     */
    public void massCheckOptimisticLock(Long[] ids, Long[] versions, OptimisticLockRepository olRepo) {
        // Merge list of ids and list of versions to a list of tuple of (id-version)
        String[] tuples = IntStream.range(0, ids.length)
                .mapToObj(index -> String.format("%d-%d", ids[index], versions[index]))
                .toArray(String[]::new);

        // Check if all tuples are exists
        if (!olRepo.existsAllByTuplesOfIdAndVersion(tuples)) {
            inconsistent("invalid_locks");
        }
    }

    // ****************************
    // Protected API
    // ****************************

    /**
     * Generate code
     *
     * @param format
     * @param lengthOfNumber
     * @param provider
     * @return
     */
    protected String generateCode(String format, int lengthOfNumber, EntityProvider<Identifiable, String> provider) {
        // Get Product with "largest" code
        Identifiable identifiable = provider.provide(format + "%");
        // Get number part from code
        String alphanumeric = Objects.isNull(identifiable) ? "0" : identifiable.getCode().substring(2);

        // Generate format
        String f = "%s%0" + lengthOfNumber + "d";
        return String.format(f, format, Integer.parseInt(alphanumeric) + 1);
    }

    /**
     * Generate code
     *
     * @param format
     * @param provider
     * @return
     */
    protected String generateCode(String format, EntityProvider<Identifiable, String> provider) {
        return generateCode(format, 5, provider);
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

            if (StringUtils.isEmpty(value)) {
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
