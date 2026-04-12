package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.PurchaseInvoice

interface PurchaseRepository {
    suspend fun createPurchaseInvoice(param: CreatePurchaseParam): Resource<PurchaseInvoice>
    suspend fun getPurchaseInvoiceById(invoiceId: Int): Resource<PurchaseInvoice>
    suspend fun deletePurchaseInvoice(invoiceId: Int): Resource<String>
    suspend fun searchPurchaseInvoices(startDate: String, endDate: String): Resource<List<PurchaseHistory>>
}