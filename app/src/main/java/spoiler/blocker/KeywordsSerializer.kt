/* $Id$ */
package spoiler.blocker

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by Muthuraj on 25/03/22.
 *
 * Jambav, Zoho Corporation
 */
class KeywordsSerializer : Serializer<Keywords> {
    override val defaultValue = Keywords()

    override suspend fun readFrom(input: InputStream): Keywords {
        try {
            return Keywords.ADAPTER.decode(input)
        } catch (e: IOException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: Keywords, output: OutputStream) {
        t.adapter.encode(output, t)
    }
}

val Context.keywordsDataStore by dataStore(
    fileName = "keywords.pb",
    serializer = KeywordsSerializer()
)