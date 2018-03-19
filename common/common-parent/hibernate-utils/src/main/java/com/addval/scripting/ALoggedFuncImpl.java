package com.addval.scripting;

import org.apache.log4j.Logger;

import com.addval.scripting.ScriptParser.Function;
import com.addval.utils.Duration;
import com.addval.utils.LogMgr;

public abstract class ALoggedFuncImpl extends AFunctionImpl{

	private static final transient Logger _logger = LogMgr.getLogger(ALoggedFuncImpl.class);

	public ALoggedFuncImpl(Function funcInfo) {
		super(funcInfo);
	}

    /**
     * Creates a new instance of ALoggedFuncImpl
     */
    public ALoggedFuncImpl(ALoggedFuncImpl otherFunc)
    {
       super(otherFunc);
    }

	public final String executeImpl(ALoggedAppData request)
    {
        try{
            Duration duration = new Duration();
            request.addExecResult(new FnExecResultInfo(this.getFuncName()));

            duration.startNow();
            String retStr = this.processImpl(request);
            duration.endNow();

            request.getLastExecResult().setResult(retStr);
            request.getLastExecResult().setExecTime(duration.getMilliSeconds());

            if(_logger.isDebugEnabled())
                _logger.debug("Process time for func " + getFuncName() + " is "
                    + duration.getMilliSeconds() + " milliseconds");

            return retStr;
        }
        catch(RuntimeException re){
            _logger.error(re);
            throw re;
        }
    }

	public abstract String processImpl(ALoggedAppData request) ;

	public String executeImpl(AppData request){
		return executeImpl((ALoggedAppData)request);
	}


}
