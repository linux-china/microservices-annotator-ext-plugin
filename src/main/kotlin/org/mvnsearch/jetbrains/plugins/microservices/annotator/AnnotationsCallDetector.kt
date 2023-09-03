package org.mvnsearch.jetbrains.plugins.microservices.annotator

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTypesUtil
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.tryResolveNamed
import org.strangeway.msa.db.InteractionType
import org.strangeway.msa.frameworks.CallDetector
import org.strangeway.msa.frameworks.FrameworkInteraction
import org.strangeway.msa.frameworks.Interaction
import org.strangeway.msa.frameworks.hasLibraryClass

class AnnotationsCallDetector : CallDetector {
    companion object {
        val GLOBAL_ANNOTATIONS = mapOf<String, Interaction>(
            "org.mvnsearch.microservices.annotator.Broadcast" to FrameworkInteraction(
                InteractionType.BROADCAST,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.CloudStorage" to FrameworkInteraction(
                InteractionType.CLOUD_STORAGE,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.DataAccess" to FrameworkInteraction(
                InteractionType.DATABASE,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.DatabaseAccess" to FrameworkInteraction(
                InteractionType.DATABASE,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.ExternalProcess" to FrameworkInteraction(
                InteractionType.RUN_PROCESS,
                "unknown"
            ),
            // todo change icon in the future
            "org.mvnsearch.microservices.annotator.ForeignAccess" to FrameworkInteraction(
                InteractionType.RUN_PROCESS,
                "jni/ffi"
            ),
            "org.mvnsearch.microservices.annotator.FileOps" to FrameworkInteraction(
                InteractionType.FILESYSTEM,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.IORead" to FrameworkInteraction(InteractionType.IO_READ, "unknown"),
            "org.mvnsearch.microservices.annotator.IOWrite" to FrameworkInteraction(
                InteractionType.IO_WRITE,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.MessageReceive" to FrameworkInteraction(
                InteractionType.MESSAGE_RECEIVE,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.MessageSend" to FrameworkInteraction(
                InteractionType.MESSAGE_SEND,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.RemoteAccess" to FrameworkInteraction(
                InteractionType.REQUEST,
                "unknown"
            ),
            "org.mvnsearch.microservices.annotator.Streaming" to FrameworkInteraction(
                InteractionType.STREAMING,
                "unknown"
            ),
        )
    }

    override fun getCallInteraction(project: Project, uCall: UCallExpression): Interaction? {
        val psiMethod = uCall.resolve()
        if (psiMethod != null) {
            val psiClass = psiMethod.containingClass
            if (psiClass != null) {
                val annotations = mutableListOf<PsiAnnotation>()
                //resolve method annotations
                if (AnnotationUtil.isAnnotated(psiMethod, GLOBAL_ANNOTATIONS.keys, 0)) {
                    annotations.addAll(psiMethod.annotations)
                }
                // resolve declared class/interface annotations
                if (annotations.isEmpty()) {
                    if (AnnotationUtil.isAnnotated(psiClass, GLOBAL_ANNOTATIONS.keys, 0)) {
                        annotations.addAll(psiClass.annotations)
                    }
                }
                // resolve receiver class/interface annotations
                if (annotations.isEmpty()) {
                    PsiTypesUtil.getPsiClass(uCall.receiverType)?.let { receiverPsiClass ->
                        if (AnnotationUtil.isAnnotated(receiverPsiClass, GLOBAL_ANNOTATIONS.keys, 0)) {
                            annotations.addAll(receiverPsiClass.annotations)
                        }
                    }
                }
                if (annotations.isNotEmpty()) {
                    for (annotation in annotations) {
                        if (GLOBAL_ANNOTATIONS.contains(annotation.qualifiedName)) {
                            return GLOBAL_ANNOTATIONS[annotation.qualifiedName]
                        }
                    }
                }
            }
            // resolve receiver declare with microservices annotations
            val declare = uCall.receiver?.tryResolveNamed()
            if (declare != null) {
                if (declare is PsiField || declare is PsiParameter || declare is PsiVariable) {
                    if (declare is PsiModifierListOwner) {
                        if (AnnotationUtil.isAnnotated(declare, GLOBAL_ANNOTATIONS.keys, 0)) {
                            val annotations = declare.annotations
                            for (annotation in annotations) {
                                if (GLOBAL_ANNOTATIONS.contains(annotation.qualifiedName)) {
                                    return GLOBAL_ANNOTATIONS[annotation.qualifiedName]
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    override fun isAvailable(project: Project): Boolean {
        return hasLibraryClass(project, "org.mvnsearch.microservices.annotator.RemoteAccess")
    }


}