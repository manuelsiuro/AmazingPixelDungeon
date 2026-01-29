# Class Hierarchy Diagrams

This document contains Mermaid diagrams showing the inheritance hierarchies of core classes.

## Visual Hierarchy

The scene graph visual hierarchy from base element to renderable objects:

```mermaid
classDiagram
    Gizmo <|-- Visual
    Visual <|-- Group
    Group <|-- Scene
    Scene <|-- PixelScene
    PixelScene <|-- TitleScene
    PixelScene <|-- StartScene
    PixelScene <|-- GameScene
    PixelScene <|-- InterlevelScene
    PixelScene <|-- SurfaceScene
    PixelScene <|-- AmuletScene
    PixelScene <|-- RankingsScene
    PixelScene <|-- BadgesScene
    PixelScene <|-- AboutScene

    class Gizmo {
        +exists: Boolean
        +active: Boolean
        +visible: Boolean
        +update()
        +destroy()
    }

    class Visual {
        +x: Float
        +y: Float
        +width: Float
        +height: Float
        +scale: PointF
        +draw()
    }

    class Group {
        +members: ArrayList
        +add(gizmo)
        +remove(gizmo)
    }

    class Scene {
        +create()
        +pause()
        +resume()
        +destroy()
    }
```

## Sprite Hierarchy

Character and item sprites:

```mermaid
classDiagram
    Visual <|-- Image
    Image <|-- MovieClip
    MovieClip <|-- CharSprite
    MovieClip <|-- ItemSprite
    MovieClip <|-- MissileSprite
    MovieClip <|-- PlantSprite
    CharSprite <|-- HeroSprite
    CharSprite <|-- MobSprite
    MobSprite <|-- RatSprite
    MobSprite <|-- GooSprite
    MobSprite <|-- TenguSprite

    class Image {
        +texture: SmartTexture
        +frame: RectF
        +frame(rect)
    }

    class MovieClip {
        +curAnim: Animation
        +curFrame: Int
        +play(anim)
        +paused: Boolean
    }

    class CharSprite {
        +ch: Char
        +idle: Animation
        +run: Animation
        +attack: Animation
        +die: Animation
        +link(char)
        +attack(cell)
        +move(from, to)
    }

    class ItemSprite {
        +view(item)
        +glowing: Glowing
    }
```

## Actor Hierarchy

Turn-based entity system:

```mermaid
classDiagram
    Actor <|-- Char
    Actor <|-- Blob
    Actor <|-- Buff

    Char <|-- Hero
    Char <|-- Mob
    Char <|-- NPC

    Mob <|-- Rat
    Mob <|-- Gnoll
    Mob <|-- Goo
    Mob <|-- Tengu
    Mob <|-- DM300
    Mob <|-- King
    Mob <|-- Yog

    NPC <|-- Shopkeeper
    NPC <|-- Ghost
    NPC <|-- Wandmaker
    NPC <|-- Blacksmith

    Buff <|-- FlavourBuff
    Buff <|-- Hunger
    FlavourBuff <|-- Burning
    FlavourBuff <|-- Poison
    FlavourBuff <|-- Paralysis
    FlavourBuff <|-- Invisibility

    class Actor {
        +time: Float
        +act(): Boolean
        +spend(time)
        +postpone(time)
    }

    class Char {
        +pos: Int
        +HP: Int
        +HT: Int
        +sprite: CharSprite
        +buffs: HashSet
        +attack(enemy)
        +damage(dmg, src)
        +die(src)
    }

    class Mob {
        +state: State
        +enemy: Char
        +EXP: Int
        +loot: Any
        +doHunt()
        +doFlee()
    }

    class Hero {
        +heroClass: HeroClass
        +belongings: Belongings
        +STR: Int
        +lvl: Int
        +exp: Int
    }
```

## Item Hierarchy

All game items:

```mermaid
classDiagram
    Item <|-- EquipableItem
    Item <|-- Potion
    Item <|-- Scroll
    Item <|-- Wand
    Item <|-- Ring
    Item <|-- Food
    Item <|-- Key
    Item <|-- Bag
    Item <|-- Gold
    Item <|-- Bomb
    Item <|-- Ankh

    EquipableItem <|-- Weapon
    EquipableItem <|-- Armor

    Weapon <|-- MeleeWeapon
    Weapon <|-- MissileWeapon
    MeleeWeapon <|-- ShortSword
    MeleeWeapon <|-- Longsword
    MeleeWeapon <|-- WarHammer
    MissileWeapon <|-- Dart
    MissileWeapon <|-- Shuriken
    MissileWeapon <|-- Boomerang

    Armor <|-- ClothArmor
    Armor <|-- LeatherArmor
    Armor <|-- MailArmor
    Armor <|-- PlateArmor
    Armor <|-- ClassArmor

    Potion <|-- PotionOfHealing
    Potion <|-- PotionOfStrength
    Potion <|-- PotionOfInvisibility

    Scroll <|-- ScrollOfUpgrade
    Scroll <|-- ScrollOfIdentify
    Scroll <|-- ScrollOfTeleportation

    class Item {
        +quantity: Int
        +level: Int
        +cursed: Boolean
        +actions(hero)
        +execute(hero, action)
        +upgrade()
    }

    class Weapon {
        +STR: Int
        +MIN: Int
        +MAX: Int
        +enchantment: Enchantment
        +damageRoll(hero)
    }

    class Armor {
        +STR: Int
        +dr: Int
        +glyph: Glyph
        +proc(attacker, defender, dmg)
    }
```

