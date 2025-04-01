package me.adamix.runner.texture;

import me.adamix.jframework.JFramework;
import me.adamix.jframework.texture.JTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
	public static int TILE_SCALE = 5;

	private static Map<Integer, JTexture> textureMap = new HashMap<>();

	public static void addTexture(int id, @NotNull String filename) {
		textureMap.put(id, JFramework.loadTexture(filename));
	}

	public static @Nullable JTexture getTexture(int id) {
		return textureMap.get(id);
	}

	public static void dispose() {
		for (JTexture texture : textureMap.values()) {
			JFramework.unloadTexture(texture);
		}
	}
}
