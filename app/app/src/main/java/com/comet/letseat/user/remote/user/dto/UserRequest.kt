package com.comet.letseat.user.remote.user.dto

import java.util.UUID

// 사용자가 서버에 요청할때 사용하는 객체 (uuid만 포함)
data class UserRequest(val uuid: UUID)