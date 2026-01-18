package com.lucaslng.engine.ui;

import java.awt.Color;

import com.lucaslng.engine.renderer.Renderer;

class Text implements Widget {
    private final String text;
    private final TextStyle style;
    
    public Text(String text) {
        this(text, new TextStyle("Arial", 8, Color.BLACK));
    }
    
    public Text(String text, TextStyle style) {
        this.text = text;
        this.style = style;
    }
    
    @Override
    public RenderObject createRenderObject() {
        return new RenderText(this);
    }
    
    static class RenderText extends RenderObject {
        private final Text widget;
        
        RenderText(Text widget) {
            this.widget = widget;
        }
        
        @Override
        protected void performLayout(double maxWidth, double maxHeight) {
            this.width = Math.min(widget.text.length() * widget.style.fontSize * 0.6, maxWidth);
            this.height = widget.style.fontSize;
        }
        
        @Override
        public void paint(Renderer renderer) {
            // renderer.drawText(x, y, widget.text, widget.style);
        }
    }
}