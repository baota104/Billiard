package com.example.billiard.domain.usecase.pricelist

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.PriceList
import com.example.billiard.domain.model.UpdatePriceListParam
import com.example.billiard.domain.repository.PriceListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPriceListsUseCase @Inject constructor(private val repository: PriceListRepository) {
    operator fun invoke(): Flow<Resource<List<PriceList>>> = flow {
        emit(Resource.Loading)
        emit(repository.getPriceLists())
    }
}

class CreatePriceListUseCase @Inject constructor(private val repository: PriceListRepository) {
    operator fun invoke(param: CreatePriceListParam): Flow<Resource<PriceList>> = flow {
        emit(Resource.Loading)
        emit(repository.createPriceList(param))
    }
}

class UpdatePriceListUseCase @Inject constructor(private val repository: PriceListRepository) {
    operator fun invoke(param: UpdatePriceListParam): Flow<Resource<PriceList>> = flow {
        emit(Resource.Loading)
        emit(repository.updatePriceList(param))
    }
}

class DeletePriceListUseCase @Inject constructor(private val repository: PriceListRepository) {
    operator fun invoke(id: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deletePriceList(id))
    }
}