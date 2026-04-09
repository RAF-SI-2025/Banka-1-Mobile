package rs.raf.banka1.mobile.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rs.raf.banka1.mobile.data.local.BankaDatabase
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): BankaDatabase {
        return Room.databaseBuilder(
            context,
            BankaDatabase::class.java,
            "banka1_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideVerificationCodeDao(database: BankaDatabase): VerificationCodeDao {
        return database.verificationCodeDao()
    }
}
