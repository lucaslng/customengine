package com.lucaslng.engine.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FontAtlas {
	public static final char START = ' ', END = '~';
	private static final char[] chars = new char[END - START + 1];
	static {
		for (char c = START; c <= END; c++) {
			chars[c - START] = c;
		}
	}
	public static final int WIDTH = 6000, HEIGHT = 1000;
	private static final int CHAR_PADDING = 4;

	private final Graphics2D g;
	protected final BufferedImage atlas;
	protected final ArrayList<int[]> fontStarts;
	protected final ArrayList<int[]> fontAdvances;
	protected final ArrayList<Integer> fontRows;
	protected final ArrayList<Integer> fontHeights;
	protected final HashMap<String, Integer> fontIndexes;
	private int yy;

	public FontAtlas() {
		atlas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		g = atlas.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.RED);
		fontStarts = new ArrayList<>();
		fontRows = new ArrayList<>();
		fontIndexes = new HashMap<>();
		fontAdvances = new ArrayList<>();
		fontHeights = new ArrayList<>();
		yy = 0;
	}

	public void addFontFromTTF(String fileName, int style) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("assets/fonts/" + fileName + ".ttf")).deriveFont(style, 50);
			addFont(font);
		} catch (Exception e) {
			throw new RuntimeException("Failed to open font file.");
		}
	}

	public void addFont(String family, int style) {
		Font font = new Font(family, style, 50);
		addFont(font);
		Integer index = fontIndexes.get(font.getFamily());
		if (index != null && !fontIndexes.containsKey(family)) {
			fontIndexes.put(family, index);
		}
	}

    private void addFont(Font font) {
        assert font.getSize() == 50;
        if (fontIndexes.containsKey(font.getFamily()))
            return;

        fontIndexes.put(font.getFamily(), fontStarts.size());
        fontRows.add(yy);

        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics();
        fontHeights.add(fontMetrics.getHeight());

        int x = 0, y = fontMetrics.getAscent() + fontMetrics.getLeading() + yy;

        // draw characters with padding
        int[] advances = new int[chars.length];
        int[] starts = new int[chars.length];
        starts[0] = 0;
        
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            g.drawString(String.valueOf(c), x, y);
            advances[i] = fontMetrics.charWidth(c);
            if (i > 0) {
                starts[i] = starts[i - 1] + fontMetrics.charWidth(chars[i - 1]) + CHAR_PADDING;
            }
            x += advances[i] + CHAR_PADDING; 	// add padding after each character
        }
        
        fontStarts.add(starts);
        fontAdvances.add(advances);

        yy += fontMetrics.getHeight() * 2;
    }

	public void dispose() {
		g.dispose();
	}
}