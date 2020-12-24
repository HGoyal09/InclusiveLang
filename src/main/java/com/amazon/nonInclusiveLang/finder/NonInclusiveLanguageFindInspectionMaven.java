package com.amazon.nonInclusiveLang.finder;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.MavenDomProjectProcessorUtils;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;

import java.util.List;

public class NonInclusiveLanguageFindInspectionMaven extends DomElementsInspection<MavenDomProjectModel> {

  public NonInclusiveLanguageFindInspectionMaven() {
    super(MavenDomProjectModel.class);
  }

  @Override
  public void checkFileElement(DomFileElement<MavenDomProjectModel> domFileElement, DomElementAnnotationHolder holder) {
    MavenDomProjectModel pomModel = domFileElement.getRootElement();
    List<MavenDomDependency> dependencies = pomModel.getDependencies().getDependencies();
    GenericDomValue<String> name = pomModel.getName();
    GenericDomValue<String> artifactId = pomModel.getArtifactId();


    Processor<MavenDomProjectModel> processor = new Processor<MavenDomProjectModel>() {
      @Override
      public boolean process(MavenDomProjectModel mavenDomProjectModel) {
        for (MavenDomDependency dependency : dependencies) {
          String groupId = dependency.getGroupId().getStringValue();
          String artifactId = dependency.getArtifactId().getStringValue();

          if (containsNonInclusiveWords(groupId) || containsNonInclusiveWords(artifactId)) {
            System.out.println("Non inclusive language detected. Groupid = " + groupId + " artifact id = " + artifactId);
            addDependencyProblem(dependency, holder);
          }
        }

        checkForNonInclusiveWords(name, holder, pomModel);
        checkForNonInclusiveWords(artifactId, holder, pomModel);
        return false;
      }
    };

    MavenDomProjectProcessorUtils.processChildrenRecursively(pomModel, processor);
    MavenDomProjectProcessorUtils.processParentProjects(pomModel, processor);

  }

  private void checkForNonInclusiveWords(GenericDomValue<String> word, DomElementAnnotationHolder holder, MavenDomProjectModel pomModel) {
    String stringValue = word.getStringValue();
    if (containsNonInclusiveWords(stringValue)) {
      addGenericProblem(word, holder);
    }
  }


  private boolean containsNonInclusiveWords(String word) {
    return (word != null && (word.contains("black") || word.contains("white")));
  }


  private static void addGenericProblem(@NotNull GenericDomValue<String> name,
                                           @NotNull DomElementAnnotationHolder holder) {


    holder.createProblem(name, HighlightSeverity.ERROR, NonInclusiveLanguageInspectionBundle.message("NonInclusiveLanguageInspection.has.non.inclusive.language"));

  }

  private static void addDependencyProblem(@NotNull MavenDomDependency dependency,
                                 @NotNull DomElementAnnotationHolder holder) {

    holder.createProblem(dependency, HighlightSeverity.ERROR, NonInclusiveLanguageInspectionBundle.message("NonInclusiveLanguageInspection.has.non.inclusive.language"));

  }
}
