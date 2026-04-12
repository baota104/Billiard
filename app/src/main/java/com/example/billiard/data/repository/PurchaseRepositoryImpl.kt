package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.PurchaseApiService
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.PurchaseInvoice
import com.example.billiard.domain.repository.PurchaseRepository
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val api: PurchaseApiService
) : BaseRepository(), PurchaseRepository {

    override suspend fun createPurchaseInvoice(param: CreatePurchaseParam): Resource<PurchaseInvoice> {
        return safeApiCall(
            apiCall = { api.createPurchaseInvoice(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun getPurchaseInvoiceById(invoiceId: Int): Resource<PurchaseInvoice> {
        return safeApiCall(
            apiCall = { api.getPurchaseInvoiceById(invoiceId) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deletePurchaseInvoice(invoiceId: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deletePurchaseInvoice(invoiceId) },
            mapper = { "Xóa hóa đơn nhập hàng thành công" }
        )
    }

    override suspend fun searchPurchaseInvoices(
        startDate: String,
        endDate: String
    ): Resource<List<PurchaseHistory>> {
        return safeApiCall(
            apiCall = { api.searchPurchaseInvoices(startDate, endDate) },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }
}