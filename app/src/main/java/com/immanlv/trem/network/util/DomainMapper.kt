package com.immanlv.trem.network.util

interface DomainMapper<T,DomainModel> {

    fun mapToDomainModel(model:T):DomainModel

    fun mapFromDomainModel(domainModel: DomainModel):T

}