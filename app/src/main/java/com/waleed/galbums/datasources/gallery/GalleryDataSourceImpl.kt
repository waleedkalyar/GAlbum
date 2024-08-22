package com.waleed.galbums.datasources.gallery

import android.content.ContentUris
import android.content.Context
import android.database.MergeCursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.waleed.galbums.models.Album
import com.waleed.galbums.models.GalleryItem
import com.waleed.galbums.models.enums.AlbumCategory
import com.waleed.galbums.models.enums.MediaType
import com.waleed.galbums.utils.extensions.updateItemPosition
import com.waleed.galbums.utils.sealed.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class GalleryDataSourceImpl @Inject constructor(context: Context) : GalleryDataSource {
    private val contentResolver by lazy {
        context.contentResolver
    }

    override
    fun findImageAlbumsFlow(): Flow<List<Album>> = flow {

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
            MediaStore.Images.ImageColumns.DATA

        )
        val orderBy = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"

        val selection = """
        ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()


        val findAlbums = HashMap<String, Album>()
        contentResolver.query(collection, projections, selection, null, orderBy)?.use { cursor ->
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

        emit(findAlbums.values.toList().sortedByDescending { it.count })
    }.flowOn(Dispatchers.IO)

    override
    fun findImagesByAlbumId(albumId: String) = flow<List<GalleryItem>> {
        val imagesMap = mutableMapOf<String, GalleryItem>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,

            )

        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        var selection = """
        ${MediaStore.Images.Media.BUCKET_ID} = ?
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()

        val selectionFilters = """
        ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()

        var selectionArgs: Array<String>? = arrayOf(albumId)
        if (albumId.equals("All", true) || albumId.equals("All Images", true)) {
            selection = selectionFilters
            selectionArgs = null
        }

        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )


        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )

                imagesMap[id.toString()] = GalleryItem(
                    id = id.toString(),
                    name = name,
                    size = size,
                    uri = contentUri,
                    type = MediaType.IMAGE
                )

            }
        }
        query?.close()

        emit(imagesMap.values.toList())
    }.flowOn(Dispatchers.IO)

    override
    fun findVideoAlbumsFlow(): Flow<List<Album>> = flow {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
        val projections = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.BUCKET_ID,
            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA
        )
        val orderBy = "${MediaStore.Video.VideoColumns.DATE_TAKEN} DESC"

        val selection = """
        ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()

        val findAlbums = HashMap<String, Album>()
        contentResolver.query(collection, projections, selection, null, orderBy)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val videoIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
                val bucketIdIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_ID)
                val bucketNameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)

                do {
                    val bucketId = cursor.getString(bucketIdIndex)
                    val album = findAlbums[bucketId] ?: let {
                        val bucketName = cursor.getStringOrNull(bucketNameIndex) ?: "Unknown"
                        val videoId = cursor.getLongOrNull(videoIdIndex) ?: 0
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            videoId
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
        emit(findAlbums.values.toList().sortedByDescending { it.count })
        // return findAlbums.values.toList().sortedByDescending { it.count }
    }.flowOn(Dispatchers.IO)

    override
    fun findVideosByAlbumId(albumId: String) = flow<List<GalleryItem>> {
        val videosMap = mutableMapOf<String, GalleryItem>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,

            )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        var selection = """
        ${MediaStore.Images.Media.BUCKET_ID} = ?
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()

        val selectionFilters = """
        ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()

        var selectionArgs: Array<String>? = arrayOf(albumId)
        if (albumId.equals("All", true) || albumId.equals("All Videos", true)) {
            selection = selectionFilters
            selectionArgs = null
        }

        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )


        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(displayNameColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )

                videosMap[id.toString()] = GalleryItem(
                    id = id.toString(),
                    name = name,
                    size = size,
                    uri = contentUri,
                    type = MediaType.VIDEO,
                    duration = duration
                )

            }
        }
        query?.close()

        emit(videosMap.values.toList())
    }.flowOn(Dispatchers.IO)

    override
    fun findAllImageVideoAlbumsFlow(): Flow<DataResult<List<Album>>> = flow {
        try {
            findImageAlbumsFlow().zip(findVideoAlbumsFlow()) { imageBuckets, videoBuckets ->
                val result = (imageBuckets + videoBuckets).toMutableList()
                val allImages = Album(
                    id = "All Images",
                    name = "All Images",
                    uri = if (imageBuckets.isNotEmpty()) imageBuckets.first().uri else Uri.EMPTY,
                    category = AlbumCategory.ALL_IMAGES,
                    count = imageBuckets.sumOf { it.count })

                val allVideos = Album(
                    id = "All Videos",
                    name = "All Videos",
                    uri = if (videoBuckets.isNotEmpty()) videoBuckets.first().uri else Uri.EMPTY,
                    category = AlbumCategory.ALL_VIDEOS,
                    count = videoBuckets.sumOf { it.count })

                val all = Album(
                    id = "All",
                    name = "All",
                    uri = if (result.isNotEmpty()) result.first().uri else Uri.EMPTY,
                    category = AlbumCategory.ALL,
                    count = allImages.count + allVideos.count
                )

                if (result.isNotEmpty()) result.add(0, all)
                if (imageBuckets.isNotEmpty()) result.add(1, allImages)
                if (videoBuckets.isNotEmpty()) result.add(2, allVideos)

                result.groupBy { it.name }
                    .map { (_, groupedAlbums) ->
                        Album(
                            id = groupedAlbums.first().id,
                            name = groupedAlbums.first().name,
                            uri = groupedAlbums.first().uri,
                            category = groupedAlbums.first().category,
                            count = groupedAlbums.sumOf { it.count }
                        )
                    }
            }
                .catch { e -> emit(DataResult.Error(message = e.message)) }
                .collect {
                    emit(DataResult.Success(it))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(message = e.message))
        }
    }

    override
    fun findImageVideosByBucketIdFlow(albumId: String): Flow<DataResult<List<GalleryItem>>> = flow {
        try {
            val videosCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val imagesCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_TAKEN,

                )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            var selection = """
        ${MediaStore.Images.Media.BUCKET_ID} = ?
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
     """.trimIndent()

            val selectionFilters = """
        ${MediaStore.Images.Media.DATA} NOT LIKE '%thumb%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%cache%'
        AND ${MediaStore.Images.Media.DATA} NOT LIKE '%nomedia%'
    """.trimIndent()


            var selectionArgs: Array<String>? = arrayOf(albumId)
            if (albumId.equals("All", true)) {
                selection = selectionFilters
                selectionArgs = null
            }

            val mediaMap = mutableMapOf<String, GalleryItem>()

            MergeCursor(
                arrayOf(
                    contentResolver.query(
                        imagesCollection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                    ),
                    contentResolver.query(
                        videosCollection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                    ),
                )
            ).use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val timeColumns = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)


                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(displayNameColumn)
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val time = cursor.getLong(timeColumns)
                    val contentUri: Uri = if (mimeType.startsWith("video")) {
                        ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                        )
                    } else {
                        ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                        )
                    }
                    if (mimeType.startsWith("video")) {
                        if (duration > 0) {
                            mediaMap[id.toString()] = GalleryItem(
                                id = id.toString(),
                                name = name,
                                size = size,
                                uri = contentUri,
                                type = if (mimeType.startsWith("video")) MediaType.VIDEO else MediaType.IMAGE,
                                duration = duration,
                                time = time
                            )
                        }
                    } else
                        mediaMap[id.toString()] = GalleryItem(
                            id = id.toString(),
                            name = name,
                            size = size,
                            uri = contentUri,
                            type = if (mimeType.startsWith("video")) MediaType.VIDEO else MediaType.IMAGE,
                            duration = duration,
                            time = time
                        )

                }
            }


            emit(DataResult.Success(mediaMap.values.toList().sortedByDescending { it.time }))
        } catch (e: Exception) {
            emit(DataResult.Error(message = e.message))
        }
    }.flowOn(Dispatchers.IO)

}