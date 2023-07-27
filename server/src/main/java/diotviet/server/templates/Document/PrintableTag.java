package diotviet.server.templates.Document;

/**
 * Printable field
 *
 * @param type
 * @param key
 */
public record PrintableTag(String type, String key, PrintableTag[] sub, Boolean isIterable, Boolean isParentIterable, String parentKey) {
}
