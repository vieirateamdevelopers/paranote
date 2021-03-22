package br.com.vieirateam.paranote.viewmodel

import kotlinx.coroutines.Job

interface ViewModelScope<T> {
    fun insert(item: T) : Job
    fun update(item: T) : Job
    fun delete(item: T) : Job
}