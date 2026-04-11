package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateTableParam
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.UpdateTableParam

interface TableRepository {
    suspend fun getTables(page: Int, size: Int): Resource<PageData<DashboardTable>>

    suspend fun getDashboardTables(page: Int, size: Int): Resource<PageData<DashboardTable>>
    
    suspend fun createTable(param: CreateTableParam): Resource<DashboardTable>
    
    suspend fun updateTable(param: UpdateTableParam): Resource<DashboardTable>
    
    suspend fun deleteTable(id: Int): Resource<String>
}