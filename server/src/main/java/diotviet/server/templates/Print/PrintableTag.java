package diotviet.server.templates.Print;

/**
 * Printable field
 *
 * @param type
 * @param key
 */
public record PrintableTag(String type, String key, PrintableTag[] sub) {
}
