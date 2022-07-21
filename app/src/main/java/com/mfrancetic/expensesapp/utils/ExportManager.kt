package com.mfrancetic.expensesapp.utils

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.db.ExpensesAppDatabase
import com.mfrancetic.expensesapp.models.DownloadFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.PrintWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ExportManager @Inject constructor(private val context: Context) {

    private val locale = Locale.GERMANY
    private val dateFormat = DateFormat.MEDIUM

    fun exportDatabase(database: ExpensesAppDatabase, downloadFormat: DownloadFormat): Boolean {
        val currentDate =
            SimpleDateFormat.getDateTimeInstance(dateFormat, dateFormat, locale)
                .format(System.currentTimeMillis())
                .replace(" ", "_")

        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (exportDir?.exists() != true) {
            exportDir?.mkdirs()
        }

        return try {
            val extension = if (downloadFormat == DownloadFormat.CSV) ".csv" else ".db"
            val file = File(exportDir, "expenses_$currentDate$extension")
            file.createNewFile()
            if (downloadFormat == DownloadFormat.CSV) {
                createCSVFile(database, file)
            } else {
                val currentFile = File("data/data/com.mfrancetic.expensesapp/databases/expenses_database")
                currentFile.copyTo(target = file, overwrite = true)
            }

            downloadFile(file, downloadFormat)
            displayNotification(file, context)

            true
        } catch (e: java.lang.Exception) {
            Log.e(this.javaClass.name, e.message, e)
            false
        }
    }

    private fun createCSVFile(database: ExpensesAppDatabase, file: File) {
        val printWriter: PrintWriter?
        printWriter = PrintWriter(FileWriter(file))

        val curCSV: Cursor =
            database.query("SELECT * FROM expenses", null)
        printWriter.println("ID, TITLE, AMOUNT, CURRENCY, CATEGORY, DATE")

        while (curCSV.moveToNext()) {
            val id = curCSV.getString(0)
            val title = curCSV.getString(1)
            val amount = curCSV.getDouble(2)
            val currency = curCSV.getString(3)
            val category = curCSV.getString(4)
            val dateMillis = curCSV.getLong(5)
            val date = SimpleDateFormat.getDateInstance(dateFormat, locale).format(dateMillis)
            val record = "$id, $title, $amount, $currency, $category, $date"
            printWriter.println(record)
        }

        printWriter.close()
        curCSV.close()
    }

    private fun downloadFile(file: File, format: DownloadFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val mimeType = if (format == DownloadFormat.CSV) "text/csv" else "application/x-sqlite3"

            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.name)
                put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType)
                put(
                    MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }

            resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )?.let { uri ->
                resolver.openOutputStream(uri)?.let { outputStream ->
                    val inputStream = FileInputStream(file)
                    inputStream.copyTo(outputStream, 1024)
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                }
            }

        } else {
            val destination =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + file.name

            val uri = Uri.parse("file://$destination")
            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                val inputStream = FileInputStream(file)
                inputStream.copyTo(outputStream, 1024)
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            }
        }
    }

    private fun displayNotification(file: File, context: Context) {
        val notificationChannelName = context.getString(R.string.notification_channel_name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(notificationChannelName, notificationChannelName, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent()
        intent.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, notificationChannelName)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(file.name)
            .setContentText(context.getString(R.string.notification_download_success))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}