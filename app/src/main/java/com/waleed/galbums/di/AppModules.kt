package com.waleed.galbums.di

import android.content.Context
import com.waleed.galbums.datasources.gallery.GalleryDataSource
import com.waleed.galbums.datasources.gallery.GalleryDataSourceImpl
import com.waleed.galbums.repos.albums.AlbumsRepo
import com.waleed.galbums.repos.albums.AlbumsRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    fun provideGalleyDataSource(@ApplicationContext context: Context): GalleryDataSource =
        GalleryDataSourceImpl(context)

    @Provides
    fun provideAlbumsRepo(gDataSource: GalleryDataSource):AlbumsRepo = AlbumsRepoImpl(gDataSource)

}