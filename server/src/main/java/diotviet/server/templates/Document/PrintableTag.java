package diotviet.server.templates.Document;

/**
 * Printable field
 *
 * @param type
 * @param key
 */
public record PrintableTag(
        String key,
        PrintableTag[] sub,
        Boolean isIterable,
        Boolean isParentIterable,
        String parentKey,
        Boolean isIdentifier
) {
}
