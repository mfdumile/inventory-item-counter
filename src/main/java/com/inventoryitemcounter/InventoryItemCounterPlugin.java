package com.inventoryitemcounter;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
		name = "Inventory Item Counter",
		description = "Counts quantity of each item on your inventory. Allows for whitelisting and blacklisting.",
		tags = {"inventory", "counter", "items"}
)
public class InventoryItemCounterPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InventoryItemCounterOverlay overlay;

	@Inject
	private InventoryItemCounterConfig config;

	@Getter
	private List<String> whitelist = Collections.emptyList();

	@Getter
	private List<String> blacklist = Collections.emptyList();

	@Override
	protected void startUp() throws Exception
	{
		updateParsedLists();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("itemcounter"))
		{
			updateParsedLists();
		}
	}

	private void updateParsedLists()
	{
		this.whitelist = parseList(config.whitelist());
		this.blacklist = parseList(config.blacklist());
	}

	private List<String> parseList(String input)
	{
		if (input == null || input.trim().isEmpty())
		{
			return Collections.emptyList();
		}
		return Arrays.stream(input.split(","))
				.map(String::trim)
				.map(String::toLowerCase)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}

	@Provides
	InventoryItemCounterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryItemCounterConfig.class);
	}
}