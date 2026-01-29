# Game Loop Diagram

This document explains the game loop and turn-based actor system in Amazing Pixel Dungeon.

## Main Game Loop

The game runs at approximately 30 FPS with a fixed timestep:

```mermaid
flowchart TD
    subgraph GameLoop["Game Loop (30 FPS)"]
        A[Frame Start] --> B[Calculate Delta Time]
        B --> C[Process Input Events]
        C --> D[Scene.update]
        D --> E[Actor.process]
        E --> F[Scene.draw]
        F --> G[Swap Buffers]
        G --> H{Continue?}
        H -->|Yes| A
        H -->|No| I[End]
    end
```

## Frame Timing

```kotlin
// In Game.kt
class Game {
    companion object {
        const val TICK = 1f / 30f  // ~33ms per frame
    }

    private var lastTime: Long = 0

    fun step() {
        val now = SystemClock.elapsedRealtime()
        val delta = (now - lastTime) / 1000f
        lastTime = now

        // Clamp delta to prevent spiral of death
        val elapsed = min(delta, 0.1f)

        // Update game logic
        scene?.update(elapsed)

        // Process turn-based actors
        if (scene is GameScene) {
            Actor.process()
        }
    }

    fun draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        camera?.updateMatrix()
        scene?.draw()
    }
}
```

## Scene Update Cascade

The update propagates through the scene graph:

```mermaid
flowchart TD
    subgraph SceneUpdate["Scene.update(delta)"]
        A[Scene.update] --> B[Update Children]
        B --> C[StatusPane.update]
        B --> D[Toolbar.update]
        B --> E[DungeonTilemap.update]
        B --> F[Mobs Group.update]
        F --> G[Each Mob Sprite]
        B --> H[Hero Sprite.update]
        B --> I[Effects.update]
        B --> J[FogOfWar.update]
    end
```

## Actor System

### Turn-Based Processing

```mermaid
flowchart TD
    subgraph ActorProcess["Actor.process()"]
        A[Start] --> B{Any actors?}
        B -->|No| C[Return false]
        B -->|Yes| D[Find actor with<br/>minimum time]
        D --> E[Set now = actor.time]
        E --> F[Set current = actor]
        F --> G[actor.act]
        G --> H{Returns true?}
        H -->|Yes| I[current = null]
        I --> J[Return true]
        H -->|No| K[Return false<br/>Waiting for input]
    end
```

### Actor Time Management

```
Time (turns)
│
├─ 0.0  ──► Hero acts
│           spend(1.0) → next turn at 1.0
│
├─ 0.0  ──► Rat acts
│           spend(1.0) → next turn at 1.0
│
├─ 0.5  ──► Buff ticks
│           spend(1.0) → next turn at 1.5
│
├─ 1.0  ──► Hero acts (faster speed)
│           spend(0.5) → next turn at 1.5
│
├─ 1.0  ──► Rat acts
│           spend(1.0) → next turn at 2.0
│
└─ ...
```

### Turn Order Priority Queue

```kotlin
object Actor {
    private val all = HashSet<Actor>()
    var now: Float = 0f

    fun process(): Boolean {
        // Find actor with earliest scheduled time
        var next: Actor? = null
        var minTime = Float.MAX_VALUE

        all.forEach { actor ->
            if (actor.time < minTime) {
                minTime = actor.time
                next = actor
            }
        }

        // Execute that actor's turn
        next?.let { actor ->
            now = actor.time
            current = actor

            if (actor.act()) {
                current = null
                return true  // Turn completed
            } else {
                return false  // Waiting for input
            }
        }

        return false
    }
}
```

## Hero Action Flow

When the hero needs to act:

```mermaid
flowchart TD
    subgraph HeroAction["Hero.act()"]
        A[Hero.act called] --> B{Has queued action?}
        B -->|No| C[Return false<br/>Wait for input]
        B -->|Yes| D{Action type?}

        D -->|Move| E[Calculate path]
        E --> F[Move one step]
        F --> G[spend 1/speed]

        D -->|Attack| H[Check range]
        H --> I[Play attack anim]
        I --> J[Calculate damage]
        J --> K[Apply damage]
        K --> L[spend attackDelay]

        D -->|Wait| M[search for secrets]
        M --> N[spend 1 turn]

        D -->|Use Item| O[Execute item action]
        O --> P[spend item time]

        G --> Q[Return true]
        L --> Q
        N --> Q
        P --> Q
    end
```

## Mob AI Flow

Each mob processes its AI state:

```mermaid
flowchart TD
    subgraph MobAI["Mob.act()"]
        A[Mob.act called] --> B[Update enemy target]
        B --> C{Current state?}

        C -->|SLEEPING| D{Notice hero?}
        D -->|No| E[spend TICK]
        E --> R[Return true]
        D -->|Yes| F[Wake up]
        F --> G[State = HUNTING]

        C -->|WANDERING| H{See enemy?}
        H -->|No| I[Move toward target]
        I --> J[spend 1/speed]
        J --> R
        H -->|Yes| K[State = HUNTING]

        C -->|HUNTING| L{Can attack?}
        L -->|Yes| M[Attack enemy]
        M --> N[spend attackDelay]
        N --> R
        L -->|No| O{Can get closer?}
        O -->|Yes| P[Move toward enemy]
        P --> Q[spend 1/speed]
        Q --> R
        O -->|No| E

        C -->|FLEEING| S{Can flee?}
        S -->|Yes| T[Move away]
        T --> U[spend 1/speed]
        U --> R
        S -->|No| E
    end
```

