package com.lucaslng.engine.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class FontAtlas {

  public static final int FIRST_CHAR = 32;
  public static final int CHAR_COUNT = 95;

  public static final class Glyph {
    public final float u0, v0, u1, v1;
    public final int width, height;
    public final int advance;
    public final int bearingY;

    private Glyph(
        float u0, float v0, float u1, float v1,
        int width, int height,
        int advance,
        int bearingY) {
      this.u0 = u0;
      this.v0 = v0;
      this.u1 = u1;
      this.v1 = v1;
      this.width = width;
      this.height = height;
      this.advance = advance;
      this.bearingY = bearingY;
    }
  }

  private final BufferedImage atlas;
  private final Graphics2D g;
  private final int atlasWidth;
  private final int atlasHeight;

  private int cursorX = 0;
  private int cursorY = 0;
  private int rowHeight = 0;

  private final Map<String, Glyph[]> fonts = new HashMap<>();

  private Texture texture;

  public FontAtlas(int width, int height) {
    this.atlasWidth = width;
    this.atlasHeight = height;

    atlas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g = atlas.createGraphics();

    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
  }

  public void addFont(String family, int size) {
    String key = family + ":" + size;
    if (fonts.containsKey(key))
      return;

    Font font = new Font(family, Font.PLAIN, size);
    g.setFont(font);

    FontMetrics metrics = g.getFontMetrics();
    Glyph[] glyphs = new Glyph[CHAR_COUNT];

    for (char c = FIRST_CHAR; c < FIRST_CHAR + CHAR_COUNT; c++) {
      int w = metrics.charWidth(c);
      int h = metrics.getHeight();

      if (cursorX + w >= atlasWidth) {
        cursorX = 0;
        cursorY += rowHeight + 2;
        rowHeight = 0;
      }

      if (cursorY + h >= atlasHeight) {
        throw new IllegalStateException("Font atlas overflow");
      }

      int drawY = cursorY + metrics.getAscent();
      g.drawString(String.valueOf(c), cursorX, drawY);

      float u0 = (float) cursorX / atlasWidth;
      float v0 = (float) cursorY / atlasHeight;
      float u1 = (float) (cursorX + w) / atlasWidth;
      float v1 = (float) (cursorY + h) / atlasHeight;

      glyphs[c - FIRST_CHAR] = new Glyph(
          u0, v0, u1, v1,
          w, h,
          w,
          metrics.getAscent());

      cursorX += w + 2;
      rowHeight = Math.max(rowHeight, h);
    }

    fonts.put(key, glyphs);
  }

  public void bake() {
    if (texture != null) {
      texture.delete();
    }
    texture = new Texture(atlas);
  }

  public Texture getTexture() {
    if (texture == null)
      throw new IllegalStateException("FontAtlas not baked");
    return texture;
  }

  public Glyph getGlyph(String family, int size, char c) {
    if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT)
      return null;

    Glyph[] glyphs = fonts.get(family + ":" + size);
    if (glyphs == null)
      throw new IllegalArgumentException("Font not loaded: " + family + " " + size);

    return glyphs[c - FIRST_CHAR];
  }

  public int getLineHeight(String family, int size) {
    Glyph g = getGlyph(family, size, 'A');
    return g.height;
  }

  public void dispose() {
    g.dispose();
    if (texture != null)
      texture.delete();
  }
}
