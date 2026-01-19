package com.lucaslng.engine.renderer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;

import org.joml.Vector4f;

public class TextRenderer {

	private final ShaderProgram shader;
	private final VertexArray vao;
	private final FloatBuffer vbo;
	private final FontAtlas fontAtlas;
	private final Texture texture;

	public TextRenderer(FontAtlas fontAtlas) {
		this.fontAtlas = fontAtlas;
		// test font atlas:
		// File outputfile = new File("image.jpg");
		// try {
		// 	ImageIO.write(fontAtlas.atlas, "jpg", outputfile);
		// } catch (IOException e) {
		// }

		texture = new Texture(fontAtlas.atlas);

		shader = new ShaderProgram("text-vertex.glsl", "text-fragment.glsl");
		shader.compileShader();
		shader.bind();
		shader.setUniform2f("atlasSize", FontAtlas.WIDTH, FontAtlas.HEIGHT);

		vao = new VertexArray();
		vao.bind();

		vbo = new FloatBuffer(GL_ARRAY_BUFFER, 4 * 6 * 1000, GL_DYNAMIC_DRAW);
		vbo.bind();
		BufferLayout layout = new BufferLayout();
		layout.add(GL_FLOAT, 2, false); // position
		layout.add(GL_FLOAT, 2, false); // uv
		vao.addBuffer(vbo, layout);

		vao.unbind();
		vbo.unbind();
	}

	public void renderText(String font, String text, float[] uiOrtho, float[] model, Vector4f color) {
		shader.bind();
		shader.setUniformMatrix4v("ortho", false, uiOrtho);
		shader.setUniform4f("uColor", color);
		shader.setUniformMatrix4v("model", false, model);

		vao.bind();
		vbo.bind();
		texture.bind(0);
		vbo.updateData(vertices(font, text));

		glDrawArrays(GL_TRIANGLES, 0, 6 * text.length());

		vao.unbind();
		vbo.unbind();
		Texture.unbind();
	}

	private float[] vertices(String font, String text) {
		int fontIndex = fontAtlas.fontIndexes.get(font);
		float scale = 0.1f;
		float x0 = 0f;
		float y0 = 0f;
		float y1 = fontAtlas.fontHeights.get(fontIndex) * scale;
		float uy0 = fontAtlas.fontRows.get(fontIndex);
		float uy1 = uy0 + fontAtlas.fontHeights.get(fontIndex);
		float[] buffer = new float[6 * 4 * text.length()]; // 6 vertices * (2 pos + 2 uv)
		int bi = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			float w = fontAtlas.fontAdvances.get(fontIndex)[c - FontAtlas.START];
			float x1 = x0 + w * scale;
			float ux0 = fontAtlas.fontStarts.get(fontIndex)[c - FontAtlas.START];
			float ux1 = ux0 + w;

			buffer[bi++] = x0;
			buffer[bi++] = y1;
			buffer[bi++] = ux0;
			buffer[bi++] = uy1;

			buffer[bi++] = x0;
			buffer[bi++] = y0;
			buffer[bi++] = ux0;
			buffer[bi++] = uy0;

			buffer[bi++] = x1;
			buffer[bi++] = y0;
			buffer[bi++] = ux1;
			buffer[bi++] = uy0;

			buffer[bi++] = x0;
			buffer[bi++] = y1;
			buffer[bi++] = ux0;
			buffer[bi++] = uy1;

			buffer[bi++] = x1;
			buffer[bi++] = y0;
			buffer[bi++] = ux1;
			buffer[bi++] = uy0;

			buffer[bi++] = x1;
			buffer[bi++] = y1;
			buffer[bi++] = ux1;
			buffer[bi++] = uy1;

			x0 = x1;
		}
		return buffer;
	}
}
