package dev.diamondclass.greattab.modules.frameworks.animations;

import dev.diamondclass.greattab.utils.CC;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Animation {

    private final String name;
    private final List<String> frames;
    private final int interval;

    private int frameIndex = 0;
    private long lastUpdate = 0;

    public Animation(String name, List<String> frames, int interval) {
        this.name = name;
        this.frames = frames.stream().map(CC::translate).collect(Collectors.toList());
        this.interval = interval;
    }

    public String getCurrentFrame() {
        if (System.currentTimeMillis() - lastUpdate >= interval * 50L) {
            frameIndex++;
            if (frameIndex >= frames.size()) {
                frameIndex = 0;
            }
            lastUpdate = System.currentTimeMillis();
        }
        return frames.get(frameIndex);
    }
}
