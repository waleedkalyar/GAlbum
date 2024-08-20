package com.waleed.galbums.models

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import java.util.UUID

object UriSerializer : KSerializer<Uri> {
    override val descriptor = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uri) {
        return encoder.encodeString(value.toString())
    }
}

object FileSerializer : KSerializer<File> {
    override val descriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): File {
        return File(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: File) {
        return encoder.encodeString(value.path)
    }
}


@Serializable
data class Album(
    val id: String,
    val name: String,
    var count: Long = 0,

    @Serializable(with = UriSerializer::class)
    var uri: Uri? = null,

    @Serializable(with = FileSerializer::class)
    var file: File? = null
)
