# Inventory Item Counter

**Inventory Item Counter** is a RuneLite plugin designed to visually overlay sequential counts onto your inventory items. It counts each item type independently, making it easy to manage your inventory at a glance (e.g., *Bucket 1, Hammer 1, Bucket 2, Bucket 3, Bucket 4*).

---

## Features

* **Independent Item Counting:** Identical items are numbered sequentially in the order they appear in your inventory (1, 2, 3...). Different item types maintain their own separate counts starting from 1.
* **Whitelist:** Specify exact items you want to overlay counts on.
* **Blacklist:** Exclude specific items so they never display a counter.

---

## Configuration

You can customize the plugin's behavior in the RuneLite settings panel:

| Option | Description | Example |
| :--- | :--- | :--- |
| **Whitelist** | Comma-separated list of items to count. If left blank, all items are counted by default. | `bucket, hammer` |
| **Blacklist** | Comma-separated list of items that should **never** display a count. | `coins, shark` |
