package com.toqsoft.habittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toqsoft.habittracker.data.dao.TaskDao
import com.toqsoft.habittracker.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    // Insert
    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    // Update (required for editing)
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    // Delete (optional)
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    // Load task by ID (for editing)
    fun getTaskById(taskId: Int?): Flow<TaskEntity?> {
        return if (taskId == null) {
            kotlinx.coroutines.flow.flow { emit(null) }
        } else {
            taskDao.getTaskById(taskId)
        }
    }
}
