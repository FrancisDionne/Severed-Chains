package legend.game.inventory.screens;

public enum TextColour {
  WHITE(0xf6, 0xf6, 0xf6),
  GREEN(0x18, 0x78, 0x00),
  LIME(0x58, 0xb8, 0x00),
  CYAN(0x70, 0xf8, 0xf8),
  BROWN(0x58, 0x40, 0x30),
  RED(0xd8, 0x40, 0x20),
  MIDDLE_BROWN(0x90, 0x68, 0x40),
  LIGHT_BROWN(0xa0, 0x80, 0x50),
  YELLOW(0xf8, 0xf8, 0x00),
  BLACK(0x08, 0x08, 0x08),
  GREY(0x78, 0x78, 0x78),
  PURPLE(0x60, 0x00, 0xf8),
  DARK_GREY(54, 54, 54),
  DARKER_GREY(40, 40, 40),
  CRUNCHY_TEXT_BROWN(64, 50, 43),
  CRUNCHY_TEXT_SHADOW_BROWN(142, 114, 86),
  LIGHT_GREY_WHITE(235, 235, 235),
  LIGHT_GREY(165, 165, 165),
  LIGHTER_GREY(200, 200, 200),

  FOOTER_BROWN(88, 64, 48),
  FOOTER_WHITE(246, 246, 246),
  FOOTER_GREY(186, 186, 186),
  FOOTER_RED(212, 116, 116),
  FOOTER_PINK(212, 116, 184),
  FOOTER_PURPLE(172, 116, 212),
  FOOTER_BLUE(116, 122, 212),
  FOOTER_AQUA(116, 210, 212),
  FOOTER_GREEN(116, 212, 136),
  FOOTER_LIME(166, 212, 116),
  FOOTER_YELLOW(212, 212, 116),
  FOOTER_ORANGE(212, 166, 116),

  STATS_YELLOW(245, 233, 62),
  STATS_GREEN(68, 235, 129),
  ;

  public final int r;
  public final int g;
  public final int b;

  TextColour(final int r, final int g, final int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
}
