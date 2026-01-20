package com.lucaslng.engine.renderer;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public final class Texture {

	private final int id;

	public Texture(BufferedImage image) {
		this(image, GL_CLAMP_TO_BORDER);
	}

	public Texture(BufferedImage image, int wrap) {
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);

		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		boolean hasAlpha = image.getColorModel().hasAlpha();
		glTexImage2D(GL_TEXTURE_2D, 0, hasAlpha ? GL_RGBA : GL_RGB8, image.getWidth(), image.getHeight(), 0, hasAlpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, imageToByteBuffer(image));
		glGenerateMipmap(GL_TEXTURE_2D);

		unbind();		
	}

	public final void bind(int unit) {
		assert unit >= 0 && unit <= 31;
		glActiveTexture(GL_TEXTURE0 + unit);
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public final static void unbind() {
		glBindTexture(GL_TEXTURE_2D, GL_ZERO);
	}

	public final void delete() {
		glDeleteTextures(id);
	}

	private static ByteBuffer imageToByteBuffer(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * (image.getColorModel().hasAlpha() ? 4 : 3));
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				if (image.getColorModel().hasAlpha())
					buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}

	public int id() {
		return id;
	}

}