## Level Hierarchy

Dungeon floor types:

```mermaid
classDiagram
    Level <|-- RegularLevel
    Level <|-- DeadEndLevel
    Level <|-- LastLevel

    RegularLevel <|-- SewerLevel
    RegularLevel <|-- PrisonLevel
    RegularLevel <|-- CavesLevel
    RegularLevel <|-- CityLevel
    RegularLevel <|-- HallsLevel
    RegularLevel <|-- LastShopLevel

    SewerLevel <|-- SewerBossLevel
    PrisonLevel <|-- PrisonBossLevel
    CavesLevel <|-- CavesBossLevel
    CityLevel <|-- CityBossLevel
    HallsLevel <|-- HallsBossLevel

    class Level {
        +map: IntArray
        +mobs: HashSet
        +heaps: SparseArray
        +entrance: Int
        +exit: Int
        +create()
        +createMobs()
        +createItems()
    }

    class RegularLevel {
        +rooms: ArrayList
        +initRooms()
        +connectRooms()
        +paintRooms()
    }
```

## UI Component Hierarchy

User interface elements:

```mermaid
classDiagram
    Group <|-- Component
    Component <|-- Button
    Component <|-- ScrollPane
    Component <|-- Window

    Button <|-- RedButton
    Button <|-- SimpleButton
    Button <|-- ItemSlot
    Button <|-- CheckBox

    ItemSlot <|-- QuickSlot
    ItemSlot <|-- InventorySlot

    Window <|-- WndBag
    Window <|-- WndItem
    Window <|-- WndHero
    Window <|-- WndSettings
    Window <|-- WndMessage
    Window <|-- WndOptions
    Window <|-- WndTabbed

    class Component {
        +x: Float
        +y: Float
        +width: Float
        +height: Float
        +createChildren()
        +layout()
    }

    class Button {
        +hotArea: PointerArea
        +onClick()
    }

    class Window {
        +chrome: NinePatch
        +hide()
        +resize(w, h)
    }
```

## Blob Hierarchy

Environmental effects:

```mermaid
classDiagram
    Actor <|-- Blob
    Blob <|-- Fire
    Blob <|-- ToxicGas
    Blob <|-- ParalyticGas
    Blob <|-- ConfusionGas
    Blob <|-- Freezing
    Blob <|-- Web
    Blob <|-- Regrowth
    Blob <|-- WellWater
    WellWater <|-- WaterOfHealth
    WellWater <|-- WaterOfAwareness
    WellWater <|-- WaterOfTransmutation

    class Blob {
        +cur: IntArray
        +off: IntArray
        +volume: Int
        +seed(level, cell, amount)
        +evolve()
    }

    class Fire {
        +burn(cell)
    }

    class ToxicGas {
        +damage: Int
    }
```

## Enchantment/Glyph Hierarchy

Equipment modifiers:

```mermaid
classDiagram
    Enchantment <|-- Fire_Enchant
    Enchantment <|-- Shock
    Enchantment <|-- Poison_Enchant
    Enchantment <|-- Leech
    Enchantment <|-- Death
    Enchantment <|-- Horror
    Enchantment <|-- Luck
    Enchantment <|-- Paralysis_Enchant
    Enchantment <|-- Slow_Enchant
    Enchantment <|-- Instability
    Enchantment <|-- Tempering

    Glyph <|-- Affection
    Glyph <|-- AntiEntropy
    Glyph <|-- AutoRepair
    Glyph <|-- Bounce
    Glyph <|-- Displacement
    Glyph <|-- Entanglement
    Glyph <|-- Metabolism
    Glyph <|-- Multiplicity
    Glyph <|-- Potential
    Glyph <|-- Stench
    Glyph <|-- Viscosity

    class Enchantment {
        +proc(weapon, attacker, defender, damage)
        +name(): String
        +glowing(): ItemSprite.Glowing
    }

    class Glyph {
        +proc(armor, attacker, defender, damage)
        +name(): String
        +glowing(): ItemSprite.Glowing
    }
```

## Plant Hierarchy

Dungeon flora:

```mermaid
classDiagram
    Plant <|-- Firebloom
    Plant <|-- Icecap
    Plant <|-- Sungrass
    Plant <|-- Earthroot
    Plant <|-- Fadeleaf
    Plant <|-- Dreamweed
    Plant <|-- Sorrowmoss
    Plant <|-- Stormvine
    Plant <|-- Rotberry

    class Plant {
        +pos: Int
        +image: Int
        +activate()
        +wither()
    }

    class Firebloom {
        Creates fire
    }

    class Sungrass {
        Heals over time
    }

    class Earthroot {
        Damage absorption
    }
```

---

## See Also

- [Architecture Overview](../architecture/overview.md)
- [Scene Flow](scene-flow.md)
- [Game Loop](game-loop.md)
