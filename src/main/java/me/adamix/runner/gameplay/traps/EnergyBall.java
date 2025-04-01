package me.adamix.runner.gameplay.traps;

import lombok.Getter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.gameplay.GameplayScene;
import me.adamix.runner.gameplay.board.Board;
import me.adamix.runner.gameplay.tile.Tile;
import me.adamix.runner.texture.TextureManager;

import java.util.Objects;

public class EnergyBall {
	@Getter
	private int tileX;
	@Getter
	private boolean remove = false;

	private int coreAnimationState = 0;
	private int shellAnimationState = 0;
	private boolean renderShell = false;

	private int appearAnimationState = 0; // 0 = Disabled
	private boolean appearAnimation = false;

	@Getter
	private boolean renderLaser = false;
	private int laserAnimationState = 0;
	private int laserDuration = 0;

	private int shootCount = 0;

	public EnergyBall(int tileX, int shootCount) {
		this.tileX = tileX;
		this.shootCount = shootCount;

		JFramework.scheduleRepeating(0.2f, 0f, task -> {
			coreAnimationState++;
			shellAnimationState++;

			if (coreAnimationState >= 3) {
				coreAnimationState = 0;
			}

			if (shellAnimationState >= 20) {
				shellAnimationState = 0;
			}

			if (shellAnimationState == 7) {
				if (this.shootCount > 0) {
					shoot();
					this.shootCount--;
				} else {
					task.setExpired(true);
					startDisappearAnimation();
				}
			}

		});
	}

	public void startAppearAnimation() {
		this.appearAnimation = true;
		JFramework.scheduleRepeating(0.15f, 0f, task -> {
			if (appearAnimationState >= 3) {
				task.setExpired(true);
				this.appearAnimationState = 0;
				this.appearAnimation = false;
				this.renderShell = true;
				return;
			}
			this.appearAnimationState += 1;
		});
	}

	public void startDisappearAnimation() {
		this.appearAnimation = true;
		this.renderShell = false;
		this.appearAnimationState = 3;
		this.renderLaser = false;
		JFramework.scheduleRepeating(0.15f, 0f, task -> {
			if (appearAnimationState < 1) {
				task.setExpired(true);
				this.appearAnimation = false;
				this.remove = true;
				return;
			}
			this.appearAnimationState -= 1;
		});
	}

	public void startLaserAnimation() {
		this.renderLaser = true;
		this.laserDuration = 10;
		JFramework.scheduleRepeating(0.2f, 0f, task -> {
			if (this.remove) {
				task.setExpired(true);
				return;
			}
			GameplayScene.getPlayer().checkForDead();
			laserAnimationState++;

			if (laserAnimationState > 1) {
				laserAnimationState = 0;
			}
			this.laserDuration -= 1;
			if (laserDuration < 0) {
				task.setExpired(true);
				renderLaser = false;
			}
		});
	}

	public void update() {

	}

	public void render(RenderContext ctx, float verticalOffset) {
		int currentTextureId = 310 + coreAnimationState;

		if (appearAnimation) {
			currentTextureId = 300 + appearAnimationState;
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

		if (renderShell) {
			JTexture shellTexture = TextureManager.getTexture(320 + shellAnimationState);
			if (shellTexture == null) {
				return;
			}

			ctx.drawTextureEx(shellTexture, x, y, 0f, TextureManager.TILE_SCALE, JColor.WHITE);
		}

		if (renderLaser) {
			JTexture laserTexture = TextureManager.getTexture(350 + laserAnimationState);
			ctx.drawTextureEx(laserTexture, x, y, 0f, TextureManager.TILE_SCALE, JColor.WHITE);
		}
	}

	public void shoot() {
		startLaserAnimation();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		EnergyBall that = (EnergyBall) object;
		return tileX == that.tileX;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(tileX);
	}
}
