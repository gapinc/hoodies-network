package com.gap.hoodies_network.header

import android.text.TextUtils


/**
 * Header class is a model class of Header
 *
 * @param name must be not null/required
 * @param value must be not null/required
 *
</T> */
class Header(private val name: String, private val value: String) {

    fun getName(): String {
        return this.name
    }

    fun getValue(): String {
        return this.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val header = other as Header
        return TextUtils.equals(name, header.name) && TextUtils.equals(value, header.value)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Header[name=$name,value=$value]"
    }
}
