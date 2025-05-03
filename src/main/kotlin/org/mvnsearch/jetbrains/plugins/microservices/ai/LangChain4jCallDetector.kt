package org.mvnsearch.jetbrains.plugins.microservices.ai

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.project.Project
import org.jetbrains.uast.UCallExpression

import org.strangeway.msa.db.InteractionType
import org.strangeway.msa.frameworks.CallDetector
import org.strangeway.msa.frameworks.FrameworkInteraction
import org.strangeway.msa.frameworks.Interaction
import org.strangeway.msa.frameworks.hasLibraryClass

class LangChain4jCallDetector : CallDetector {
    private val interaction: Interaction = FrameworkInteraction(InteractionType.REQUEST, "LangChain4j")
    private val vectorInteraction: Interaction = FrameworkInteraction(InteractionType.DATABASE, "Vector DB")
    private val aiMessageAnnotations = listOf(
        "dev.langchain4j.service.SystemMessage",
        "dev.langchain4j.service.UserMessage"
    )
    private val aiServiceAnnotations =
        listOf("dev.langchain4j.service.spring.AiService", "io.quarkiverse.langchain4j.RegisterAiService")
    private val langChainStubInterfaces =
        listOf("dev.langchain4j.model.chat.ChatModel", "dev.langchain4j.model.chat.StreamingChatModel")
    private val langChainStubMethods = listOf("chat")
    private val langChainVectorServiceStubInterfaces = listOf(
        "dev.langchain4j.store.embedding.EmbeddingStore",
    )
    private val langChainVectorServiceCallStubMethods = listOf(
        "add", "addAll", "remove", "removeAll", "search",
    )

    override fun getCallInteraction(project: Project, uCall: UCallExpression): Interaction? {
        val psiMethod = uCall.resolve()
        if (psiMethod != null) {
            if (AnnotationUtil.isAnnotated(psiMethod, aiMessageAnnotations, 0)) {
                return interaction
            } else {
                val psiClass = psiMethod.containingClass
                if (psiClass != null) {
                    val psiClassFullName = psiClass.qualifiedName!!
                    if (langChainVectorServiceStubInterfaces.contains(psiClassFullName)) {
                        if (langChainVectorServiceCallStubMethods.contains(psiMethod.name)) {
                            return vectorInteraction
                        }
                    } else if (psiClass.isInterface && AnnotationUtil.isAnnotated(psiClass, aiServiceAnnotations, 0)) {
                        return interaction
                    } else if (isLangChainServiceStub(psiClassFullName)) {
                        if (langChainStubMethods.contains(psiMethod.name)) {
                            return interaction
                        }
                    }
                }
            }
        }
        return null
    }

    override fun isAvailable(project: Project): Boolean {
        return hasLibraryClass(project, "dev.langchain4j.model.chat.ChatModel")
    }

    private fun isLangChainServiceStub(classFullName: String): Boolean {
        return langChainStubInterfaces.contains(classFullName)
    }
}

