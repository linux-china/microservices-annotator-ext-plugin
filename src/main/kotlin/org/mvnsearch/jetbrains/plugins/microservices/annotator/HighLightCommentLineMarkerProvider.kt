package org.mvnsearch.jetbrains.plugins.microservices.annotator

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

/**
 * Microservice comment highlighter
 *
 * @author linux_china
 */
class HighLightCommentLineMarkerProvider : LineMarkerProviderDescriptor() {
    override fun getName(): String {
        return "Microservice comment highlighter";
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is PsiComment) {
            val text = element.text
            if (text.startsWith("//!") || text.startsWith("// !")) {
                return LineMarkerInfo(
                    element, element.textRange, AllIcons.Actions.Lightning,
                    {
                        text.substring(text.indexOf("!") + 1).trim()
                    },
                    null,
                    GutterIconRenderer.Alignment.LEFT
                ) {
                    "Microservice comment highlighter"
                }
            }
        }
        return null
    }
}