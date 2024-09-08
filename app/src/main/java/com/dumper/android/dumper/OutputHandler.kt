package com.dumper.android.dumper

import android.os.ParcelFileDescriptor
import android.util.Log
import com.dumper.android.ui.console.ConsoleViewModel
import com.dumper.android.utils.TAG
import java.io.FileOutputStream
import java.io.OutputStream

class OutputHandler {
    private var isRoot = false
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private lateinit var outputStream: OutputStream
    private lateinit var console: ConsoleViewModel

    private constructor()

    /**
     * This method is used to send message to client
     * Use this method if you're on root services
     * @param from: Message from client
     * @param reply: Message to client
    */
    constructor(parcelFileDescriptor: ParcelFileDescriptor) : this() {
        isRoot = true
        this.parcelFileDescriptor = parcelFileDescriptor
        outputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
    }

    /**
     * This method is used to append message to console
     * Use this method if you're on non-root
     * @param console: ConsoleViewModel to append
    */
    constructor(console: ConsoleViewModel) : this() {
        isRoot = false
        this.console = console
    }

    private fun processInput(str: String) {
        if (isRoot) {
            try {
                outputStream.write(str.toByteArray())
            } catch (e: Exception) {
                Log.e(TAG, "Error writing to output stream", e)
            }
        } else {
            console.append(str)
        }
    }

    fun finish(code: Int) {
        if (isRoot) {
            try {
                outputStream.close()
                parcelFileDescriptor.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing output stream", e)
            }
        } else {
            console.finish(code)
        }
    }

    fun append(text: String) {
        processInput(text)
    }

    fun appendLine(text: String) {
        processInput(text + "\n")
    }

    fun appendError(text: String) {
        appendLine("[ERROR] $text")
    }

    fun appendWarning(text: String) {
        appendLine("[WARNING] $text")
    }

    fun appendInfo(text: String) {
        appendLine("[INFO] $text")
    }

    fun appendSuccess(text: String) {
        appendLine("[SUCCESS] $text")
    }
}