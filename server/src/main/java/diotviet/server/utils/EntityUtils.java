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
import java.util.Objects;

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
            entityHeaders.add(new EntityHeader(fieldBase, OtherUtils.get(basename, fieldBase), fieldBase, !field.isAnnotationPresent(InitHide.class)));
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
        return traceAndHarvestPrintTag(entityClass, new ArrayList<>(), "", null).toArray(new PrintableTag[0]);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Trace and harvest Entity @PrintTag
     *
     * @param entityClass
     * @param harvested
     * @param parentKey
     * @param parentTag
     * @return
     */
    private ArrayList<PrintableTag> traceAndHarvestPrintTag(Class<?> entityClass, ArrayList<String> harvested, String parentKey, PrintTag parentTag) {
        // Create output
        ArrayList<PrintableTag> printableFields = new ArrayList<>();
        // Get uncapitalize basename for identification
        String base = StringUtils.uncapitalize(entityClass.getSimpleName());

        // Cache the field's class, so it won't be harvested next time
        harvested.add(entityClass.getName());

        // Iterate through each field
        for (Field field : entityClass.getDeclaredFields()) {
            // Continue to next field if it does not have @PrintTag
            if (!field.isAnnotationPresent(PrintTag.class)) {
                continue;
            }

            // Get PrintTag Annotation
            PrintTag printTag = field.getAnnotation(PrintTag.class);
            // Get PrintTag type
            String type = (ArrayUtils.isNotEmpty(printTag.component()) ? "NestedMenuItem" : "MenuItem").toLowerCase();
            // Get translation key
            String key = base + "_" + (StringUtils.isEmpty(printTag.value()) ? field.getName() : printTag.value().isEmpty());

            // Create subfield
            ArrayList<PrintableTag> subfields = new ArrayList<>();
            // Check if this is a nested Print Tag
            if (ArrayUtils.isNotEmpty(printTag.component())) {
                // Make sure does not re-harvest harvested field to avoid infinite references
                if (!harvested.contains(printTag.component()[0].getName())) {
                    // Harvest subfields
                    subfields = traceAndHarvestPrintTag(
                            printTag.component()[0],                        // Component (An Entity)
                            harvested,                                      // Harvested Entity
                            printTag.isIterable() ? key : parentKey,        // If self is iterable, it'll become a new parent for it child
                            printTag.isIterable() ? printTag : parentTag    // Same reason as above
                    );
                }
            }

            // Check if subfields is force merged into upper class
            if (printTag.merge()) {
                // Add all
                printableFields.addAll(subfields);
                // Clear subfields
                subfields.clear();
            } else {
                // Retrieve parent's isIterable flag
                boolean isParentIterable = Objects.nonNull(parentTag) && parentTag.isIterable();
                // Add new PrintableTag
                printableFields.add(
                        new PrintableTag(
                                type,                                       // Tag type
                                key,                                        // Translation key
                                subfields.toArray(new PrintableTag[0]),     // Subfields
                                printTag.isIterable(),                      // Tag iterable flag
                                isParentIterable,                           // Tag's parent iterable flag
                                parentKey                                   // Tag's parent key
                        )
                );
            }
        }

        return printableFields;
    }
}
