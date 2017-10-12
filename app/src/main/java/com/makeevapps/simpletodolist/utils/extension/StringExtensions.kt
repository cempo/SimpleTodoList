@file:JvmName("StringExtensions")

package com.makeevapps.simpletodolist.utils.extension

import java.util.*

fun String.generateRandom(): String = UUID.randomUUID().toString()
