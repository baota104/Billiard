package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.remote.api.TableApiService
import com.example.billiard.domain.model.*
import com.example.billiard.domain.repository.TableRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class TableRepositoryImpl @Inject constructor(
    private val api: TableApiService
) : BaseRepository(), TableRepository {

    override suspend fun getTables(page: Int, size: Int): Resource<PageData<DashboardTable>> {
        return safeApiCall(
            apiCall = { api.getTables(page, size, listOf("id,asc")) },
            mapper = { pageDto ->
                PageData(
                    content = pageDto.content.map { it.toDomain() },
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    currentPage = pageDto.number,
                    isLastPage = pageDto.last ?: true
                )
            }
        )
    }

    override suspend fun getDashboardTables(page: Int, size: Int): Resource<PageData<DashboardTable>> {
        return safeApiCall(
            apiCall = { api.getDashboardTables(page, size, listOf("id,asc")) },
            mapper = { pageDto ->
                PageData(
                    content = pageDto.content.map { it.toDomain() },
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    currentPage = pageDto.number,
                    isLastPage = pageDto.last ?: true
                )
            }
        )
    }

    override suspend fun createTable(param: CreateTableParam): Resource<DashboardTable> {
        val nameBody = param.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusBody = param.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val typeBody = param.tableType.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = param.imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
        }

        return safeApiCall(
            apiCall = { api.createTable(nameBody, statusBody, typeBody, imagePart) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateTable(param: UpdateTableParam): Resource<DashboardTable> {
        val nameBody = param.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusBody = param.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val typeBody = param.tableType.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = param.imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
        }

        return safeApiCall(
            apiCall = { api.updateTable(param.id, nameBody, statusBody, typeBody, imagePart) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteTable(id: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteTable(id) },
            mapper = { "Xóa bàn thành công" }
        )
    }
}