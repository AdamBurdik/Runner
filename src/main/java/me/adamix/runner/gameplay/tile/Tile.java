package me.adamix.runner.gameplay.tile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.gameplay.GameplayScene;
import me.adamix.runner.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
@Data
public class Tile {
	public static final int WIDTH = 18;
	public static final int HEIGHT = 16;

	private int textureId;
	private boolean remove = false;

	private boolean isWalkableOn = false;  // If player can walk on it
	private boolean isDeadly = false;      // If player dies when walked on

	private int appearAnimationState = 0; // 0 = Disabled
	private boolean appearAnimation = false;

	@Getter
	private boolean transparent = false;

	public Tile(int textureId) {
		this.textureId = textureId;
	}

	public void startAppearAnimation(float randomDelay) {
		this.appearAnimation = true;
		this.transparent = true;
		JFramework.scheduleRepeating(1f / 12f, randomDelay, task -> {
			if (appearAnimationState >= 8) {
				task.setExpired(true);
				this.appearAnimationState = 0;
				this.appearAnimation = false;
				this.isWalkableOn = true;
				this.transparent = false;
				return;
			}
			this.appearAnimationState += 1;
		});
	}

	public void startDisappearAnimation(float randomDelay) {
		this.appearAnimation = true;
		this.appearAnimationState = 8;
		this.transparent = true;
		this.isWalkableOn = false;
		JFramework.scheduleRepeating(1f / 7.5f, randomDelay, task -> {
			if (appearAnimationState < 1) {
				this.remove = true;
				this.appearAnimation = false;
				task.setExpired(true);
				GameplayScene.getPlayer().checkForDead();
			}
			if (appearAnimationState == 3) {
				this.isDeadly = true;
				GameplayScene.getPlayer().checkForDead();
			}
			this.appearAnimationState -= 1;
		});
	}

	public void update(@NotNull UpdateContext ctx) {

	}

	public void render(@NotNull RenderContext ctx, int tileX, int tileY, float verticalOffset) {
		int currentTextureId = textureId;

		if (appearAnimation) {
			if (appearAnimationState < 7) {
				currentTextureId = 40 + appearAnimationState;
			} else {
				if (appearAnimationState <= 8) {
					currentTextureId = textureId + appearAnimationState - 6;
				}
			}
		}

		JTexture texture = TextureManager.getTexture(currentTextureId);
		if (texture == null) {
			return;
		}

		int width = Tile.WIDTH * TextureManager.TILE_SCALE;
		int height = Tile.HEIGHT * TextureManager.TILE_SCALE;

		float centerOffsetX = (JFramework.getScreenWidth() - width) / 2f;
		float centerOffsetY = (JFramework.getScreenHeight() - height) / 4f;

		float x = tileX * width + centerOffsetX;
		float y = tileY * height + centerOffsetY + verticalOffset;

		ctx.drawTextureEx(
				texture,
				x,
				y,
				0f,
				TextureManager.TILE_SCALE,
				JColor.WHITE
		);

		if (y > JFramework.getScreenHeight() * 2) {
			this.remove = true;
		}
	}

	public void renderBottom(@NotNull RenderContext ctx, int tileX, int tileY, float verticalOffset) {
		int currentTextureId = 50; // 40 = tile bottom texture

		if (appearAnimation) {
			currentTextureId = 51 + appearAnimationState;
		}

		JTexture texture = TextureManager.getTexture(currentTextureId);
		if (texture == null) {
			return;
		}

		int width = Tile.WIDTH * TextureManager.TILE_SCALE;
		int height = Tile.HEIGHT * TextureManager.TILE_SCALE;

		float centerOffsetX = (JFramework.getScreenWidth() - width) / 2f;
		float centerOffsetY = (JFramework.getScreenHeight() - height) / 4f;

		float x = tileX * width + centerOffsetX;
		float y = (tileY + 1) * height + centerOffsetY + verticalOffset;

		ctx.drawTextureEx(
				texture,
				x,
				y,
				0f,
				TextureManager.TILE_SCALE,
				JColor.WHITE
		);
	}
}