## Buff Processing

Buffs act as independent actors:

```mermaid
flowchart TD
    subgraph BuffProcess["Buff.act()"]
        A[Buff.act called] --> B{Buff type?}

        B -->|Burning| C[Deal fire damage]
        C --> D[Reduce duration]
        D --> E{Duration > 0?}
        E -->|Yes| F[spend TICK]
        F --> G[Return true]
        E -->|No| H[Detach buff]
        H --> G

        B -->|Poison| I[Deal poison damage]
        I --> J[Reduce level]
        J --> K{Level > 0?}
        K -->|Yes| F
        K -->|No| H

        B -->|Regeneration| L[Heal 1 HP]
        L --> F

        B -->|FlavourBuff| M[Detach immediately]
        M --> G
    end
```

## Combat Resolution

```mermaid
sequenceDiagram
    participant H as Hero
    participant M as Mob
    participant G as GameScene

    H->>H: act() - Attack action
    H->>H: attack(mob)
    H->>M: hit calculation
    Note over H,M: acuRoll vs defRoll
    alt Hit successful
        H->>M: damageRoll()
        M->>M: damage reduction (DR)
        M->>M: HP -= damage
        M->>G: showStatus(damage)
        alt HP <= 0
            M->>M: die()
            M->>H: earnExp(EXP)
            M->>G: drop loot
        end
    else Miss
        M->>G: showStatus("miss")
    end
    H->>H: spend(attackDelay)
```

## Input Handling

```mermaid
flowchart TD
    subgraph InputFlow["Input Processing"]
        A[Touch Event] --> B[CellSelector]
        B --> C{Valid cell?}
        C -->|No| D[Ignore]
        C -->|Yes| E{Cell contents?}

        E -->|Enemy| F[Hero.curAction = Attack]
        E -->|Item| G[Hero.curAction = PickUp]
        E -->|Stairs| H[Hero.curAction = Descend/Ascend]
        E -->|Door| I[Hero.curAction = OpenDoor]
        E -->|Empty| J[Hero.curAction = Move]

        F --> K[Actor.process resumes]
        G --> K
        H --> K
        I --> K
        J --> K
    end
```

## Animation Synchronization

```mermaid
sequenceDiagram
    participant A as Actor System
    participant S as Sprite
    participant G as GameScene

    A->>A: hero.attack(target)
    A->>S: sprite.attack(cell)
    S->>S: play(attackAnim)
    Note over A: Returns false (waiting)

    loop Each frame
        S->>S: update() - advance frames
    end

    S->>S: onComplete(attackAnim)
    S->>A: callback - animation done
    A->>A: Actor.process resumes
```

## Frame Budget

Target: 33ms per frame (30 FPS)

```
┌─────────────────────────────────────────────────────┐
│                    Frame (33ms)                      │
├────────────┬──────────────┬─────────────────────────┤
│   Input    │    Update    │         Render          │
│   (~1ms)   │   (~5-10ms)  │       (~15-20ms)        │
├────────────┼──────────────┼─────────────────────────┤
│ Touch      │ Scene.update │ Clear screen            │
│ events     │ Actor.process│ Draw tilemap            │
│ Key        │ Sprite anims │ Draw sprites            │
│ events     │ Particle FX  │ Draw effects            │
│            │ Camera follow│ Draw UI                 │
│            │              │ Swap buffers            │
└────────────┴──────────────┴─────────────────────────┘
```

## Optimization Strategies

### Batch Rendering
```kotlin
// Sprites batched by texture
fun draw() {
    NoosaScript.get()
        .camera(camera)
        .texture(texture)
        .drawQuads(allSpriteVertices)
}
```

### Dirty Flags
```kotlin
// Only update changed tiles
class Tilemap {
    var dirty = BooleanArray(LENGTH)

    fun updateCell(cell: Int) {
        dirty[cell] = true
    }

    fun draw() {
        if (dirty.any { it }) {
            rebuildVertices()
            dirty.fill(false)
        }
        render()
    }
}
```

### Object Pooling
```kotlin
// Reuse particle objects
object ParticlePool {
    private val available = Stack<Particle>()

    fun get(): Particle {
        return if (available.isEmpty()) {
            Particle()
        } else {
            available.pop().reset()
        }
    }

    fun recycle(p: Particle) {
        available.push(p)
    }
}
```

---

## See Also

- [Actor System](../systems/actor-system.md) - Detailed actor documentation
- [Scene System](../systems/scene-system.md) - Scene lifecycle
- [Rendering System](../systems/rendering-system.md) - Graphics pipeline
