package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.InvoiceApiService
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.example.billiard.domain.repository.InvoiceRepository
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val api: InvoiceApiService
) : BaseRepository(), InvoiceRepository {

    override suspend fun getInvoices(): Resource<List<Invoice>> {
        return safeApiCall(
            apiCall = { api.getInvoices() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun getInvoiceById(invoiceId: Int): Resource<Invoice> {
        return safeApiCall(
            apiCall = { api.getInvoiceById(invoiceId) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun createInvoice(param: CreateInvoiceParam): Resource<Invoice> {
        return safeApiCall(
            apiCall = { api.createInvoice(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateInvoice(param: UpdateInvoiceParam): Resource<Invoice> {
        return safeApiCall(
            apiCall = { api.updateInvoice(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteInvoice(invoiceId: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteInvoice(invoiceId) },
            mapper = { "Xóa hóa đơn thành công" }
        )
    }
}