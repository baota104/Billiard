package com.example.billiard.domain.usecase.invoice

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.example.billiard.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetInvoicesUseCase @Inject constructor(private val repository: InvoiceRepository) {
    operator fun invoke(): Flow<Resource<List<Invoice>>> = flow {
        emit(Resource.Loading)
        emit(repository.getInvoices())
    }
}

class GetInvoiceByIdUseCase @Inject constructor(private val repository: InvoiceRepository) {
    operator fun invoke(invoiceId: Int): Flow<Resource<Invoice>> = flow {
        emit(Resource.Loading)
        emit(repository.getInvoiceById(invoiceId))
    }
}

class CreateInvoiceUseCase @Inject constructor(private val repository: InvoiceRepository) {
    operator fun invoke(param: CreateInvoiceParam): Flow<Resource<Invoice>> = flow {
        emit(Resource.Loading)
        emit(repository.createInvoice(param))
    }
}

class UpdateInvoiceUseCase @Inject constructor(private val repository: InvoiceRepository) {
    operator fun invoke(param: UpdateInvoiceParam): Flow<Resource<Invoice>> = flow {
        emit(Resource.Loading)
        emit(repository.updateInvoice(param))
    }
}

class DeleteInvoiceUseCase @Inject constructor(private val repository: InvoiceRepository) {
    operator fun invoke(invoiceId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteInvoice(invoiceId))
    }
}