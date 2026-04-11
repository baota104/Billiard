package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam

interface InvoiceRepository {
    suspend fun getInvoices(): Resource<List<Invoice>>
    suspend fun getInvoiceById(invoiceId: Int): Resource<Invoice>
    suspend fun createInvoice(param: CreateInvoiceParam): Resource<Invoice>
    suspend fun updateInvoice(param: UpdateInvoiceParam): Resource<Invoice>
    suspend fun deleteInvoice(invoiceId: Int): Resource<String>
}