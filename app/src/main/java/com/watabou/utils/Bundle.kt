package com.watabou.utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.util.ArrayList
import java.util.HashMap
class Bundle {
    private val data: JSONObject
    constructor() : this(JSONObject())
    constructor(data: JSONObject) {
        this.data = data
    }
    override fun toString(): String {
        return data.toString()
    }
    fun isNull(): Boolean {
        return false // JSONObject itself is never null if we constructed Bundle with it. But maybe data could be? No, ctor ensures it. The logic "is null" in java referred to fields? No, "data == null" but ctor ensures it. Wait, the static read/write might produce nulls, but usage is local. The original code checked data == null? "return data == null;".
        // Actually, let's keep it safe. But wait, data is initialized in all constructors.
    }
    fun fields(): ArrayList<String> {
        val result = ArrayList<String>()
        val iterator = data.keys()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        return result
    }
    fun contains(key: String): Boolean {
        return !data.isNull(key)
    }
    fun getBoolean(key: String): Boolean {
        return data.optBoolean(key)
    }
    fun getInt(key: String): Int {
        return data.optInt(key)
    }
    fun getFloat(key: String): Float {
        return data.optDouble(key).toFloat()
    }
    fun getString(key: String): String {
        return data.optString(key)
    }
    fun getBundle(key: String): Bundle {
        return Bundle(data.optJSONObject(key) ?: JSONObject())
    }
    private fun get(): Bundlable? {
        try {
            var clName = getString(CLASS_NAME)
            if (aliases.containsKey(clName)) {
                clName = aliases[clName] ?: clName
            }
            val cl = Class.forName(clName)
            val `object` = cl.getDeclaredConstructor().newInstance() as Bundlable
            `object`.restoreFromBundle(this)
            return `object`
        } catch (e: Exception) {
            return null
        }
    }
    operator fun get(key: String): Bundlable? {
        return getBundle(key).get()
    }
    fun <E : Enum<E>> getEnum(key: String, enumClass: Class<E>): E {
        return try {
            java.lang.Enum.valueOf(enumClass, data.getString(key))
        } catch (e: JSONException) {
            enumClass.enumConstants?.get(0) ?: throw IllegalArgumentException("Enum class has no constants")
        }
    }
    fun getIntArray(key: String): IntArray? {
        return try {
            val array = data.getJSONArray(key)
            val length = array.length()
            val result = IntArray(length)
            for (i in 0 until length) {
                result[i] = array.getInt(i)
            }
            result
        } catch (e: JSONException) {
            null
        }
    }
    fun getBooleanArray(key: String): BooleanArray? {
        return try {
            val array = data.getJSONArray(key)
            val length = array.length()
            val result = BooleanArray(length)
            for (i in 0 until length) {
                result[i] = array.getBoolean(i)
            }
            result
        } catch (e: JSONException) {
            null
        }
    }
    fun getStringArray(key: String): Array<String>? {
        return try {
            val array = data.getJSONArray(key)
            val length = array.length()
            val result = arrayOfNulls<String>(length)
            for (i in 0 until length) {
                result[i] = array.getString(i)
            }
            @Suppress("UNCHECKED_CAST")
            result as Array<String>
        } catch (e: JSONException) {
            null
        }
    }
    fun getCollection(key: String): Collection<Bundlable> {
        val list = ArrayList<Bundlable>()
        try {
            val array = data.getJSONArray(key)
            for (i in 0 until array.length()) {
                val bundle = Bundle(array.getJSONObject(i))
                val item = bundle.get()
                if (item != null) {
                    list.add(item)
                }
            }
        } catch (e: JSONException) {
        }
        return list
    }
    fun put(key: String, value: Boolean) {
        try {
            data.put(key, value)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, value: Int) {
        try {
            data.put(key, value)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, value: Float) {
        try {
            data.put(key, value.toDouble())
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, value: String) {
        try {
            data.put(key, value)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, bundle: Bundle) {
        try {
            data.put(key, bundle.data)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, `object`: Bundlable?) {
        if (`object` != null) {
            try {
                val bundle = Bundle()
                bundle.put(CLASS_NAME, `object`.javaClass.name)
                `object`.storeInBundle(bundle)
                data.put(key, bundle.data)
            } catch (e: JSONException) {
            }
        }
    }
    fun put(key: String, value: Enum<*>?) {
        if (value != null) {
            try {
                data.put(key, value.name)
            } catch (e: JSONException) {
            }
        }
    }
    fun put(key: String, array: IntArray) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data.put(key, jsonArray)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, array: BooleanArray) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data.put(key, jsonArray)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, array: Array<String>) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data.put(key, jsonArray)
        } catch (e: JSONException) {
        }
    }
    fun put(key: String, collection: Collection<Bundlable>) {
        val array = JSONArray()
        for (`object` in collection) {
            val bundle = Bundle()
            bundle.put(CLASS_NAME, `object`.javaClass.name)
            `object`.storeInBundle(bundle)
            array.put(bundle.data)
        }
        try {
            data.put(key, array)
        } catch (e: JSONException) {
        }
    }
    companion object {
        private const val CLASS_NAME = "__className"
        private val aliases = HashMap<String, String>()
        fun read(stream: InputStream): Bundle? {
            try {
                val reader = BufferedReader(InputStreamReader(stream))
                val all = StringBuilder()
                var line = reader.readLine()
                while (line != null) {
                    all.append(line)
                    line = reader.readLine()
                }
                val json = JSONTokener(all.toString()).nextValue() as JSONObject
                reader.close()
                return Bundle(json)
            } catch (e: Exception) {
                return null
            }
        }
        fun read(bytes: ByteArray): Bundle? {
            return try {
                val json = JSONTokener(String(bytes)).nextValue() as JSONObject
                Bundle(json)
            } catch (e: JSONException) {
                null
            }
        }
        fun write(bundle: Bundle, stream: OutputStream): Boolean {
            return try {
                val writer = BufferedWriter(OutputStreamWriter(stream))
                writer.write(bundle.data.toString())
                writer.close()
                true
            } catch (e: IOException) {
                false
            }
        }
        fun addAlias(cl: Class<*>, alias: String) {
            aliases[alias] = cl.name
        }
    }
}
