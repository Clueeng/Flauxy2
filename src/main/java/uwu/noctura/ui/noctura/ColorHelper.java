package uwu.noctura.ui.noctura;

import uwu.noctura.utils.render.ColorUtils;

import java.awt.*;

public interface ColorHelper {
    int stringColor = -1;

    int defaultWidth = 130;
    int defaultHeight = 200;


    int mainColor = new Color(26, 26, 26, 255).getRGB();
    int darkerMainColor = new Color(21, 21, 21, 255).getRGB();
    int nocturaGalaxy = ColorUtils.setAlpha(new Color(141, 68, 173), 255).getRGB();
    int nocturaGalaxyDark = ColorUtils.setAlpha(new Color(111, 54, 136), 255).getRGB();

    int outlineWidth = 1;
    int categoryNameHeight = 16;

    int moduleHeight = 16;

    boolean hoveredColor = false;
}
