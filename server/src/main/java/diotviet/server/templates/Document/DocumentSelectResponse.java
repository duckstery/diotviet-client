package diotviet.server.templates.Document;

import diotviet.server.views.Document.DocumentInitView;

import java.util.List;

public record DocumentSelectResponse(
        List<DocumentInitView> documents
) {
}
