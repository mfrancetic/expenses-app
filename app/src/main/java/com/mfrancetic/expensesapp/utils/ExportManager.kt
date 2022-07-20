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
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import javax.inject.Inject


class ExportManager @Inject constructor(private val context: Context) {

    fun exportDatabase(database: ExpensesAppDatabase): Boolean {
        val date =
            SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis())
                .replace(" ", "_")
        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (exportDir?.exists() == false) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, "expenses_$date.csv")
        return try {
            file.createNewFile()
            val csvWrite = CSVWriter(FileWriter(file))
            val curCSV: Cursor =
                database.query("SELECT * FROM ${database.openHelper.databaseName}", null)
            csvWrite.writeNext(curCSV.columnNames)
            while (curCSV.moveToNext()) {
                //Which column you want to export
                val arrStr = arrayOfNulls<String>(curCSV.columnCount)
                for (i in 0 until curCSV.columnCount - 1) arrStr[i] = curCSV.getString(i)
                csvWrite.writeNext(arrStr)
            }
            csvWrite.close()
            curCSV.close()

            downloadFile(file)
            displayNotification(file)

            true
        } catch (e: java.lang.Exception) {
            Log.e(this.javaClass.name, e.message, e)
            false
        }
    }

    private fun downloadFile(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.name)
                put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                put(
                    MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }

            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

            val fos = resolver.openOutputStream(uri!!)
            val fin = FileInputStream(file)
            fin.copyTo(fos!!, 1024)
            fos.flush()
            fos.close()
            fin.close()
        } else {
            val destination =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + file.name

            val uri = Uri.parse("file://$destination")
            val fos = context.contentResolver.openOutputStream(uri!!)
            val fin = FileInputStream(file)
            fin.copyTo(fos!!, 1024)
            fos.flush()
            fos.close()
            fin.close()
        }
    }

    private fun displayNotification(file: File){
        val defaultChannel = "ExpensesAppChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ExpensesAppDownloadsChannel"
            val descriptionText = "ExpensesApp Downloads"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(defaultChannel, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent()
        intent.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, defaultChannel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(file.name)
            .setContentText("Expenses succesfully exported")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}