package org.mvnsearch.jetbrains.plugins.microservices.ai

import com.intellij.openapi.project.Project
import org.jetbrains.uast.UCallExpression
import org.strangeway.msa.db.InteractionType
import org.strangeway.msa.frameworks.CallDetector
import org.strangeway.msa.frameworks.FrameworkInteraction
import org.strangeway.msa.frameworks.Interaction
import org.strangeway.msa.frameworks.hasLibraryClass

class SpringAiCallDetector : CallDetector {
    private val aiInteraction: Interaction = FrameworkInteraction(InteractionType.REQUEST, "Spring AI")
    private val vectorInteraction: Interaction = FrameworkInteraction(InteractionType.DATABASE, "Vector DB")
    private val springAiStubInterfaces = listOf(
        "org.springframework.ai.chat.model.StreamingChatModel",
        "org.springframework.ai.chat.model.ChatModel",
        "org.springframework.ai.model.StreamingModel",
        "org.springframework.ai.ollama.OllamaChatModel",
        "org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec",
        "org.springframework.ai.embedding.EmbeddingModel",
        "org.springframework.ai.image.ImageModel"
    )
    private val vectorDbStubInterfaces = listOf(
        "org.springframework.ai.vectorstore.VectorStore",
        "org.springframework.ai.vectorstore.SimpleVectorStore"
    )
    private val aiCallStubMethods = listOf("call", "stream", "embed")
    private val vectorCallStubMethods = listOf("add", "delete", "accept", "similaritySearch")
    override fun getCallInteraction(project: Project, uCall: UCallExpression): Interaction? {
        val psiMethod = uCall.resolve()
        if (psiMethod != null) {
            val psiClass = psiMethod.containingClass
            if (psiClass != null) {
                val psiClassFullName = psiClass.qualifiedName!!
                if (isSpringAiStub(psiClassFullName)) {
                    if (aiCallStubMethods.contains(psiMethod.name)) {
                        return aiInteraction
                    }
                } else if (vectorDbStubInterfaces.contains(psiClassFullName)) {
                    if (vectorCallStubMethods.contains(psiMethod.name)) {
                        return vectorInteraction
                    }
                }
            }
        }
        return null
    }

    override fun isAvailable(project: Project): Boolean {
        return hasLibraryClass(project, "org.springframework.ai.chat.client.ChatClient")
    }

    private fun isSpringAiStub(classFullName: String): Boolean {
        return springAiStubInterfaces.contains(classFullName)
    }
}
