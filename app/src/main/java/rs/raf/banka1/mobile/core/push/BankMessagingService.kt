package rs.raf.banka1.mobile.core.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.MainActivity
import rs.raf.banka1.mobile.R
import rs.raf.banka1.mobile.core.di.ApplicationScope
import rs.raf.banka1.mobile.data.apis.NotificationApi
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import rs.raf.banka1.mobile.data.local.VerificationCodeEntity
import rs.raf.banka1.mobile.data.remote.requests.FcmTokenRequest
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import javax.inject.Inject

@AndroidEntryPoint
class BankMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var notificationApi: NotificationApi

    @Inject
    lateinit var verificationCodeDao: VerificationCodeDao

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    companion object {
        private const val TAG = "BankMessagingService"
        private const val CHANNEL_ID = "verification_codes"
        private const val CHANNEL_NAME = "Verifikacioni kodovi"
        private const val OTP_VALIDITY_MILLIS = 5 * 60 * 1000L // 5 minutes
        private const val NOTIFICATION_ID = 1001
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        appScope.launch {
            userPreferencesRepository.saveFcmToken(token)
            registerTokenWithBackend(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        Log.d(TAG, "FCM message received: $data")

        val type = data["type"]
        if (type != "VERIFICATION_OTP") return

        val code = data["code"] ?: return
        val operationType = data["operationType"] ?: "UNKNOWN"
        val sessionId = data["sessionId"] ?: ""
        val now = System.currentTimeMillis()

        appScope.launch {
            // Always persist to Room so code is available when user opens the app
            verificationCodeDao.insert(
                VerificationCodeEntity(
                    code = code,
                    operationType = operationType,
                    sessionId = sessionId,
                    receivedAt = now,
                    expiresAt = now + OTP_VALIDITY_MILLIS
                )
            )

            // Only show system notification if user is currently logged in
            val isLoggedIn = userPreferencesRepository.readClientData().firstOrNull() != null
            if (isLoggedIn) {
                showVerificationNotification(operationType)
            }
        }
    }

    private suspend fun registerTokenWithBackend(token: String) {
        try {
            val clientData = userPreferencesRepository.readClientData().firstOrNull()
            if (clientData != null) {
                notificationApi.registerFcmToken(
                    FcmTokenRequest(clientId = clientData.id, fcmToken = token)
                )
                Log.d(TAG, "FCM token registered with backend for clientId=${clientData.id}")
            } else {
                Log.d(TAG, "No client data yet, token saved locally for post-login sync")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to register FCM token with backend: ${e.message}")
        }
    }

    private fun showVerificationNotification(operationType: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Verifikacioni kodovi za transakcije"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "verification")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val opLabel = when (operationType) {
            "PAYMENT" -> "Placanje"
            "TRANSFER" -> "Transfer"
            "LIMIT_CHANGE" -> "Promena limita"
            "CARD_REQUEST" -> "Zahtev za karticu"
            "LOAN_REQUEST" -> "Zahtev za kredit"
            else -> operationType
        }

        val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
        val largeIcon = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888).also {
            val canvas = Canvas(it)
            drawable?.setBounds(0, 0, 128, 128)
            drawable?.draw(canvas)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle("Verifikacioni kod")
            .setContentText("Novi verifikacioni kod za $opLabel - otvorite aplikaciju")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
