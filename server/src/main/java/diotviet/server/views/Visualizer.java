package diotviet.server.views;

import org.springframework.beans.factory.annotation.Value;

public interface Visualizer {
    /**
     * Get image's id
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).getFirstOrUseDefault(target.images, \"getId\", null)}")
    Long getImgId();

    /**
     * Get image's src
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).getFirstOrUseDefault(target.images, \"getSrc\", \"https://i.ibb.co/GspmYFm/33d42f31ce7f.jpg\")}")
    String getSrc();
}
