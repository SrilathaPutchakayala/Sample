package com.tm.ui.handlers


interface OnModelClickListener<in T : Any> {
    fun onClick(model: T)
}