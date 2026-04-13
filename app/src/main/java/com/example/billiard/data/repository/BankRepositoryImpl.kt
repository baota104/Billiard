package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.BankApiService
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.CreateBankParam
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.domain.model.VietQr
import com.example.billiard.domain.repository.BankRepository
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor(
    private val api: BankApiService
) : BaseRepository(), BankRepository {

    override suspend fun getBanks(): Resource<List<Bank>> {
        return safeApiCall(
            apiCall = { api.getBanks() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun createBank(param: CreateBankParam): Resource<Bank> {
        return safeApiCall(
            apiCall = { api.createBank(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateBank(param: UpdateBankParam): Resource<Bank> {
        return safeApiCall(
            apiCall = { api.updateBank(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteBank(bankId: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteBank(bankId) },
            mapper = { "Xóa tài khoản ngân hàng thành công" }
        )
    }

    override suspend fun getVietQr(id: Int): Resource<VietQr> {
        return safeApiCall(
            apiCall = { api.getVietQr(id) },
            mapper = { it.toDomain() }
        )
    }
}