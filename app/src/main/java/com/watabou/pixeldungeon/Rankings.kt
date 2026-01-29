package com.watabou.pixeldungeon
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Bundlable
import com.watabou.utils.SystemTime
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
object Rankings {
    const val TABLE_SIZE = 6
    const val RANKINGS_FILE = "rankings.dat"
    const val DETAILS_FILE = "game_%d.dat"
    var records: ArrayList<Record>? = null
    var lastRecord: Int = 0
    var totalNumber: Int = 0
    var wonNumber: Int = 0
    fun submit(win: Boolean) {
        load()
        val rec = Record()
        val currentHero = Dungeon.hero ?: return
        rec.info = Dungeon.resultDescription
        rec.win = win
        rec.heroClass = currentHero.heroClass
        rec.armorTier = currentHero.tier()
        rec.score = score(win)
        val gameFile = Utils.format(DETAILS_FILE, SystemTime.now)
        try {
            Dungeon.saveGame(gameFile)
            rec.gameFile = gameFile
        } catch (e: Exception) {
            rec.gameFile = ""
        }
        val currentRecords = records ?: return
        currentRecords.add(rec)
        Collections.sort(currentRecords, scoreComparator)
        lastRecord = currentRecords.indexOf(rec)
        val size = currentRecords.size
        if (size > TABLE_SIZE) {
            val removedGame: Record
            if (lastRecord == size - 1) {
                removedGame = currentRecords.removeAt(size - 2)
                lastRecord--
            } else {
                removedGame = currentRecords.removeAt(size - 1)
            }
            if (removedGame.gameFile.length > 0) {
                Game.instance?.deleteFile(removedGame.gameFile)
            }
        }
        totalNumber++
        if (win) {
            wonNumber++
        }
        Badges.validateGamesPlayed()
        save()
    }
    private fun score(win: Boolean): Int {
        val currentHero = Dungeon.hero ?: return 0
        return (Statistics.goldCollected + currentHero.lvl * Statistics.deepestFloor * 100) * if (win) 2 else 1
    }
    private const val RECORDS = "records"
    private const val LATEST = "latest"
    private const val TOTAL = "total"
    private const val WON = "won"
    fun save() {
        val currentRecords = records ?: return
        val bundle = Bundle()
        bundle.put(RECORDS, currentRecords)
        bundle.put(LATEST, lastRecord)
        bundle.put(TOTAL, totalNumber)
        bundle.put(WON, wonNumber)
        try {
            val output = Game.instance?.openFileOutput(RANKINGS_FILE, android.content.Context.MODE_PRIVATE) ?: return
            Bundle.write(bundle, output)
            output.close()
        } catch (e: Exception) {
        }
    }
    fun load() {
        if (records != null) {
            return
        }
        val newRecords = ArrayList<Record>()
        records = newRecords
        try {
            val input = Game.instance?.openFileInput(RANKINGS_FILE) ?: return
            val bundle = Bundle.read(input)
            input.close()
            if (bundle != null) {
                for (record in bundle.getCollection(RECORDS)) {
                    newRecords.add(record as Record)
                }
                lastRecord = bundle.getInt(LATEST)
                totalNumber = bundle.getInt(TOTAL)
                if (totalNumber == 0) {
                    totalNumber = newRecords.size
                }
                wonNumber = bundle.getInt(WON)
                if (wonNumber == 0) {
                    for (rec in newRecords) {
                        if (rec.win) {
                            wonNumber++
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
    class Record : Bundlable {
        var info: String? = null
        var win: Boolean = false
        var heroClass: HeroClass = HeroClass.WARRIOR // Default to avoid null
        var armorTier: Int = 0
        var score: Int = 0
        var gameFile: String = ""
        override fun restoreFromBundle(bundle: Bundle) {
            info = bundle.getString(REASON)
            win = bundle.getBoolean(WIN)
            score = bundle.getInt(SCORE)
            heroClass = HeroClass.restoreInBundle(bundle)
            armorTier = bundle.getInt(TIER)
            gameFile = bundle.getString(GAME)
        }
        override fun storeInBundle(bundle: Bundle) {
            bundle.put(REASON, info ?: "")
            bundle.put(WIN, win)
            bundle.put(SCORE, score)
            heroClass.storeInBundle(bundle)
            bundle.put(TIER, armorTier)
            bundle.put(GAME, gameFile)
        }
        companion object {
            private const val REASON = "reason"
            private const val WIN = "win"
            private const val SCORE = "score"
            private const val TIER = "tier"
            private const val GAME = "gameFile"
        }
    }
    private val scoreComparator = Comparator<Record> { lhs, rhs ->
        // Math.signum(rhs.score - lhs.score).toInt() might be wrong if diff is large?
        // But score is int.
        // Use Integer.compare or just unsafe subtraction if ranges allowed.
        // Original Java used Math.signum.
        // Kotlin:
        // rhs.score - lhs.score
        // But wait, Math.signum returns float. Java cast to int.
        // If diff is too large it might overflow but scores are ints.
        // Safer:
        // rhs.score.compareTo(lhs.score)
        rhs.score.compareTo(lhs.score)
    }
}
