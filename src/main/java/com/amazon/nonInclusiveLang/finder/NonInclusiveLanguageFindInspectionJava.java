package com.amazon.nonInclusiveLang.finder;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;

import java.util.Objects;

public class NonInclusiveLanguageFindInspectionJava  extends AbstractBaseJavaLocalInspectionTool {

  public NonInclusiveLanguageFindInspectionJava() {
    super();
  }

  @Override
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }


  @Override
  public PsiElementVisitor buildVisitor(final ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      @Override
      public void visitClass(PsiClass aClass) {
        super.visitClass(aClass);

        if (containsNonInclusiveWords(aClass.getQualifiedName())) {
          holder.registerProblem(aClass.getIdentifyingElement(), NonInclusiveLanguageInspectionBundle.message("NonInclusiveLanguageInspection.has.non.inclusive.language"));
        }
      }

      @Override
      public void visitMethod(PsiMethod method) {
        super.visitMethod(method);

        if (method != null && containsNonInclusiveWords(method.getName())) {
          holder.registerProblem(method.getIdentifyingElement(), NonInclusiveLanguageInspectionBundle.message("NonInclusiveLanguageInspection.has.non.inclusive.language"));
        }
      }

      @Override
      public void visitVariable(PsiVariable variable) {
        super.visitVariable(variable);

        if (variable != null && containsNonInclusiveWords(variable.getName())) {
          holder.registerProblem(Objects.requireNonNull(variable.getIdentifyingElement()), NonInclusiveLanguageInspectionBundle.message("NonInclusiveLanguageInspection.has.non.inclusive.language"));
        }
      }

      private boolean containsNonInclusiveWords(String word) {
        return (word != null && (word.contains("black") || word.contains("white")));
      }
    };
  }
}

