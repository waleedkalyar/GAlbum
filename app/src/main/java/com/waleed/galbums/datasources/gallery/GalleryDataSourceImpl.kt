package com.waleed.galbums.datasources.gallery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.waleed.galbums.models.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GalleryDataSourceImpl @Inject constructor(context: Context) : GalleryDataSource {
    private val contentResolver by lazy {
        context.contentResolver
    }

    override
    fun findAlbumsFlow(): Flow<List<Album>> = flow {
        // val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val projections = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        )
        val orderBy = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
        val findAlbums = HashMap<String, Album>()
        contentResolver.query(collection, projections, null, null, orderBy)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val imageIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                val bucketIdIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)
                val bucketNameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)

                do {
                    val bucketId = cursor.getString(bucketIdIndex)
                    val album = findAlbums[bucketId] ?: let {
                        val bucketName = cursor.getString(bucketNameIndex)
                        // val lastImageUri = Uri.parse(cursor.getString(imageUriIndex))
                        val imageId = cursor.getLong(imageIdIndex)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId
                        )
                        val album = Album(
                            id = bucketId,
                            name = bucketName,
                            uri = uri,
                            count = 0,
                        )
                        findAlbums[bucketId] = album
                        emit(findAlbums.values.toList().sortedByDescending { it.count })
                        album
                    }
                    album.count++
                } while (cursor.moveToNext())
            }
        }

        // add all albums
        findAlbums["All"] =
            Album(
                id = "All",
                name = "All",
                uri = findAlbums.values.first().uri,
                count = findAlbums.values.sumOf { it.count })
        emit(findAlbums.values.toList().sortedByDescending { it.count })
    }.flowOn(Dispatchers.IO)
}