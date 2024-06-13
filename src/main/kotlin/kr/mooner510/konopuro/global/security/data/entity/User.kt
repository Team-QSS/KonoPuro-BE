package kr.mooner510.konopuro.global.security.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@SQLRestriction(value = "is_deleted = false")
@SQLDelete(sql = "UPDATE user SET is_deleted = true where id = ?")
@Entity
@Table(name = "user")
class User(
    @Column(nullable = false, length = 24)
    var userName: String,

    @Column(nullable = false)
    var loginId: String,

    @Column(nullable = false)
    private var password: String,

    @Column(nullable = true, unique = true, columnDefinition = "BINARY(16)")
    var client: UUID? = null
) : UserDetails, BaseEntity() {
    @Id
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    @Column(name = "is_deleted", nullable = false)
    val isDeleted = false

    companion object {
//        val ADMIN_AUTH = mutableListOf(
//            SimpleGrantedAuthority("ADMIN"),
//            SimpleGrantedAuthority("TEACHER"),
////            SimpleGrantedAuthority("DONGPO"),
//            SimpleGrantedAuthority("STUDENT")
//        )
//        val TEACHER_AUTH = mutableListOf(SimpleGrantedAuthority("TEACHER"))
//        val DONGPO_AUTH = mutableListOf(SimpleGrantedAuthority("DONGPO"))
//        val STUDENT = mutableListOf(SimpleGrantedAuthority("STUDENT"))
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptyList()
//        return if (admin) ADMIN_AUTH else if (teacher) TEACHER_AUTH else if (clubOwner != null) DONGPO_AUTH else STUDENT
    }

    fun setPassword(password: String) {
        this.password = password;
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return userName
    }

    override fun isAccountNonExpired(): Boolean {
        return !isDeleted
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return !isDeleted
    }
}