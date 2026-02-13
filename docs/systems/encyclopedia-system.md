# Encyclopedia System

The in-game encyclopedia (Guide) provides players with a comprehensive reference for all items, monsters, buffs, and game mechanics. It is accessible from the title screen via the "Guide" dashboard button.

## Architecture

```
TitleScene → EncyclopediaScene → WndEncyclopediaEntry
               (category grid       (detail popup)
                + scrollable list)
```

### Key Files

| File | Purpose |
|------|---------|
| `encyclopedia/EncyclopediaCategory.kt` | Enum defining the 12 content categories |
| `encyclopedia/EncyclopediaEntry.kt` | Data class for individual entries |
| `encyclopedia/EncyclopediaRegistry.kt` | Singleton registry that populates all entries |
| `scenes/EncyclopediaScene.kt` | Full-screen scene with category buttons and scrollable entry list |
| `windows/WndEncyclopediaEntry.kt` | Detail popup showing entry icon, description, and stats |

### Categories (12)

| Category | Content |
|----------|---------|
| Weapons | Melee weapons by tier, crafted weapons, missile weapons |
| Armor | Armor by tier, class armor, crafted armor |
| Potions | All potion types |
| Scrolls | All scroll types |
| Wands | All wand types |
| Rings | All ring types |
| Food | Basic food, special food, farm produce, cooked dishes |
| Crafting | Materials, recipes by station, enchantments |
| Farming | Crops, tools |
| Monsters | Enemies by dungeon region and bosses |
| Buffs | Positive and negative status effects |
| Mechanics | Hunger, strength, upgrades, enchanting, leveling, etc. |

## Adding New Content to the Encyclopedia

**This is critical for maintaining a complete player guide.** Whenever you add new items, monsters, buffs, or game mechanics, you **must** also register them in `EncyclopediaRegistry.kt`.

### Registration Methods

#### Items (standard)
```kotlin
registerItem(MyCoolSword(), EncyclopediaCategory.WEAPONS, "Tier 3")
```
Uses `trueName()` for the display name and `image()` for the icon. Falls back to the category icon if `image()` returns 0.

#### Items with randomized appearances (potions, scrolls, wands, rings)
```kotlin
registerWithIcon(
    "my_potion",
    "Potion of Healing",
    EncyclopediaCategory.POTIONS,
    ItemSpriteSheet.POTION_TURQUOISE,
    "Restores health...",
    mapOf("Healing" to "20-40 HP")
)
```
These items use appearance handlers that are not initialized outside gameplay, so `name()` returns `"null potion"` and `image()` returns 0. Always use explicit names and sprite constants.

#### Monsters
```kotlin
registerMob(Rat(), EncyclopediaCategory.MONSTERS, "Sewers")
```
Extracts name, HP, EXP, defense skill, and sprite texture from the mob instance.

#### Buffs
```kotlin
registerBuff(
    "poison",
    "Poison",
    BuffIndicator.POISON,
    "Deals damage over time...",
    "Negative"
)
```
Uses `BuffIndicator` constants for icons. Never use `BuffIndicator.NONE` (-1) as it causes garbled rendering.

#### Mechanics (handwritten)
```kotlin
entries.add(EncyclopediaEntry(
    id = "mech_hunger",
    name = "Hunger",
    category = EncyclopediaCategory.MECHANICS,
    iconImage = ItemSpriteSheet.RATION,
    iconType = IconType.CUSTOM,
    description = "Your hero gets hungry over time...",
    subcategory = "Survival"
))
```

### Important Notes

- **No game state dependency**: The encyclopedia is viewed from the title screen where `Dungeon.hero` and `Dungeon.level` are null. Never depend on game state when registering entries.
- **Use `trueName()` not `name()`**: For items with identification mechanics, `name()` returns the unidentified name which may be null outside gameplay.
- **Guard against negative icon indices**: `BuffIndicator.NONE` is -1 and causes `TextureFilm.get()` to return null, displaying the entire sprite sheet as a garbled image. Always check `iconImage >= 0` before creating buff icons.
- **Lazy initialization**: `EncyclopediaRegistry` uses `lazy` initialization — entries are created on first access, not at app startup.
