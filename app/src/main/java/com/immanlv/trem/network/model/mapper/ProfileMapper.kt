package com.immanlv.trem.network.model.mapper

import android.util.Log
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.network.model.ProfileDto
import com.immanlv.trem.network.util.DomainMapper

object ProfileMapper:DomainMapper<ProfileDto,Profile> {
    override fun mapToDomainModel(model: ProfileDto): Profile {
        Log.d("TAG", "Profile mapToDomainModel: $model")
        return Profile(
            name = model.name,
            regdno = model.redgno,
            phoneno = model.phoneno,
            sem = model.sem,
            program = model.program,
            branch = model.branch,
            batch = model.batch,
            extras = model.extras,
            sicno = model.sicno,
        )
    }

    override fun mapFromDomainModel(domainModel: Profile): ProfileDto {
        TODO("Not yet implemented")
    }
}