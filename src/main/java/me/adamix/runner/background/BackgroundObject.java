package me.adamix.runner.background;

import lombok.Setter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.texture.TextureManager;

public class BackgroundObject {
	private final int textureId;
	private float scale;
	private float x;
	private float y;
	private float rotation;
	private int directionX;
	private int directionY;
	private int directionScale;
	@Setter
	private boolean change = true;

	public BackgroundObject(float x, float y, int textureId, float scale, float rotation, int directionX, int directionY, int directionScale) {
		this.x = x;
		this.y = y;
		this.textureId = textureId;
		this.scale = scale;
		this.rotation = rotation;
		this.directionX = directionX;
		this.directionY = directionY;
		this.directionScale = directionScale;
	}

	public void render(RenderContext ctx) {
		JTexture texture = TextureManager.getTexture(textureId);
		if (texture == null) {
			return;
		}

		ctx.drawTextureEx(texture, x, y, rotation, scale, JColor.WHITE);
	}

	public void update(UpdateContext ctx) {
		// ToDo Change object position, rotation, maybe scale
		x += directionX * 100 * ctx.getDeltaTime();
		y += directionY * 100 * ctx.getDeltaTime();

		if (x < 0 || x > JFramework.getScreenWidth()) {
			directionX *= -1;
		}

		if (y < 0 || y > JFramework.getScreenHeight()) {
			directionY *= -1;
		}

		if (change) {
			scale += directionScale * ctx.getDeltaTime() * 0.1f;
			if (scale > 4) {
				directionScale *= -1;
			}
			if (scale < 0.3f) {
				directionScale *= -1;
			}

			rotation += 5 * ctx.getDeltaTime();
		}
	}
}
