package bassem.task.characters.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String
)