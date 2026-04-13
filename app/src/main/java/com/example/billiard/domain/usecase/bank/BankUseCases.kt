package com.example.billiard.domain.usecase.bank

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.CreateBankParam
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.domain.model.VietQr
import com.example.billiard.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBanksUseCase @Inject constructor(private val repository: BankRepository) {
    operator fun invoke(): Flow<Resource<List<Bank>>> = flow {
        emit(Resource.Loading)
        emit(repository.getBanks())
    }
}

class CreateBankUseCase @Inject constructor(private val repository: BankRepository) {
    operator fun invoke(param: CreateBankParam): Flow<Resource<Bank>> = flow {
        emit(Resource.Loading)
        emit(repository.createBank(param))
    }
}

class UpdateBankUseCase @Inject constructor(private val repository: BankRepository) {
    operator fun invoke(param: UpdateBankParam): Flow<Resource<Bank>> = flow {
        emit(Resource.Loading)
        emit(repository.updateBank(param))
    }
}

class DeleteBankUseCase @Inject constructor(private val repository: BankRepository) {
    operator fun invoke(bankId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteBank(bankId))
    }
}

class GetVietQrUseCase @Inject constructor(private val repository: BankRepository) {
    operator fun invoke(id: Int): Flow<Resource<VietQr>> = flow {
        emit(Resource.Loading)
        emit(repository.getVietQr(id))
    }
}