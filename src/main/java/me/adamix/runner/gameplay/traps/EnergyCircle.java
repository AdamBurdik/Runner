package me.adamix.runner.gameplay.traps;

import lombok.Getter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.gameplay.tile.Tile;
import me.adamix.runner.texture.TextureManager;

public class EnergyCircle {
	@Getter
	private boolean remove = false;

	private int appearAnimationState = 0; // 0 = Disabled
	private boolean appearAnimation = false;

	public void startAppearAnimation() {
		this.appearAnimation = true;
		this.appearAnimationState = 0;
		JFramework.scheduleRepeating(1f / 5f, 0f, task -> {
			if (appearAnimationState >= 5) {
				task.setExpired(true);
				this.appearAnimation = false;
				return;
			}
			this.appearAnimationState += 1;
		});
	}

	public void render(RenderContext ctx, int tileX, int tileY, float verticalOffset) {
		int currentTextureId = 310;

		if (appearAnimation) {
			currentTextureId = 400 + appearAnimationState;
		}

		JTexture texture = TextureManager.getTexture(currentTextureId);
		if (texture == null) {
			return;
		}

		int width = Tile.WIDTH * TextureManager.TILE_SCALE;
		int height = Tile.HEIGHT * TextureManager.TILE_SCALE;

		float centerOffsetX = (JFramework.getScreenWidth() - width) / 2f;

		float x = tileX * width + centerOffsetX + (width - texture.width() * TextureManager.TILE_SCALE) / 2f;
		float y = JFramework.getScreenHeight() * 0.1f + (height - texture.height() * TextureManager.TILE_SCALE) / 2f;

		ctx.drawTextureEx(texture, x, y, 0f, TextureManager.TILE_SCALE, JColor.WHITE);
	}
}
