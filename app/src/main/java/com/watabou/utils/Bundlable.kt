package com.watabou.utils
interface Bundlable {
    fun restoreFromBundle(bundle: Bundle)
    fun storeInBundle(bundle: Bundle)
}
