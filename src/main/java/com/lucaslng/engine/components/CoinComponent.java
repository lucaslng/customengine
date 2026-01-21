package com.lucaslng.engine.components;

public class CoinComponent {
	public boolean collected;
	public final int flag;

	public CoinComponent() {
		this(0);
	}

	public CoinComponent(int flag) {
		this.flag = flag;
	}
}
