package diotviet.server.templates.Document;

import diotviet.server.views.Document.DocumentDisplayView;

public record DocumentInitResponse(
        DocumentDisplayView document,
        PrintableTag[] tags
) {
}
