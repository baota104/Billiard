package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.domain.model.Voucher

interface VoucherRepository {
    suspend fun getActiveVouchers(): Resource<List<Voucher>>
    suspend fun getVoucherById(voucherId: Long): Resource<Voucher>
    suspend fun createVoucher(param: CreateVoucherParam): Resource<Voucher>
    suspend fun updateVoucher(param: UpdateVoucherParam): Resource<Voucher>
    suspend fun validateVouchers(totalBillAmount: Double): Resource<List<Voucher>>
    suspend fun deleteVoucher(voucherId: Long): Resource<String>
}