package diotviet.server.views;

import org.springframework.beans.factory.annotation.Value;

public interface Visualizer {
    /**
     * Get image's id
     *
     * @return
     */
    @Value("#{target.images[0].id}")
    Long getImgId();

    /**
     * Get image's src
     *
     * @return
     */
    @Value("#{target.images[0].src}")
    String getSrc();
}
