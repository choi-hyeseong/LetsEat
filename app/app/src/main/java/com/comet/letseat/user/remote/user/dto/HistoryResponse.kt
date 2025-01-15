package com.comet.letseat.user.remote.user.dto

import com.comet.letseat.user.remote.user.model.UserHistory

// 이력 결과 리턴값
class HistoryResponse(val histories : List<UserHistory>)