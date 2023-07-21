package diotviet.server.utils;

import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.annotations.PrintTag;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Print.PrintableTag;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

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

    /**
     * Get printable field of Entity
     *
     * @param entityClass
     * @return
     */
    public PrintableTag[] getPrintableTag(Class<?> entityClass) {
        // Trace and harvest
        return traceAndHarvestPrintTag(entityClass, "", new ArrayList<>()).toArray(new PrintableTag[0]);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Trace and harvest PrintTag
     *
     * @param entityClass
     * @param prefix
     * @param harvested
     * @return
     */
    private ArrayList<PrintableTag> traceAndHarvestPrintTag(Class<?> entityClass, String prefix, ArrayList<String> harvested) {
        // Create output
        ArrayList<PrintableTag> printableFields = new ArrayList<>();
        // Get uncapitalize basename for identification
        String base = (StringUtils.isEmpty(prefix) ? "" : prefix + "_") + StringUtils.uncapitalize(entityClass.getSimpleName());

        // Cache the field's class, so it won't be harvested next time
        harvested.add(entityClass.getName());

        // Iterate through each field
        for (Field field : entityClass.getDeclaredFields()) {
            // Continue to next field
            if (!field.isAnnotationPresent(PrintTag.class)) {
                continue;
            }

            // Get Annotation
            PrintTag printTag = field.getAnnotation(PrintTag.class);
            // Get PrintTag type
            String type = (ArrayUtils.isNotEmpty(printTag.component()) ? "NestedMenuItem" : "MenuItem").toLowerCase();
            // Uncapitalize field name
            String fieldBase = field.getName();
            // Get translation basename
            String key = base + "_" + (StringUtils.isEmpty(printTag.value()) ? fieldBase : printTag.value().isEmpty());

            // Create subfield
            ArrayList<PrintableTag> subfields = new ArrayList<>();
            // Check if this is a nested Print Tag
            if (ArrayUtils.isNotEmpty(printTag.component())) {
                // Make sure does not re-harvest harvested field to avoid infinite references
                if (!harvested.contains(printTag.component()[0].getName())) {
                    // Harvest subfields
                    subfields = traceAndHarvestPrintTag(printTag.component()[0], base, harvested);
                }
            }

            // Check if subfields is force merged into upper class
            if (printTag.forceMerge()) {
                // Add all
                printableFields.addAll(subfields);
                // Clear subfields
                subfields.clear();
            } else {
                printableFields.add(new PrintableTag(type, key, subfields.toArray(new PrintableTag[0])));
            }
        }

        return printableFields;
    }
}
