package com.pdfwallet.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.pdfwallet.data.db.AppDatabase
import com.pdfwallet.data.db.DocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Singleton
import android.util.Base64

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val PREFS_NAME = "pdf_wallet_secure_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase"

    @Provides
    @Singleton
    fun provideSupportFactory(@ApplicationContext context: Context): SupportFactory {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        var passphrase = sharedPreferences.getString(KEY_PASSPHRASE, null)
        if (passphrase == null) {
            val randomBytes = ByteArray(32)
            SecureRandom().nextBytes(randomBytes)
            passphrase = Base64.encodeToString(randomBytes, Base64.NO_WRAP)
            sharedPreferences.edit().putString(KEY_PASSPHRASE, passphrase).apply()
        }

        val factory = SupportFactory(passphrase.toByteArray())
        return factory
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pdf_wallet.db"
        )
        .openHelperFactory(supportFactory)
        .build()
    }

    @Provides
    fun provideDocumentDao(appDatabase: AppDatabase): DocumentDao {
        return appDatabase.documentDao()
    }
}
