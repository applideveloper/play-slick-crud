package models.entities

trait BaseEntity {
    val id:      Long
    val isValid: Boolean = true
}
