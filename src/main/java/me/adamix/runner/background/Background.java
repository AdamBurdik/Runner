package me.adamix.runner.background;

import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.texture.TextureManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Background {
	private final List<BackgroundObject> objectList = new ArrayList<>();
	private final JTexture bgTexture1;
	private final JTexture bgTexture2;

	private float offsetX1 = 0f;
	private float offsetY1 = 0f;
	private int directionX1 = 1;
	private int directionY1 = -1;


	private float offsetX2 = 0f;
	private float offsetY2 = 0f;
	private int directionX2 = -1;
	private int directionY2 = -1;

	public Background() {
		// Create objects

		var random = new Random();

		for (int i = 0; i < 5; i++) {
			var object = new BackgroundObject(
					random.nextFloat(0, JFramework.getScreenWidth()),
					random.nextFloat(0, JFramework.getScreenHeight()),
					random.nextInt(10001, 10002 + 1),
					random.nextFloat(1f, 3f),
					random.nextFloat(0f, 360f),
					random.nextBoolean() ? 1 : -1,
					random.nextBoolean() ? 1 : -1,
					random.nextBoolean() ? 1 : -1
			);
			objectList.add(object);
		}
		var label = new BackgroundObject(
				random.nextFloat(0, JFramework.getScreenWidth()),
				random.nextFloat(0, JFramework.getScreenHeight()),
				10003,
				1f,
				0f,
				1,
				1,
				0);
		var logo = new BackgroundObject(
				random.nextFloat(0, JFramework.getScreenWidth()),
				random.nextFloat(0, JFramework.getScreenHeight()),
				10004,
				2f,
				0f,
				1,
				-1,
				0);
		label.setChange(false);
		logo.setChange(false);
		objectList.add(label);
		objectList.add(logo);


		bgTexture1 = TextureManager.getTexture(10000);
		bgTexture2 = TextureManager.getTexture(10001);
	}

	public void render(RenderContext ctx) {
		float scaleX = (float) JFramework.getScreenWidth() / bgTexture1.width() * 1.5f;
		float scaleY = (float) JFramework.getScreenHeight() / bgTexture1.height() * 1.5f;
		float scale = Math.max(scaleX, scaleY);

		float width = bgTexture1.width() * scale;
		float height = bgTexture1.height() * scale;

		ctx.drawTexturePro(bgTexture1, offsetX1 - 120, offsetY1 - 120, width * 1f, height * 1f, 0f, JColor.WHITE);
		ctx.drawTexturePro(bgTexture2, offsetX2 - 100, offsetY2 - 100, width * 1f, height * 1f, 0f, JColor.WHITE);
//		for (BackgroundObject backgroundObject : objectList) {
//			backgroundObject.render(ctx);
//		}
	}

	public void update(UpdateContext ctx) {
		offsetX1 += directionX1 * ctx.getDeltaTime() * 30;
		if (offsetX1 < -115 || offsetX1 > 80) {
			directionX1 *= -1;
		}
		offsetY1 += directionY1 * ctx.getDeltaTime() * 30;
		if (offsetY1 < -110 || offsetY1 > 95) {
			directionY1 *= -1;
		}

		offsetX2 += directionX2 * ctx.getDeltaTime() * 30;
		if (offsetX2 < -80 || offsetX2 > 120) {
			directionX2 *= -1;
		}
		offsetY2 += directionY2 * ctx.getDeltaTime() * 30;
		if (offsetY2 < -120 || offsetY2 > 80) {
			directionY2 *= -1;
		}

//		for (BackgroundObject backgroundObject : objectList) {
//			backgroundObject.update(ctx);
//		}
	}
}
