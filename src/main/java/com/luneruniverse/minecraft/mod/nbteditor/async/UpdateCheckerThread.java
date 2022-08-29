package com.luneruniverse.minecraft.mod.nbteditor.async;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.luneruniverse.minecraft.mod.nbteditor.NBTEditor;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class UpdateCheckerThread extends Thread {
	
	private static final String VERSION = FabricLoader.getInstance().getModContainer("nbteditor").orElseThrow().getMetadata().getVersion().getFriendlyString();
	private static final URL URL;
	static {
		URL url;
		try {
			url = new URL("https://api.modrinth.com/v2/project/5Osk0m1G/version");
		} catch (MalformedURLException e) {
			NBTEditor.LOGGER.error("Unable to check for updates!", e);
			url = null;
		}
		URL = url;
	}
	
	public UpdateCheckerThread() {
		super("NBTEditor/Async/UpdateChecker");
		setDaemon(true);
	}
	
	@Override
	public void run() {
		if (URL == null)
			return;
		
		try {
			String thisGameVersion = SharedConstants.getGameVersion().getName();
			HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
			JsonArray versions = new Gson().fromJson(new String(conn.getInputStream().readAllBytes(), "UTF-8"), JsonArray.class);
			String highestVersion = null;
			for (JsonElement version : versions) {
				if (!version.getAsJsonObject().get("version_type").getAsString().equals("release"))
					continue;
				for (JsonElement gameVersion : version.getAsJsonObject().get("game_versions").getAsJsonArray()) {
					if (!thisGameVersion.equals(gameVersion.getAsString()))
						continue;
					String number = version.getAsJsonObject().get("version_number").getAsString();
					if (highestVersion == null || versionCompare(highestVersion, number) < 0)
						highestVersion = number;
				}
			}
			if (highestVersion == null)
				NBTEditor.LOGGER.warn("Unable to check for updates! Is the game version invalid?");
			else if (versionCompare(highestVersion, VERSION) > 0) {
				NBTEditor.LOGGER.warn("NBT Editor is outdated!");
				MainUtil.client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
						Text.translatable("nbteditor.outdated.title"),
						Text.translatable("nbteditor.outdated.desc")));
			} else
				NBTEditor.LOGGER.info("NBT Editor is fully updated!");
		} catch (Exception e) {
			NBTEditor.LOGGER.error("Unable to check for updates!", e);
		}
	}
	
	private static int versionCompare(String a, String b) {
		int[] aInts = getVersionInts(a);
		int[] bInts = getVersionInts(b);
		
		for (int i = 0; i < Math.min(aInts.length, bInts.length); i++) {
			int output = Integer.compare(aInts[i], bInts[i]);
			if (output != 0)
				return output;
		}
		
		return Integer.compare(aInts.length, bInts.length);
	}
	private static int[] getVersionInts(String version) {
		return Stream.of(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
	}
	
}
