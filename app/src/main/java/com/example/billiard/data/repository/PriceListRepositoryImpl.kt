package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.PriceListApiService
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.PriceList
import com.example.billiard.domain.model.UpdatePriceListParam
import com.example.billiard.domain.repository.PriceListRepository
import javax.inject.Inject

class PriceListRepositoryImpl @Inject constructor(
    private val api: PriceListApiService
) : BaseRepository(), PriceListRepository {

    override suspend fun getPriceLists(): Resource<List<PriceList>> {
        return safeApiCall(
            apiCall = { api.getPriceLists() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun createPriceList(param: CreatePriceListParam): Resource<PriceList> {
        return safeApiCall(
            apiCall = { api.createPriceList(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updatePriceList(param: UpdatePriceListParam): Resource<PriceList> {
        return safeApiCall(
            apiCall = { api.updatePriceList(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deletePriceList(id: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deletePriceList(id) },
            mapper = { "Xóa khung giờ thành công" } // Backend có trả về obj đã xóa, nhưng mình map thành thông báo String để UI tiện hiện Toast
        )
    }
}