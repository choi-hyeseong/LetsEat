package com.comet.letseat.user.remote.predict.dto

import java.util.UUID

data class PredictRequest(val uuid : UUID, val categories : List<String>)