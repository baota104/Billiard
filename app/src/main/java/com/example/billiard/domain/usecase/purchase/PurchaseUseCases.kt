package com.example.billiard.domain.usecase.purchase

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.PurchaseInvoice
import com.example.billiard.domain.repository.PurchaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreatePurchaseInvoiceUseCase @Inject constructor(private val repository: PurchaseRepository) {
    operator fun invoke(param: CreatePurchaseParam): Flow<Resource<PurchaseInvoice>> = flow {
        emit(Resource.Loading)
        emit(repository.createPurchaseInvoice(param))
    }
}

class GetPurchaseInvoiceByIdUseCase @Inject constructor(private val repository: PurchaseRepository) {
    operator fun invoke(invoiceId: Int): Flow<Resource<PurchaseInvoice>> = flow {
        emit(Resource.Loading)
        emit(repository.getPurchaseInvoiceById(invoiceId))
    }
}

class DeletePurchaseInvoiceUseCase @Inject constructor(private val repository: PurchaseRepository) {
    operator fun invoke(invoiceId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deletePurchaseInvoice(invoiceId))
    }
}

class SearchPurchaseInvoicesUseCase @Inject constructor(private val repository: PurchaseRepository) {
    operator fun invoke(startDate: String, endDate: String): Flow<Resource<List<PurchaseHistory>>> = flow {
        emit(Resource.Loading)
        emit(repository.searchPurchaseInvoices(startDate, endDate))
    }
}