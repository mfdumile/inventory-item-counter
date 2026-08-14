package com.inventoryitemcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("inventoryitemcounter")
public interface InventoryItemCounterConfig extends Config
{
	@ConfigItem(
			keyName = "whitelist",
			name = "Whitelist",
			description = "Item names that will be counter separated by commas (eg: Raw salmon, Hammer). Empty means everything is counted.",
			position = 1
	)
	default String whitelist()
	{
		return "";
	}

	@ConfigItem(
			keyName = "blacklist",
			name = "Blacklist",
			description = "Item names that will be ignored separated by commas (eg: Raw salmon, Hammer).",
			position = 2
	)
	default String blacklist()
	{
		return "";
	}

	@ConfigItem(
			keyName = "mergelist",
			name = "Merge list",
			description = "Items that will be counted together built in arrays separated by commas (eg: [Raw salmon, Salmon], [Bucket, Bucket of sand]).",
			position = 3
	)
	default String mergelist()
	{
		return "";
	}
}