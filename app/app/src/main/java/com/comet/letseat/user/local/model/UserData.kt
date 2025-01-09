package com.comet.letseat.user.local.model

import java.util.UUID

/**
 * 유저 정보가 저장될 모델
 * @property uuid 서버 요청시 필요한 고유 id입니다.
 */
data class UserData(val uuid : UUID)