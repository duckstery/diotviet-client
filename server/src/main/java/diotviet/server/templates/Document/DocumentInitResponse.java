package diotviet.server.templates.Document;

import diotviet.server.entities.Group;
import diotviet.server.views.Document.DocumentInitView;

import java.util.List;

public record DocumentInitResponse(
        List<Group> groups,
        List<DocumentInitView> documents,
        PrintableTag[] tags
) {
}
