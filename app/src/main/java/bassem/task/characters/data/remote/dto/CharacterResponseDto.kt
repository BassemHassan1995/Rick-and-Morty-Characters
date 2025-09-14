package bassem.task.characters.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResponseDto(
    val info: InfoDto,
    val results: List<CharacterDto>
)

@JsonClass(generateAdapter = true)
data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String
)