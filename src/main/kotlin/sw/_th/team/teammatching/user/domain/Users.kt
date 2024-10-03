package sw._th.team.teammatching.user.domain

import javax.persistence.*

@Entity
@Table(name = "users")
data class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long,

    @Column(name = "user_name", nullable = false)
    var username: String,

    @Column(name = "user_email", nullable = false)
    var email: String,

    @Column(name = "user_password", nullable = false)
    var password: String,

    @Column(name = "user_major")
    var major: String,

    @Column(name = "user_student_id")
    var studentId: String,

    @Column(name = "user_phone_number")
    var phoneNumber: String,

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
    var meetings: List<User_Meeting> = mutableListOf()

    @Column(name = "user_attendence_score")
    var attendence_score: Int,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_file_id")
    var profile: File? = null,

    var features: List<String>
)
