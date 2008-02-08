package org.eclipse.imp.lpg.actions;

import java.util.ResourceBundle;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.IWorkspaceModel;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.IFactDescriptor;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class DumpCallGraphAction extends TextEditorAction {
    public interface ISourceEntityContext extends IFactContext {
        ISourceEntity getEntity();
    }

    public static final class WorkspaceContext implements ISourceEntityContext {
        private IWorkspaceModel  fWorkspace;

        public WorkspaceContext() {
            fWorkspace= ModelFactory.getModelRoot();
        }

        public IWorkspaceModel getWorkspace() {
            return fWorkspace;
        }

        public ISourceEntity getEntity() {
            return fWorkspace;
        }
    }

    public static final class ProjectContext implements ISourceEntityContext {
        private ISourceProject fProject;

        public ProjectContext(ISourceProject proj) {
            fProject= proj;
        }

        public ISourceProject getProject() {
            return fProject;
        }

        public ISourceEntity getEntity() {
            return fProject;
        }
    }

    public static final class FolderContext implements ISourceEntityContext {
        private ISourceFolder fFolder;

        public FolderContext(ISourceFolder folder) {
            fFolder= folder;
        }

        public ISourceFolder getFolder() {
            return fFolder;
        }

        public ISourceEntity getEntity() {
            return fFolder;
        }
    }

    public static final class CompilationUnitContext implements ISourceEntityContext {
        private ICompilationUnit fUnit;

        public CompilationUnitContext(ICompilationUnit unit) {
            fUnit= unit;
        }

        public ICompilationUnit getCompilationUnit() {
            return fUnit;
        }

        public ISourceEntity getEntity() {
            return fUnit;
        }
    }

    private final TypeFactory tf= TypeFactory.getInstance();
    public final Type LPGNonterminalType= tf.namedType("org.lpg.nonterminal", TypeFactory.getInstance().stringType());

    public DumpCallGraphAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
        super(bundle, prefix, editor);
    }

    public DumpCallGraphAction(ResourceBundle bundle, String prefix, ITextEditor editor, int style) {
        super(bundle, prefix, editor, style);

        final UniversalEditor ue= (UniversalEditor) editor;

        FactBase.getInstance().getRelation(new IFactKey() {
            public IFactContext getContext() {
                return new CompilationUnitContext(ue.getParseController().getCompilationUnit());
            }

            public IFactDescriptor getDescriptor() {
                return new IFactDescriptor() {
                    public String getName() {
                        return "LPG Call Graph";
                    }
                    public Type getType() {
                        return tf.relTypeOf(LPGNonterminalType, LPGNonterminalType);
                    }
                };
            } });
    }
}
