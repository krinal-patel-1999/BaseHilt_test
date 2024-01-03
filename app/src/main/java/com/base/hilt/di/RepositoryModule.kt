package com.base.hilt.di

import com.apollographql.apollo3.ApolloClient
import com.base.hilt.ui.login.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideHomeRepository( apolloClient: ApolloClient) = LoginRepository( apolloClient)
}