package com.lucaslng.engine.ui;

public class Text extends UIElement {

	public String text;
	public TextStyle textStyle;

	public Text(float x, float y, float width, float height, XAlignment xAlignment, YAlignment yAlignment, String text, TextStyle textStyle) {
		super(x, y, width, height, xAlignment, yAlignment);
		this.text = text;
		this.textStyle = textStyle;
	}
	
}
