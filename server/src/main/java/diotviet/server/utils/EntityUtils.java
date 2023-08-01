package diotviet.server.utils;

import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.annotations.PrintTag;
import diotviet.server.annotations.PrintTags;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Document.PrintableTag;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
     * @param group
     * @return
     */
    public PrintableTag[] getPrintableTag(String group, Class<?> entityClass) {
        // Trace and harvest
        return traceAndHarvestPrintTag(group, entityClass, new ArrayList<>(), "", null).toArray(new PrintableTag[0]);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Trace and harvest Entity @PrintTag
     *
     * @param group
     * @param entityClass
     * @param harvested
     * @param parentKey
     * @param parentTag
     * @return
     */
    private ArrayList<PrintableTag> traceAndHarvestPrintTag(String group, Class<?> entityClass, ArrayList<String> harvested, String parentKey, PrintTag parentTag) {
        // Create output
        ArrayList<PrintableTag> printableFields = new ArrayList<>();
        // Get uncapitalize basename for identification
        String base = StringUtils.uncapitalize(entityClass.getSimpleName());

        // Cache the field's class, so it won't be harvested next time
        harvested.add(entityClass.getName());

        // Iterate through each field
        for (Field field : entityClass.getDeclaredFields()) {
            // Get PrintTag Annotation
            PrintTag printTag = retrievePrintTag(field, group);

            // Continue to next field if it does not have @PrintTag
            if (Objects.isNull(printTag)) {
                continue;
            }

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
                            group,
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
                // Preprocess subfields
                PrintableTag[] preprocessedFields = preprocessSubFields(printTag, subfields, key);
                // Add new PrintableTag
                printableFields.add(
                        new PrintableTag(
                                key,                    // Translation key
                                preprocessedFields,     // Subfields
                                printTag.isIterable(),  // Tag iterable flag
                                isParentIterable,       // Tag's parent iterable flag
                                parentKey,              // Tag's parent key
                                printTag.isIdentifier() // Tag's identifier
                        )
                );
            }
        }

        return printableFields;
    }

    /**
     * Retrieve Print Tag
     *
     * @param field
     * @param group
     * @return
     */
    private PrintTag retrievePrintTag(Field field, String group) {
        // Check if @PrintTag is present
        if (field.isAnnotationPresent(PrintTag.class)) {
            // Return that @PrintTag
            return field.getAnnotation(PrintTag.class);
        }

        // Check if @PrintTags is present
        if (field.isAnnotationPresent(PrintTags.class)) {
            // Get @PrintTags
            PrintTags printTags = field.getAnnotation(PrintTags.class);
            // Then, get the @PrintTag with group
            for (PrintTag printTag : printTags.value()) {
                // Check if group is matched
                if (StringUtils.equals(group, printTag.group())) {
                    return printTag;
                }
            }

            // Return null if no @PrintTag has matched group
            return null;
        }

        // Else, return null
        return null;
    }

    private PrintableTag[] preprocessSubFields(PrintTag printTag, ArrayList<PrintableTag> subfields, String key) {
        // Create output holder
        ArrayList<PrintableTag> tags = new ArrayList<>(subfields);

        // Depends on the characteristic of PrintTag, subfields can be overwritten
        if (printTag.isIdentifier()) {
            // Clear all
            tags.clear();
            // Add plain value Tag
            tags.add(new PrintableTag("id_raw", new PrintableTag[0], false, false, key, true));
            // Add Barcode Tag
            tags.add(new PrintableTag("id_bc", new PrintableTag[0], false, false, key, true));
            // Add QR Code Tag
            tags.add(new PrintableTag("id_qr", new PrintableTag[0], false, false, key, true));
        }

        return tags.toArray(new PrintableTag[0]);
    }
}
