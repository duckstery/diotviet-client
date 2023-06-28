package diotviet.server.utils;

import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.templates.EntityHeader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Utility to support and interact with Entity
 */
@Component
public class EntityUtils {

    // ****************************
    // Properties
    // ****************************

    @Autowired
    private MessageSource messageSource;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get headers of Entity
     *
     * @param entityClass
     * @return
     */
    public EntityHeader[] getHeaders(Class<?> entityClass) {
        // Create output holder
        ArrayList<EntityHeader> entityHeaders = new ArrayList<>();
        // Get uncapitalize basename for translation
        String base = StringUtils.uncapitalize(entityClass.getSimpleName());

        for (Field field : entityClass.getDeclaredFields()) {
            // Continue to next field
            if (field.isAnnotationPresent(InitIgnore.class)) {
                continue;
            }

            // Uncapitalize field name
            String fieldBase = field.getName();
            // Get translation basename
            String basename = base + "_" + fieldBase;
            // Create EntityHeader
            entityHeaders.add(new EntityHeader(
                    fieldBase,
                    messageSource.getMessage(basename, null, fieldBase, LocaleContextHolder.getLocale()),
                    fieldBase,
                    !field.isAnnotationPresent(InitHide.class)
            ));
        }

        return entityHeaders.toArray(new EntityHeader[0]);
    }
}
