package org.epic.debug.db;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.epic.debug.PerlDebugPlugin;

/**
 * The "step into" DebugCommand. Lets the debugger continue execution
 * until either a breakpoint (known to EPIC) is reached, the step
 * finishes, or the debugger terminates. 
 * 
 * @author jploski
 */
class StepIntoCommand extends DebugCommand
{
    public StepIntoCommand(PerlDebugThread thread)
    {
        super(thread);
    }

    protected int runImpl() throws IOException
    {
        try
        {
            IPPosition startIP = getDB().getCurrentIP();            
    
            getDB().stepInto();
            maybeSkipStringEval(startIP);
            return getThread().isBreakpointReached()
                ? BREAKPOINT : STEP_END;
        }
        catch (CoreException e)
        {
            PerlDebugPlugin.log(e);
            return STEP_END;
        }
        catch (DebuggerInterface.SessionTerminatedException e)
        {
            return TERMINATED;
        }
    }
}
