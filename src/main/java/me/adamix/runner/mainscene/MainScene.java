package me.adamix.runner.mainscene;

import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.scene.JScene;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

public class MainScene implements JScene {
	@Override
	public void init() {

	}

	@Override
	public void create() {
		TextureManager.addTexture(50001, "assets/ui/logo.png");
	}

	@Override
	public void update(@NotNull UpdateContext ctx) {

	}

	@Override
	public void render(@NotNull RenderContext ctx) {
		JTexture tex = TextureManager.getTexture(50001);
		float texWidth = tex.width() * TextureManager.TILE_SCALE;
		float texHeight = tex.height() * TextureManager.TILE_SCALE;

		ctx.drawTextureEx(
				tex,
				(JFramework.getScreenWidth() - texWidth) / 2f,
				(JFramework.getScreenHeight() - texHeight) / 4f,
				0f,
				TextureManager.TILE_SCALE,
				JColor.WHITE
		);
	}
}
