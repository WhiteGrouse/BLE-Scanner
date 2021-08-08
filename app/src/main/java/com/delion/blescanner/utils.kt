package com.delion.blescanner

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun navigateTo(manager: FragmentManager, fragment: Fragment) {
    manager.beginTransaction()
        .replace(R.id.container, fragment)
        .commitNow()
}

fun appShutdown(activity: Activity, title: String?, message: String) {
    val builder = AlertDialog.Builder(activity)

    if(title != null) {
        builder.setTitle(title)
    }
    builder
        .setMessage(message)
        .setPositiveButton("OK") { _, _ ->
            activity.finish()
            activity.moveTaskToBack(true)
        }
        .setCancelable(false)
        .show()
}