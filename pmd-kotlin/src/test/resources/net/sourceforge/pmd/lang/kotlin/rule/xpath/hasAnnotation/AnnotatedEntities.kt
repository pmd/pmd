package com.example

import javax.persistence.Column
import javax.persistence.Entity
import kotlin.Deprecated

@Entity
@Deprecated("use NewService instead")
class UserEntity {

    @Column(name = "user_name")
    val name: String = ""

    @Column
    val email: String = ""

    // No annotation
    val id: Long = 0L

    @Deprecated("use newMethod")
    fun oldMethod(): String = name

    fun normalMethod(): String = email
}

// Annotation written as fully-qualified name in source (no import)
@org.springframework.stereotype.Service
class FqnAnnotatedService
