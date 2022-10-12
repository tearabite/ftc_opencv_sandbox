package com.tearabite.opencvjavasandbox;

import lombok.NoArgsConstructor;
import org.opencv.core.Size;

@NoArgsConstructor
public class Settings extends SettingsBase {
    @Setting(label = "FPS")
    public static Integer FPS = 30;

    @Setting(label = "Image Size")
    public static Size imageSize = new Size(640, 480);
}
