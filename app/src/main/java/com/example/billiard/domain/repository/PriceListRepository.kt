package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.PriceList
import com.example.billiard.domain.model.UpdatePriceListParam

interface PriceListRepository {
    suspend fun getPriceLists(): Resource<List<PriceList>>
    suspend fun createPriceList(param: CreatePriceListParam): Resource<PriceList>
    suspend fun updatePriceList(param: UpdatePriceListParam): Resource<PriceList>
    suspend fun deletePriceList(id: Int): Resource<String>
}