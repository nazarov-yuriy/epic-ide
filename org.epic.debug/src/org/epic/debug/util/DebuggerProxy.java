/*
 * Created on 03.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
 
package org.epic.debug.util;


import java.io.IOException;
import java.io.PrintWriter;


import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.debug.internal.core.InputStreamMonitor;

import org.epic.debug.PerlDB;
import org.epic.debug.PerlLaunchConfigurationConstants;




/**
 * @author ST
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DebuggerProxy  extends PlatformObject implements IProcess, ITerminate,IStreamsProxy {
	private OutputStreamMonitor mMonitorError;
	private OutputStreamMonitor mMonitorOut;
	private InputStreamMonitor mMonitorIn;
	private PrintWriter mOut;
	ILaunch mLaunch;
	PerlDB mDebugger;
	String mLabel;
	RemotePort mIOStream;
	RemotePort mErrorStream;
			
	public DebuggerProxy(PerlDB fDebugger,String fLabel) throws InstantiationException
	{
		mDebugger = fDebugger;
		mLabel = fLabel;
		mLaunch = fDebugger.getLaunch();
		
			
		int port ;
		
		port = Integer.parseInt(getAttribute(PerlLaunchConfigurationConstants.ATTR_DEBUG_IO_PORT));
		mIOStream = new RemotePort(port);
		mIOStream.startConnect();
		mDebugger.redirectIO(port);
		if( ! mIOStream.waitForConnect(true) )
			throw new InstantiationException("Could not Create Connection to Debugger on Port("+port+")");
		
		port =  Integer.parseInt(getAttribute(PerlLaunchConfigurationConstants.ATTR_DEBUG_ERROR_PORT));
		mErrorStream = new RemotePort(port);
		mErrorStream.startConnect();
		mDebugger.redirectError(port);
		if( ! mErrorStream.waitForConnect(true) )
					throw new InstantiationException("Could not Create Connection to Debugger on Port("+port+")");
					
		mMonitorIn = new InputStreamMonitor(mIOStream.getOutStream());
		mMonitorOut = new OutputStreamMonitor(mIOStream.getInStream());
		mMonitorError = new OutputStreamMonitor(mErrorStream.getInStream());
		mMonitorIn.startMonitoring();
		mMonitorOut.startMonitoring();
		mMonitorError.startMonitoring();
		
		mOut = mIOStream.getWriteStream();
		
		fireCreationEvent();
		
		}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#getLabel()
	 */
	public String getLabel() {
		return mLabel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#getLaunch()
	 */
	public ILaunch getLaunch() {
		return mLaunch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String key, String value) {
		mLaunch.setAttribute(key,value);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		return mLaunch.getAttribute(key);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IProcess#getExitValue()
	 */
	public int getExitValue() throws DebugException {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(IDebugTarget.class)) {
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (this.equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		}
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return mDebugger.canTerminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return mDebugger.isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		mDebugger.terminate();
		shutdown();
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getErrorStreamMonitor()
	 */
	public IStreamMonitor getErrorStreamMonitor() {
		return mMonitorError;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getOutputStreamMonitor()
	 */
	public IStreamMonitor getOutputStreamMonitor() {
		return mMonitorOut;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStreamsProxy#write(java.lang.String)
	 */
	public void write(String input) throws IOException {
		mOut.print(input);
	}
	
	/**
	 * Fire a debug event marking the creation of this element.
	 */
		private void fireCreationEvent() {
			fireEvent(new DebugEvent(this, DebugEvent.CREATE));
		}

		/**
		 * Fire a debug event
		 */
		private void fireEvent(DebugEvent event) {
			DebugPlugin manager= DebugPlugin.getDefault();
			if (manager != null) {
				manager.fireDebugEventSet(new DebugEvent[]{event});
			}
		}

	/**
		 * Fire a debug event marking the termination of this process.
		 */
		private void fireTerminateEvent() {
			fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		}

	void shutdown()
	{
		mMonitorError.kill();
		mMonitorOut.kill();
		mMonitorIn.close();
		mOut.close();
		mIOStream.shutdown();
		mErrorStream.shutdown();
	    fireTerminateEvent();
	}
}