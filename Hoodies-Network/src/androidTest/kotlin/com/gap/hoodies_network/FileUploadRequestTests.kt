package com.gap.hoodies_network


import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.config.CLIENT_ID
import com.gap.hoodies_network.config.CLIENT_ID_VALUE
import com.gap.hoodies_network.config.CLIENT_OS
import com.gap.hoodies_network.config.CLIENT_OS_VALUE
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.testObjects.testInterceptor
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.*
import java.util.*

class FileUploadRequestTests {

    private val baseURL = "http://localhost:6969/"

    private val interceptor = testInterceptor(InstrumentationRegistry.getInstrumentation().context)
    private val defaultHeaders = hashMapOf(
        CLIENT_ID to CLIENT_ID_VALUE,
        CLIENT_OS to CLIENT_OS_VALUE,
        "Test" to "File Upload",
        "Library" to "Network"
    )
    val context = InstrumentationRegistry.getInstrumentation().context
    val fileDir = context.filesDir.absoluteFile.toString()
    val file1 = File(fileDir)
    val file2 = File(fileDir)
    val file3 = File(fileDir)
    val fileFirst = File(file1, "a1.txt").apply {
        writeText("First file")
    }
    val fileSecond = File(file2, "a2.txt").apply {
        writeText("Second file")
    }
    val fileThird = File(file3, "a3.txt").apply {
        writeText("Third file")
    }

    @Before
    fun startMockWebServer() {
        ServerManager.setup(InstrumentationRegistry.getInstrumentation().context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun fileTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder()
                .baseUrl(baseURL)
                .addInterceptor(interceptor)
                .build()

            when (val result = client.postMultipartFiles<String>("post", arrayListOf(fileFirst), defaultHeaders )) {
                is Success -> {
                    val files = JSONObject(result.value).getJSONObject("files")
                    assertEquals(files.getString("file0"), "First file")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun twoFilesTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder()
                .baseUrl(baseURL)
                .addInterceptor(interceptor)
                .build()


            when (val result = client.postMultipartFiles<String>("post", arrayListOf(fileFirst, fileSecond), defaultHeaders )) {
                is Success -> {
                    val files = JSONObject(result.value).getJSONObject("files")
                    assertEquals(files.getString("file0"), "First file")
                    assertEquals(files.getString("file1"), "Second file")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun threeFilesTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder()
                .baseUrl(baseURL)
                .addInterceptor(interceptor)
                .build()


            when (val result = client.postMultipartFiles<String>("post", arrayListOf(fileFirst, fileSecond, fileThird), defaultHeaders )) {
                is Success -> {
                    val files = JSONObject(result.value).getJSONObject("files")
                    assertEquals(files.getString("file0"), "First file")
                    assertEquals(files.getString("file1"), "Second file")
                    assertEquals(files.getString("file2"), "Third file")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    }
