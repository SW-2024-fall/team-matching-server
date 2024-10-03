package sw._th.team.teammatching.user.domain

import javax.persistence.*

@Entity
@Table(name = "users")
data class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String
)