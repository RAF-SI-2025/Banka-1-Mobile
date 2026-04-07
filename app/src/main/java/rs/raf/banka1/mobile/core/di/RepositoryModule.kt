package rs.raf.banka1.mobile.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rs.raf.banka1.mobile.data.repository.AuthRepositoryImpl
import rs.raf.banka1.mobile.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}