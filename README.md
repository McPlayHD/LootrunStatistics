# LootrunStatistics

A mod for Wynncraft that records your lootrun statistics.

## Installation

[Download](https://github.com/McPlayHD/LootrunStatistics/releases/download/v1.1.0/lootrunstatistics-1.1.0.jar) the
latest version of the mod.

You can find specific versions of the mod in the [Releases](https://github.com/McPlayHD/LootrunStatistics/releases) page
to the right.

Put the compiled `.jar` file into your `%appdata%/.minecraft/mods/` folder.

In order for all features to work, you have to install [Wynntils](https://wynntils.com/) by also placing it into the `%appdata%/.minecraft/mods/` folder.

**Not compatible with the Acivia chestcountmod!**

**I also never managed to make it register chest contents with WIM2 so better not use it.**

## Commands:
* `/lrs` - main command (currently opens the settings)
* `/lrs settings` - opens the settings
* `/lrs history` - see your mythic find history
* `/lrs rarities` - see a distribution of all your item finds
* `/dry` - see your current dry count and items dry
* `/lastmythic` - see your last mythic find

## TODO

- [ ] display mythics found in a chest
- [ ] loot cooldown above chests

## DONE

- [x] count total chests
- [x] count dry chests / emeralds / items
- [x] save statistics about mythic finds
- [x] import stats from Chestcountmod and Wynntils
- [x] created `/dry` and `/lastmythic` commands
- [x] show chest count in inventory
- [x] record and display chest data
- [x] make chest level calculation better
- [x] add settings
- [x] display possible mythics
- [x] update notification
- [x] auto update Wynncraft item database
- [x] store actual mythic found in mythic history
- [x] mythic history accessible with `/lrs history`
- [x] item tier distribution accessible with `/lrs rarities`

## Credits
- `LootrunNotes` and displaying items from https://github.com/Wynntils/Wynntils
- idea and some events / functions from https://github.com/albarv340/chestcountmod
