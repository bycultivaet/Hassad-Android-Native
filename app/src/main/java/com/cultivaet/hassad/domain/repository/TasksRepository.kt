package com.cultivaet.hassad.domain.repository

import com.cultivaet.hassad.core.source.remote.Resource
import com.cultivaet.hassad.domain.model.remote.responses.Task
import com.cultivaet.hassad.domain.model.remote.responses.UpdateStatus
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    suspend fun userId(): Flow<Int?>

    suspend fun getAllTasksById(id: Int): Resource<List<Task>>

    suspend fun updateTaskStatus(
        facilitatorId: Int,
        taskId: Int,
        status: Boolean
    ): Resource<UpdateStatus>
}