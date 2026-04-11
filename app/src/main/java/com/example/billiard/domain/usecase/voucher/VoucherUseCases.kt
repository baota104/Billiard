package com.example.billiard.domain.usecase.voucher

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.domain.model.Voucher
import com.example.billiard.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// Gộp các UseCase của Voucher vào cùng một file để dễ quản lý (vì chúng khá nhỏ và giống nhau)

class GetActiveVouchersUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(): Flow<Resource<List<Voucher>>> = flow {
        emit(Resource.Loading)
        emit(repository.getActiveVouchers())
    }
}

class GetVoucherByIdUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(voucherId: Long): Flow<Resource<Voucher>> = flow {
        emit(Resource.Loading)
        emit(repository.getVoucherById(voucherId))
    }
}

class CreateVoucherUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(param: CreateVoucherParam): Flow<Resource<Voucher>> = flow {
        emit(Resource.Loading)
        emit(repository.createVoucher(param))
    }
}

class UpdateVoucherUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(param: UpdateVoucherParam): Flow<Resource<Voucher>> = flow {
        emit(Resource.Loading)
        emit(repository.updateVoucher(param))
    }
}

class ValidateVouchersUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(totalBillAmount: Double): Flow<Resource<List<Voucher>>> = flow {
        emit(Resource.Loading)
        emit(repository.validateVouchers(totalBillAmount))
    }
}

class DeleteVoucherUseCase @Inject constructor(private val repository: VoucherRepository) {
    operator fun invoke(voucherId: Long): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteVoucher(voucherId))
    }
}