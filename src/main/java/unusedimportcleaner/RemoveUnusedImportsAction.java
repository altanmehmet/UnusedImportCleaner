package unusedimportcleaner;

import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.psi.KtFile;

public class RemoveUnusedImportsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        // Find all Java files in the project
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        // Process Java files
        FileTypeIndex.processFiles(JavaFileType.INSTANCE, virtualFile -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof PsiJavaFile javaFile) {
                WriteCommandAction.runWriteCommandAction(project, () -> removeUnusedImports(javaFile));
            }
            return true;
        }, scope);

        // Process Kotlin files
        FileTypeIndex.processFiles(KotlinFileType.INSTANCE, virtualFile -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof KtFile kotlinFile) {
                WriteCommandAction.runWriteCommandAction(project, () -> removeUnusedImports(kotlinFile));
            }
            return true;
        }, scope);
    }

    private void removeUnusedImports(PsiJavaFile javaFile) {
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(javaFile.getProject());
        styleManager.removeRedundantImports(javaFile);
    }

    private void removeUnusedImports(KtFile ktFile) {
        new OptimizeImportsProcessor(ktFile.getProject(), ktFile).run();
    }
}
