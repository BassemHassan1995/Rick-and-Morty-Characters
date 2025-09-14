package bassem.task.characters.domain.model

import android.content.Context
import bassem.task.characters.R

enum class CharacterStatus {
    ALIVE,
    DEAD,
    UNKNOWN;

    companion object {
        fun fromString(status: String?): CharacterStatus {
            return when (status?.lowercase()) {
                "alive" -> ALIVE
                "dead" -> DEAD
                else -> UNKNOWN
            }
        }
    }

    fun displayName(context: Context): String {
        return when (this) {
            ALIVE -> context.getString(R.string.character_status_alive)
            DEAD -> context.getString(R.string.character_status_dead)
            UNKNOWN -> context.getString(R.string.character_status_unknown)
        }
    }
}
