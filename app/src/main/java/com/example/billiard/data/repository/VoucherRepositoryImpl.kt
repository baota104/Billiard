package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.VoucherApiService
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.domain.model.Voucher
import com.example.billiard.domain.repository.VoucherRepository
import javax.inject.Inject

class VoucherRepositoryImpl @Inject constructor(
    private val api: VoucherApiService
) : BaseRepository(), VoucherRepository {

    override suspend fun getActiveVouchers(): Resource<List<Voucher>> {
        return safeApiCall(
            apiCall = { api.getActiveVouchers() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun getVoucherById(voucherId: Long): Resource<Voucher> {
        return safeApiCall(
            apiCall = { api.getVoucherById(voucherId) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun createVoucher(param: CreateVoucherParam): Resource<Voucher> {
        return safeApiCall(
            apiCall = { api.createVoucher(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateVoucher(param: UpdateVoucherParam): Resource<Voucher> {
        return safeApiCall(
            apiCall = { api.updateVoucher(param.id, param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun validateVouchers(totalBillAmount: Double): Resource<List<Voucher>> {
        return safeApiCall(
            apiCall = { api.validateVouchers(totalBillAmount) },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun deleteVoucher(voucherId: Long): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteVoucher(voucherId) },
            mapper = { it ?: "Xóa Voucher thành công" }
        )
    }
}