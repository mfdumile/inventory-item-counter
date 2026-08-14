package com.inventoryitemcounter;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	@Getter
	private Map<String, String> mergeMap = Collections.emptyMap();

	@Override
	protected void startUp()
	{
		updateParsedLists();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void onConfigChanged(ConfigChanged event)
	{
		if (!"inventoryitemcounter".equals(event.getGroup()))
		{
			return;
		}

		updateParsedLists();
	}

	private void updateParsedLists()
	{
		this.whitelist = parseList(config.whitelist());
		this.blacklist = parseList(config.blacklist());
		this.mergeMap = parseMergeMap(config.mergelist());

		if (overlay != null)
		{
			overlay.invalidateCache();
		}
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

	private Map<String, String> parseMergeMap(String input)
	{
		if (input == null || input.trim().isEmpty())
		{
			return Collections.emptyMap();
		}

		Map<String, String> map = new HashMap<>();
		Matcher matcher = Pattern.compile("\\[(.*?)]").matcher(input);

		while (matcher.find())
		{
			String groupContent = matcher.group(1);
			String[] items = groupContent.split(",");
			if (items.length == 0)
			{
				continue;
			}

			String canonicalKey = items[0].trim().toLowerCase();
			if (canonicalKey.isEmpty())
			{
				continue;
			}

			for (String item : items)
			{
				String name = item.trim().toLowerCase();
				if (!name.isEmpty())
				{
					map.put(name, canonicalKey);
				}
			}
		}

		return map;
	}

	@Provides
	@SuppressWarnings("unused")
	InventoryItemCounterConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryItemCounterConfig.class);
	}
}