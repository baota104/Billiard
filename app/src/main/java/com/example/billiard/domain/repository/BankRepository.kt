package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.CreateBankParam
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.domain.model.VietQr

interface BankRepository {
    suspend fun getBanks(): Resource<List<Bank>>
    suspend fun createBank(param: CreateBankParam): Resource<Bank>
    suspend fun updateBank(param: UpdateBankParam): Resource<Bank>
    suspend fun deleteBank(bankId: Int): Resource<String>
    suspend fun getVietQr(id: Int): Resource<VietQr>
    suspend fun getActive(): Resource<Bank>
}

