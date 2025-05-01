package org.mvnsearch.jetbrains.plugins.microservices.ai

import com.intellij.openapi.project.Project
import org.jetbrains.uast.UCallExpression
import org.strangeway.msa.db.InteractionType
import org.strangeway.msa.frameworks.CallDetector
import org.strangeway.msa.frameworks.FrameworkInteraction
import org.strangeway.msa.frameworks.Interaction
import org.strangeway.msa.frameworks.hasLibraryClass

class OpenAiCallDetector : CallDetector {
    private val aiInteraction: Interaction = FrameworkInteraction(InteractionType.REQUEST, "OpenAI")
    private val vectorDBInteraction: Interaction = FrameworkInteraction(InteractionType.DATABASE, "OpenAI Vector")
    private val fileInteraction: Interaction = FrameworkInteraction(InteractionType.CLOUD_STORAGE, "OpenAI Files")
    private val openAiStubInterfaces = listOf(
        "com.openai.services.blocking.CompletionService",
        "com.openai.services.blocking.CompletionService.WithRawResponse",
        "com.openai.services.async.CompletionServiceAsync",
        "com.openai.services.async.CompletionServiceAsync.WithRawResponse",
        "com.openai.services.blocking.chat.ChatCompletionService",
        "com.openai.services.blocking.CompletionService.WithRawResponse",
        "com.openai.services.async.chat.ChatCompletionServiceAsync",
        "com.openai.services.async.chat.ChatCompletionServiceAsync.WithRawResponse",
        "com.openai.services.blocking.chat.completions.MessageService",
        "com.openai.services.blocking.chat.ChatCompletionService.WithRawResponse",
        "com.openai.services.async.chat.completions.MessageServiceAsync",
        "com.openai.services.async.chat.completions.MessageServiceAsync.WithRawResponse",
        "com.openai.services.blocking.EmbeddingService",
        "com.openai.services.blocking.EmbeddingService.WithRawResponse",
        "com.openai.services.async.EmbeddingServiceAsync",
        "com.openai.services.async.EmbeddingServiceAsync.WithRawResponse",
        "com.openai.services.blocking.ImageService",
        "com.openai.services.blocking.ImageService.WithRawResponse",
        "com.openai.services.async.ImageServiceAsync",
        "com.openai.services.async.ImageServiceAsync.WithRawResponse",
        "com.openai.services.blocking.audio.TranscriptionService",
        "com.openai.services.blocking.audio.TranscriptionService.WithRawResponse",
        "com.openai.services.async.audio.TranscriptionServiceAsync",
        "com.openai.services.async.audio.TranscriptionServiceAsync.WithRawResponse",
        "com.openai.services.blocking.audio.TranslationService",
        "com.openai.services.blocking.audio.TranslationService.WithRawResponse",
        "com.openai.services.async.audio.TranslationServiceAsync",
        "com.openai.services.async.audio.TranslationServiceAsync.WithRawResponse",
        "com.openai.services.blocking.audio.SpeechService",
        "com.openai.services.blocking.audio.SpeechService.WithRawResponse",
        "com.openai.services.async.audio.SpeechServiceAsync",
        "com.openai.services.async.audio.SpeechServiceAsync.WithRawResponse",
        "com.openai.services.blocking.ModelService",
        "com.openai.services.blocking.ModelService.WithRawResponse",
        "com.openai.services.async.ModelServiceAsync",
        "com.openai.services.async.ModelServiceAsync.WithRawResponse",
    )
    private val openAiCallStubMethods = listOf(
        "completions", "stream",
        "create", "createStreaming", "createVariation", "generate",
        "retrieve"
    )

    private val openAiFileServiceStubInterfaces = listOf(
        "com.openai.services.blocking.vectorstores.FileService",
        "com.openai.services.blocking.vectorstores.FileService.WithRawResponse",
        "com.openai.services.async.vectorstores.FileServiceAsync",
        "com.openai.services.async.vectorstores.FileServiceAsync.WithRawResponse",
        "com.openai.services.blocking.vectorstores.FileBatchService",
        "com.openai.services.blocking.vectorstores.FileBatchService.WithRawResponse",
        "com.openai.services.async.vectorstores.FileBatchServiceAsync",
        "com.openai.services.async.vectorstores.FileBatchServiceAsync.WithRawResponse",
    )
    private val openAiFileServiceCallStubMethods = listOf(
        "create", "update", "edit", "cancel", "delete", "content", "list", "listFies",
    )

    private val openAiVectorServiceStubInterfaces = listOf(
        "com.openai.services.blocking.vectorstores.VectorStoreService",
        "com.openai.services.blocking.vectorstores.VectorStoreService.WithRawResponse",
        "com.openai.services.async.vectorstores.VectorStoreServiceAsync",
        "com.openai.services.async.vectorstores.VectorStoreServiceAsync.WithRawResponse",
    )
    private val openAiVectorServiceCallStubMethods = listOf(
        "create", "update", "retrieve", "list", "delete", "search",
    )

    override fun getCallInteraction(project: Project, uCall: UCallExpression): Interaction? {
        val psiMethod = uCall.resolve()
        if (psiMethod != null) {

            val psiClass = psiMethod.containingClass
            if (psiClass != null) {
                val psiClassFullName = psiClass.qualifiedName!!
                if (isOpenAiStub(psiClassFullName)) {
                    if (openAiCallStubMethods.contains(psiMethod.name)) {
                        return aiInteraction
                    }
                } else if (openAiFileServiceStubInterfaces.contains(psiClassFullName)) {
                    if (openAiFileServiceCallStubMethods.contains(psiMethod.name)) {
                        return fileInteraction
                    }
                } else if (openAiVectorServiceStubInterfaces.contains(psiClassFullName)) {
                    if (openAiVectorServiceCallStubMethods.contains(psiMethod.name)) {
                        return vectorDBInteraction
                    }
                }
            }
        }
        return null
    }

    override fun isAvailable(project: Project): Boolean {
        return hasLibraryClass(project, "com.openai.client.OpenAIClient")
    }

    private fun isOpenAiStub(classFullName: String): Boolean {
        return openAiStubInterfaces.contains(classFullName)
    }
}
