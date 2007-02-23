package org.eclipse.safari.jikespg.views;

import org.eclipse.safari.jikespg.actions.ParseNonTerminalAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class GrammarInteractionView extends ViewPart {
    private Text text;

    private static GrammarInteractionView singleton;

    public GrammarInteractionView() {
	singleton= this;
    }

    public static GrammarInteractionView getDefault() {
	openView();
	reset();
	return singleton;
    }

    public void createPartControl(Composite parent) {
	text= new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	text.setForeground(new Color(parent.getDisplay(), 0, 0, 255));
	text.setFont(new Font(parent.getDisplay(), "Helvetica", 10, SWT.NORMAL));
    }

    public void setFocus() {
	text.setFocus();
    }

    public Text getText() {
	return text;
    }

    private static void openView() {
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		try {
		    openViewDirect();
		} catch (Throwable e) {
		    e.printStackTrace();
		}
	    }

	});
    }

    static void openViewDirect() throws PartInitException {
	IWorkbench wb= PlatformUI.getWorkbench();
	IWorkbenchWindow win= wb.getActiveWorkbenchWindow();
	if (win == null)
	    return;
	IWorkbenchPage page= win.getActivePage();
	try {
	    page.showView("org.jikespg.uide.views.GrammarInteractionView");
	} catch (Exception e) {
	}
    }

    public static void println(final String line) {
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		try {
		    if (singleton == null)
			return;
		    if (singleton.text.isDisposed())
			openViewDirect();
		    singleton.text.append(line);
		    singleton.text.append("\n");
		} catch (Throwable e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public static void reset() {
	Display.getDefault().asyncExec(new Runnable() {
	    public void run() {
		try {
		    if (singleton == null)
			return;
		    if (singleton.text.isDisposed())
			openViewDirect();
		    singleton.text.setText("");
		} catch (Throwable e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public interface InputHandler {
	public void handleInput(String s);
    }

    public void addInputListener(InputHandler handler) {
	throw new IllegalArgumentException("addInputListener() is unimplemented");
    }
}
