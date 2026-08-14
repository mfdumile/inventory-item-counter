package com.inventoryitemcounter;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class InventoryItemCounterOverlay extends WidgetItemOverlay
{
    private final Client client;
    private final InventoryItemCounterPlugin plugin;
    private final ItemManager itemManager;

    private int lastTick = -1;
    private final int[] slotCounts = new int[28];

    @Inject
    public InventoryItemCounterOverlay(Client client, InventoryItemCounterPlugin plugin, ItemManager itemManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        showOnInventory();
    }

    public void invalidateCache()
    {
        this.lastTick = -1;
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null || widgetItem.getWidget() == null)
        {
            return;
        }

        int targetSlot = widgetItem.getWidget().getIndex();
        if (targetSlot < 0 || targetSlot >= slotCounts.length)
        {
            return;
        }

        int currentTick = client.getTickCount();
        if (currentTick != lastTick)
        {
            calculateSlotCounts(inventory);
            lastTick = currentTick;
        }

        int count = slotCounts[targetSlot];
        if (count <= 0)
        {
            return;
        }

        Rectangle bounds = widgetItem.getCanvasBounds();
        String text = String.valueOf(count);

        FontMetrics fm = graphics.getFontMetrics();
        int x = (int) bounds.getMaxX() - fm.stringWidth(text) - 2;
        int y = (int) bounds.getMaxY() - 2;

        graphics.setFont(FontManager.getRunescapeBoldFont());
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(Color.YELLOW);
        graphics.drawString(text, x, y);
    }

    private void calculateSlotCounts(ItemContainer inventory)
    {
        Arrays.fill(slotCounts, 0);
        Item[] items = inventory.getItems();
        Map<String, Integer> runningCounts = new HashMap<>();

        for (int i = 0; i < items.length && i < slotCounts.length; i++)
        {
            Item item = items[i];
            if (item == null || item.getId() <= -1)
            {
                continue;
            }

            ItemComposition comp = itemManager.getItemComposition(item.getId());
            String itemName = comp.getName().toLowerCase();

            if (plugin.getBlacklist().contains(itemName))
            {
                continue;
            }

            if (!plugin.getWhitelist().isEmpty() && !plugin.getWhitelist().contains(itemName))
            {
                continue;
            }

            String countKey = plugin.getMergeMap().getOrDefault(itemName, itemName);

            int currentCount = runningCounts.getOrDefault(countKey, 0) + 1;
            runningCounts.put(countKey, currentCount);
            slotCounts[i] = currentCount;
        }
    }
}