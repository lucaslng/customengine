package com.lucaslng.engine.systems;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;

import com.lucaslng.engine.components.TextComponent;
import com.lucaslng.engine.ecs.EntityManager;
import com.lucaslng.engine.renderer.*;

public class TextRenderSystem {

    private static final int FLOATS_PER_VERTEX = 4;
    private static final int VERTICES_PER_CHAR = 6;

    private final FontAtlas fontAtlas;
    private final ShaderProgram shader;

    private final VertexArray vao;
    private final FloatBuffer vbo;

    public TextRenderSystem(FontAtlas atlas, ShaderProgram shader) {
        this.fontAtlas = atlas;
        this.shader = shader;

        vao = new VertexArray();
        vao.bind();

        vbo = new FloatBuffer(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        BufferLayout layout = new BufferLayout();
        layout.add(GL_FLOAT, 2, false); // position
        layout.add(GL_FLOAT, 2, false); // uv

        vao.addBuffer(vbo, layout);
        vao.unbind();
    }

    public void render(EntityManager em, int screenWidth, int screenHeight) {
        shader.bind();

        shader.setMat4("uProjection",
                Ortho.projection(0, screenWidth, screenHeight, 0));

        fontAtlas.getTexture().bind(0);
        shader.setInt("uFontAtlas", 0);

        vao.bind();

        for (int entity : em.getEntitiesWith(TextComponent.class)) {
            TextComponent text = em.get(entity, TextComponent.class);

            FloatBuffer buffer = buildTextBuffer(text);
            vbo.setData(buffer);

            shader.setVec4("uColor", text.color);

            glDrawArrays(GL_TRIANGLES, 0, buffer.limit() / FLOATS_PER_VERTEX);

            text.dirty = false;
        }

        vao.unbind();
        shader.unbind();
    }

    private FloatBuffer buildTextBuffer(TextComponent text) {
        int len = text.text.length();
        FloatBuffer buffer = FloatBuffer.allocate(
                len * VERTICES_PER_CHAR * FLOATS_PER_VERTEX
        );

        float cursorX = text.x;
        float baselineY = text.y;

        for (int i = 0; i < len; i++) {
            char c = text.text.charAt(i);
            FontAtlas.Glyph g =
                    fontAtlas.getGlyph(text.fontFamily, text.fontSize, c);

            if (g == null) continue;

            float x0 = cursorX;
            float y0 = baselineY;
            float x1 = cursorX + g.width;
            float y1 = baselineY + g.height;

            // triangle 1
            put(buffer, x0, y0, g.u0, g.v0);
            put(buffer, x1, y0, g.u1, g.v0);
            put(buffer, x1, y1, g.u1, g.v1);

            // triangle 2
            put(buffer, x1, y1, g.u1, g.v1);
            put(buffer, x0, y1, g.u0, g.v1);
            put(buffer, x0, y0, g.u0, g.v0);

            cursorX += g.advance;
        }

        buffer.flip();
        return buffer;
    }

    private static void put(FloatBuffer b, float x, float y, float u, float v) {
        b.put(x).put(y).put(u).put(v);
    }

    public void dispose() {
        vbo.delete();
        vao.delete();
    }
}
