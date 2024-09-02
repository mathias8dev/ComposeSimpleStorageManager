package com.mathias8dev.composesimplestoragemanager.queries

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.composesimplestoragemanager.models.Apk
import com.mathias8dev.composesimplestoragemanager.models.Archive
import com.mathias8dev.composesimplestoragemanager.models.Audio
import com.mathias8dev.composesimplestoragemanager.models.Image
import com.mathias8dev.composesimplestoragemanager.models.InstalledApp
import com.mathias8dev.composesimplestoragemanager.models.MediaInfo
import com.mathias8dev.composesimplestoragemanager.models.Video
import com.mathias8dev.composesimplestoragemanager.utils.Resource
import com.mathias8dev.composesimplestoragemanager.utils.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import java.io.File


@KoinViewModel
class MediaQueryViewModel(
    private val queryMaker: QueryMaker
) : ViewModel() {
    private val _allApksResource by lazy { MutableStateFlow<Resource<List<Apk>>>(Resource.Idle()) }
    private val _allImagesResource by lazy { MutableStateFlow<Resource<List<Image>>>(Resource.Idle()) }
    private val _allVideosResource by lazy { MutableStateFlow<Resource<List<Video>>>(Resource.Idle()) }
    private val _allAudiosResource by lazy { MutableStateFlow<Resource<List<Audio>>>(Resource.Idle()) }
    private val _allDocumentsResource by lazy { MutableStateFlow<Resource<List<MediaInfo>>>(Resource.Idle()) }
    private val _allRecentFilesResource by lazy { MutableStateFlow<Resource<List<MediaInfo>>>(Resource.Idle()) }
    private val _allInstalledAppsResource by lazy { MutableStateFlow<Resource<List<InstalledApp>>>(Resource.Idle()) }
    private val _allArchivesResource by lazy { MutableStateFlow<Resource<List<Archive>>>(Resource.Idle()) }
    private val _allMediaResource by lazy { MutableStateFlow<Resource<List<MediaInfo>>>(Resource.Idle()) }

    val allApksResource by lazy { _allApksResource.asStateFlow() }
    val allImagesResource by lazy { _allImagesResource.asStateFlow() }
    val allVideosResource by lazy { _allVideosResource.asStateFlow() }
    val allAudiosResource by lazy { _allAudiosResource.asStateFlow() }
    val allDocumentsResource by lazy { _allDocumentsResource.asStateFlow() }
    val allRecentFilesResource by lazy { _allRecentFilesResource.asStateFlow() }
    val allInstalledAppsResource by lazy { _allInstalledAppsResource.asStateFlow() }
    val allArchivesResource by lazy { _allArchivesResource.asStateFlow() }
    val allMediaResource by lazy { _allMediaResource.asStateFlow() }

    var allApksFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allImagesFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allVideosFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allAudiosFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allDocumentsFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allRecentFilesFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allArchivesFiles = mutableStateOf<List<File>>(emptyList())
        private set

    var allMediaFiles = mutableStateOf<List<File>>(emptyList())
        private set

    fun queryAllFiles(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allMediaResource.value.isSuccess() && !reload) return@launch
            _allMediaResource.value = Resource.Loading()
            val result = queryMaker.queryAllFiles()
            allMediaFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allMediaResource.value = Resource.Success(result)
        }
    }

    fun queryAllApks(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allApksResource.value.isSuccess() && !reload) return@launch
            _allApksResource.value = Resource.Loading()
            val result = queryMaker.queryAllApks()
            allApksFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allApksResource.value = Resource.Success(result)
        }
    }

    fun queryAllImages(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allImagesResource.value.isSuccess() && !reload) return@launch
            _allImagesResource.value = Resource.Loading()
            val result = queryMaker.queryAllImages()
            allImagesFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allImagesResource.value = Resource.Success(result)
        }
    }

    fun queryAllVideos(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allVideosResource.value.isSuccess() && !reload) return@launch
            _allVideosResource.value = Resource.Loading()
            val result = queryMaker.queryAllVideos()
            allVideosFiles.value = result.mapNotNull { it.externalContentUri?.toString()?.let { it1 -> File(it1) } }
            _allVideosResource.value = Resource.Success(result)
        }
    }

    fun queryAllAudios(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allAudiosResource.value.isSuccess() && !reload) return@launch
            _allAudiosResource.value = Resource.Loading()
            val result = queryMaker.queryAllAudios()
            allAudiosFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allAudiosResource.value = Resource.Success(result)
        }
    }

    fun queryAllDocuments(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allDocumentsResource.value.isSuccess() && !reload) return@launch
            _allDocumentsResource.value = Resource.Loading()
            val result = queryMaker.queryAllDocuments()
            allDocumentsFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allDocumentsResource.value = Resource.Success(result)
        }
    }

    fun queryAllRecentFiles(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allRecentFilesResource.value.isSuccess() && !reload) return@launch
            _allRecentFilesResource.value = Resource.Loading()
            val result = queryMaker.queryAllRecentFiles()
            allRecentFilesFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allRecentFilesResource.value = Resource.Success(result)
        }
    }

    fun queryInstalledApps(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allInstalledAppsResource.value.isSuccess() && !reload) return@launch
            _allInstalledAppsResource.value = Resource.Loading()
            val result = queryMaker.queryInstalledApps()
            _allInstalledAppsResource.value = Resource.Success(result)
        }
    }

    fun queryAllArchives(reload: Boolean = false) {
        viewModelScope.launch {
            if (_allArchivesResource.value.isSuccess() && !reload) return@launch
            _allArchivesResource.value = Resource.Loading()
            val result = queryMaker.queryAllArchives()
            allArchivesFiles.value = result.map { it.externalContentUri.toString().let { it1 -> File(it1) } }
            _allArchivesResource.value = Resource.Success(result)
        }
    }

    fun queryAllMedia(reload: Boolean = false) {
        queryAllApks(reload)
        queryAllImages(reload)
        queryAllVideos(reload)
        queryAllAudios(reload)
        queryAllDocuments(reload)
        queryAllRecentFiles(reload)
        queryInstalledApps(reload)
        queryAllArchives(reload)
    }

}

@Single
class QueryMaker(
    private val context: Context
) {

    fun queryAllApks() = queryAllApks(context)
    fun queryAllImages() = queryAllImages(context)
    fun queryAllVideos() = queryAllVideos(context)
    fun queryAllAudios() = queryAllAudios(context)
    fun queryAllDocuments() = queryAllDocuments(context)
    fun queryAllRecentFiles() = queryRecentFiles(context)
    fun queryInstalledApps() = queryInstalledApps(context)
    fun queryAllArchives() = queryAllArchives(context)
    fun queryAllFiles() = queryAllMedias(context)
}