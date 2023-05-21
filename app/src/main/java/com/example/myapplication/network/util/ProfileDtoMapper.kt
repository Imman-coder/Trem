package com.example.myapplication.network.util

import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.util.DomainMapper
import com.example.myapplication.network.model.ProfileDto

class ProfileDtoMapper : DomainMapper<ProfileDto, Profile> {
    override fun mapToDomainModel(model: ProfileDto): Profile {
        return Profile(
            name = model.name,
            redgno = model.redgno,
            phoneno = model.phoneno,
            sem = model.sem,
            program = model.program
        )
    }

    override fun mapFromDomainModel(domainModel: Profile): ProfileDto {
        TODO("Not yet implemented")
    }
}