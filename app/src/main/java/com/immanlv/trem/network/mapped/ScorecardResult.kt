package com.immanlv.trem.network.mapped

import com.immanlv.trem.network.model.ScorecardDto


sealed class ScorecardResult {
    data class Success(val data: ScorecardDto): ScorecardResult()
    data class Failed(val exception: Exception): ScorecardResult()
}