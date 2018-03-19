/*
 * ScriptParser.java
 *
 * Created on December 14, 2006, 1:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.scripting;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.lang.reflect.Constructor;

/**
 *
 * @author ravi
 */
public class ScriptParser 
{    
    /** Creates a new instance of ScriptParser */
    ScriptParser() {
    }
    
    public static ScriptInterpreter parse(String script) throws ScriptException
    {
        try{
            ScriptParser sp = new ScriptParser(script);
            sp.parse();
            Map funcMap = sp.createFunctionInstances();
            ScriptInterpreter interpreter = 
                new ScriptInterpreter(
                    new FunctionClassFactory(funcMap),
                    sp.getLoopVars(), 
                    sp.functionMap,
                    sp.getSteps()
                );
                       
            return interpreter;
        }
        catch(Exception e){
            throw new ScriptException("Parsing Error: " + e.getMessage());
        }        
    }
    

    public static ScriptInterpreter parse(InputStream inputStream) throws ScriptException, java.io.IOException
    {
        InputStreamReader isr = null;
        
        try{
            isr = new InputStreamReader(inputStream);

            String s = null;
            StringBuffer sb = new StringBuffer();
            char[] chars = new char[256];
            int len = 0;
            while((len = isr.read(chars)) > 0){
                sb.append(chars, 0, len);
            }
            s = sb.toString();
            
            return parse(s);       
        }
        finally{
            inputStream.close();
            isr.close();
        }
    }        
    
    public static ScriptInterpreter parse(URL url) throws ScriptException, java.io.IOException
    {
        InputStream is = url.openStream();
        return parse(is);       
    }    
    
    public static ScriptInterpreter parse(File file) throws ScriptException, java.io.IOException
    {
        FileInputStream fis = new FileInputStream(file);
        return parse(fis);       
    }    

    
    private static final String FUNCTION = "function";
    private static final String LOOPVARS = "loopvars";
    private static final String LOCALFUNCTION = "localfunction";
    private static final String LOOP = "loop";
    private static final String BREAK = "break";
    private static final String CONFIGPARAM = "configparam";
    private static final String CLASS = "class";
    private static final String REUSE = "reuse";
    private static final String END = "end";
    private static final String RESULT = "result";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String RETURN = "return";
    private static final String DEFINE = "define";
    private static final String LABEL = "label";
    
    private static final String BRACE_BEGIN = "{";
    private static final String BRACE_END = "}";
    private static final String PARAN_BEGIN = "(";
    private static final String PARAN_END = ")";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SEMI_COLON = ";";
    private static final String COMMENT_BEGIN = "#";
    
    private static final char COMMENT_BEGIN_CHAR = '#';
        
    private static String[] keyChars = new String[]{BRACE_BEGIN, BRACE_END, 
        PARAN_BEGIN, PARAN_END, COLON, SEMI_COLON};
        
    int len;
    int idx;
    String s;
    char c;
    String nextToken;
    int curCharIdxInLine = 1;
    int curLine = 1;
    int curTokenIdxInLine = 1;
    int curTokenLine = 1;
    boolean commentStarted = false;
    
    //key function name, value function
    private HashMap functionMap = new HashMap();
    private HashSet loopvars;
    private HashMap definesMap = new HashMap();
    private StepList steps = new StepList(true);
    
    ScriptParser(String s){
        this.s = s;
        len = this.s.length();
        idx = -1;
        getNextChar();
    }
    
    Map createFunctionInstances() throws Exception
    {
        HashMap map = new HashMap();
        Map.Entry entry;
        Function func;
        AFunctionImpl funcImpl;
        
        for(Iterator iter = functionMap.entrySet().iterator(); iter.hasNext();){
            entry = (Map.Entry)iter.next();
            func = (Function)entry.getValue();
            if(func.isLocalFunction())continue;
            Class funcClass = Class.forName(func.getImplClass());
            Constructor ctr = funcClass.getConstructor(new Class[] {Function.class});
            funcImpl = (AFunctionImpl)ctr.newInstance(new Object[] {func});
            map.put(func.getName(), funcImpl);
        }
        
        return map;
    }
    
    Map getFunctionMap(){
        return functionMap;
    }
    
    Set getLoopVars(){
        return loopvars;
    }
    
    StepList getSteps(){
        return steps;
    }
        
    /**
     * function abcd class:aaaa, reuse:true result:a,b,c
     * {
     *   configparam abcd(abcd, abcd)
     *   configparam abcd(abcd, abcd, abcd)
     * }
     *
     * function abcd class:aaaa, reuse:true;
     *
     * loopvars abcd, abcd;
     *     
     * localfunction abcd {
     *   abcd;
     *   loop abc{abcd;}
     *   return a;
     * }
     *
     * {
     *   abcd;
     *
     *   loop{
     *     abcd;
     *   }
     *   abcd{result:result1 {abcd;} result:result2 break; }
     *   loop abcd{
     *     abcd;
     *   }
     *   abcd {
     *     result:result1{
     *        abcd;
     *     }
     *     result:result2 break;
     *     result:result3 end;
     *   }
     * }
     *
     * key words: function, localfunction, loopvars, loop, break, configparam, class, reuse, end, result 
     * key chars: { } ( ) , : ;
     * comment begin char #, rest of the line ignored
     * comma separated list must be on the same line
     * grammar
     * P --> F+ LV? SD* LF* { S+ }
     * F --> function N class : N, reuse : true|false [result : RV]? [C|;]?
     * SD --> define N  ^[\n]+
     * LF --> localfunction N { S+ }
     * RV --> <comma separated result value list>
     * C --> { configparam N ( V )+ }
     * V --> <comma separated value list>
     * LV --> loopvars <comma separated name list>  ;
     * S --> [end ; | break ; | return N ; | FN | L | label N ; | resume N ;]
     * L --> loop N? { S+ }
     * FN --> N AL? [R|;]
     * AL --> (<comma separated defines>)
     * R --> { T+ }
     * T --> result : N [{ S+ }|break;|end;]
     * N --> [a-z|A-Z].[a-z|A-Z|0-9|_]*
     */            
    private void parse() throws ScriptException{
        processFunctions();
        if(equ(LOOPVARS, nextToken))processLoopVars();
        while(equ(DEFINE, nextToken))processDefine();        
        if(equ(LOCALFUNCTION, nextToken))processLocalFunctions();
        //the next token must be {
        if(!equ(BRACE_BEGIN, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "{ expected at the beginning of script body");
        processSteps(steps);
    }
    
    private void skipSpace(){       
        while(c != 0 && (Character.isWhitespace(c) || commentStarted))getNextChar();
    }
    
    private void getNextToken(){
        if(c == 0){
            nextToken = null;
            return;
        }
        
        skipSpace();
        int startIdx = idx;
        curTokenLine = curLine;
        curTokenIdxInLine = curCharIdxInLine;
        
        //first check if the next token is a key char, if yes that is the key char
        if(keyChar()){
            getNextChar();
        }
        else{
            while(c != 0 && !(Character.isWhitespace(c) || keyChar()))getNextChar();
        }
        nextToken = s.substring(startIdx, idx);        
    }  
    
    private void getNextTokenIgnoreSpace(){
        if(c == 0){
            nextToken = null;
            return;
        }
        
        skipSpace();
        int startIdx = idx;
        
        //first check if the next token is a key char, if yes that is the key char
        if(keyChar()){
            getNextChar();
        }
        else{
            while(c != 0 && !keyChar())getNextChar();
        }
        nextToken = s.substring(startIdx, idx);        
    }
    
    /** get the end of line */
    private void getNextTokenEOL(){
        if(c == 0){
            nextToken = null;
            return;
        }
        
        skipSpace();
        int startIdx = idx;
        
        while(c != '\n')getNextChar();
        
        nextToken = s.substring(startIdx, idx);        
    }    
    
    private void getNextChar(){
        if(idx >= len)return;
        if(idx < len-1)c = s.charAt(++idx);
        else{++idx; c = 0;}
        if(c == COMMENT_BEGIN_CHAR)commentStarted = true;
        curCharIdxInLine++;
        if(c == '\n'){commentStarted = false; curLine++; curCharIdxInLine = 1;}
    }
             
    private void processFunctions() throws ScriptException{
        getNextToken();
            
        while(equ(FUNCTION, nextToken)){
            processFunction();
            getNextToken();
        }
        if(functionMap.size() < 1)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "At least one function definition required");
    }
    
    private void processFunction() throws ScriptException
    {        
        //get func name
        getNextToken();
        Function f = new Function(nextToken);
        functionMap.put(f.getName(), f);
        getNextToken();
        if(!equ(nextToken, CLASS))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "keyword class expected");
        getNextToken();
        if(!equ(nextToken, COLON))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + ": after class expected");
        getNextToken();
        f.setImplClass(nextToken);
        getNextToken();
        if(!equ(nextToken, REUSE))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "keyword reuse expected");
        getNextToken();
        if(!equ(COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + ": after reuse expected");
        getNextToken();
        if(!equ(nextToken, TRUE) && !equ(nextToken, FALSE))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "keyword true or false expected");
        f.setReuse(Boolean.valueOf(nextToken).booleanValue());
        getNextToken();
        if(equ(RESULT, nextToken))processFuncResults(f);
        if(equ(SEMI_COLON, nextToken))return;
        else if(equ(BRACE_BEGIN, nextToken)){
            processConfigParams(f);
        }
        else throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "{ or ; is expected");
    }
    
    void processFuncResults(Function f)
    {
        getNextToken();
        if(!equ(COLON, nextToken))throw new RuntimeException(": expected after result keyword in function declaration");
        HashSet values = new HashSet();
        getNextTokenIgnoreSpace();
        if(empty(nextToken))throw new RuntimeException("at least one result value expected in function declaration");
        String[] vals = nextToken.split(",");
        for(int i = 0; i < vals.length; i++)values.add(vals[i].trim());  
        f.setResults(values);
        getNextToken();
    }        
    
    private void processConfigParams(Function f) throws ScriptException
    {        
        getNextToken();
        if(!equ(nextToken, CONFIGPARAM))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "keyword configparam expected");
        while(equ(nextToken, CONFIGPARAM)){
            processConfigParam(f);
            getNextToken();
        }       
        if(f.getCfgParamSize() == 0)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "at least one configparam section expected");
        if(!equ(BRACE_END, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "} after configparam section");
    }
    
    private void processConfigParam(Function f) throws ScriptException
    {
        getNextToken();
        String paramName = nextToken;
        getNextToken();
        if(!equ(PARAN_BEGIN, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "( after configparam name expected");
        HashSet values = new HashSet();
        getNextTokenIgnoreSpace();
        if(empty(nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "at least one configparam value expected");
        String[] vals = nextToken.split(",");
        for(int i = 0; i < vals.length; i++)values.add(vals[i].trim());  
        f.addCfgParam(paramName, values);
        getNextToken();
        if(!equ(PARAN_END, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + ") after configparam values expected");
    }
    
    private void processDefine() throws ScriptException{
        getNextToken(); //get defined var
        if(nextToken == null || nextToken.length() == 0)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "define must be followed by a proper identifier");
        String defid = nextToken;
        //get the rest of the line as token
        getNextTokenEOL();
        String val = nextToken.trim();
        definesMap.put(defid, val);
        getNextToken();
    }
             
    private void processLoopVars() throws ScriptException{
        getNextTokenIgnoreSpace();
        if(empty(nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "at least one loop var name expected after keyword loopvars");
        String[] vars = nextToken.split(",");
        loopvars = new HashSet();
        for(int i = 0; i < vars.length; i++)loopvars.add(vars[i].trim());
        getNextToken();
        if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "loopvars declaration must end with ;");
        getNextToken(); //it will be {
    }
    
    private void processLocalFunctions() throws ScriptException{
        while(equ(LOCALFUNCTION, nextToken)){
            processLocalFunction();
            getNextToken();
        }        
    }
    
    private void processLocalFunction() throws ScriptException{        
        getNextToken(); //local func name
        if(functionMap.containsKey(nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "localfunction name is already declared");
        Function lf = new Function(nextToken);   
        lf.setLocalFunction(true);
        functionMap.put(lf.getName(), lf);
        getNextToken();
        if(!equ(BRACE_BEGIN, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "localfunction must be followed by script block {");        
        processSteps(lf.getSteps());
    }    
                 
    private void processSteps(StepList steps) throws ScriptException{
     
        getNextToken();
        while(!equ(BRACE_END, nextToken) && c != 0){
            Step step = steps.addStep();
            processStep(step);
            if(step.isLabel()){
                if(!steps.isMainBlock()){
                    throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "label only supported in the main block");
                }
                if(!steps.addLabelName(step.getLabelName())){
                    throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "Duplicate label name: " + step.getLabelName());
                }
            }
            getNextToken();
        }  
        if(!equ(BRACE_END, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "script block must terminate in }");
        if(steps.size() < 1)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "At least one step required in script block");
    } 
    
    private void processStep(Step step) throws ScriptException
    {                        
        //next token can be either a func or loop or break or end
        if(equ(nextToken, BREAK)){
            step.setIsBreak(true);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword break");
        }
        else if(equ(nextToken, END)){
            step.setIsEnd(true);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword end");            
        }
        else if(equ(nextToken, RETURN)){
            step.setIsReturn(true);
            getNextToken(); //get return value
            step.setReturnValue(nextToken);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword end");            
        }
        else if(equ(nextToken, LABEL)){
            step.setIsLabel(true);
            getNextToken(); //get return value
            step.setLabelName(nextToken);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword end");            
        }       
        else if(equ(nextToken, LOOP)){    
            step.setIsLoop(true);
            Loop l = new Loop();
            step.setLoop(l);
            processLoop(l);
        }
        else{
            //check if func name defined            
            Function f = (Function)functionMap.get(nextToken);
            if(f == null)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "Function name not declared: " + nextToken);
            FunctionCall fc = new FunctionCall();            
            fc.setName(nextToken);
            step.setFunc(fc);
            processFunctionCall(f, fc);
        }        
    }
    
    private void processLoop(Loop l) throws ScriptException
    {
        getNextToken();
        if(!equ(BRACE_BEGIN, nextToken)){ //loop with loop var
            if(!loopvars.contains(nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "undeclared loopvar " + nextToken);
            l.setLoopVar(nextToken);
            getNextToken();
            if(!equ(BRACE_BEGIN, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "{ expected after loop loopvar");
        }        
        processSteps(l.getSteps());        
    }
    
    private void processFunctionCall(Function f, FunctionCall fc) throws ScriptException
    {
        getNextToken();
        if(equ(PARAN_BEGIN, nextToken)){
            //process func call args
            processFuncCallArgs(fc);
        }        
        if(equ(BRACE_BEGIN, nextToken)){
            processResult(f, fc);
        }
        else if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "( or ; or { expected after a function call");
    }
    
    private void processFuncCallArgs(FunctionCall fc) throws ScriptException
    {
        List args = new ArrayList(2);
        fc.setArgs(args);
        
        //args are of the style ("aaa", "aaa")
        getNextTokenIgnoreSpace();
        if(nextToken != null && nextToken.trim().length() != 0){
            String[] vals = nextToken.split(",");
            String defid;
            for(int i = 0; i < vals.length; i++){
                defid = vals[i].trim();
                if(!definesMap.containsKey(defid))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "function arg not defined : " + defid);
                args.add(definesMap.get(defid));
            }  
        }
        getNextToken();
        if(!equ(PARAN_END, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + ") expected after a function arg list");
        getNextToken();
    }    
        
    private void processResult(Function f, FunctionCall fc) throws ScriptException
    {
        getNextToken();
        if(equ(BRACE_END, nextToken)){
            if(fc.getResultSize() < 1)throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "At least one result required when a block { is started after a function call");
            return;
        }  
        
        if(!equ(RESULT, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "keyword result expected after a function name and begin block{");
        getNextToken();
        if(!equ(COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + ": expected after keyword result");
        getNextToken(); //result value
        if(!f.hasResult(nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "Undeclared function result " + nextToken + 
                " used in script for function " + f.getName());
        Result result = new Result();
        result.setValue(nextToken);
        fc.addResult(result);
        getNextToken(); //{ or break or end or func call;
        if(equ(BREAK, nextToken)){
            Step step = result.addStep();
            step.setIsBreak(true);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword break");            
        }
        else if(equ(END, nextToken)){
            Step step = result.addStep();
            step.setIsEnd(true);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword end");                        
        }
        else if(equ(nextToken, RETURN)){
            Step step = result.addStep();
            step.setIsReturn(true);
            getNextToken(); //get return value
            step.setReturnValue(nextToken);
            getNextToken();
            if(!equ(SEMI_COLON, nextToken))throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "; expected after keyword end");            
        }        
        else if(equ(BRACE_BEGIN, nextToken)){
            processSteps(result.getSteps());
        }
        else throw new ScriptException("[" + curTokenLine + ":" + curTokenIdxInLine + "]" + "unexpected token " + nextToken + " after result value"); 
        
        processResult(f, fc);
    }
    
    private boolean empty(String str){
        return str == null || str.length() == 0;
    }
    
    private boolean equ(String s1, String s2){
        return empty(s1) ? empty(s2) :  s1.equals(s2);
    } 
    
    private boolean keyChar(){
        for(int i = 0; i < 6; i++)if(keyChars[i].charAt(0) == c)return true;
        return false;
    }
    
    public static class Function{
        private String _name;
        private String _implClass;
        private boolean _reuse;
        //key = param name, value = set of values
        private HashMap _cfgParams;
        private HashSet _results;
        
        private StepList _steps; //for local function
        private boolean localFunction = false;
        
        Function(String name){
            _name = name;
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }

        public String getImplClass() {
            return _implClass;
        }

        public void setImplClass(String implClass) {
            this._implClass = implClass;
        }

        public boolean isReuse() {
            return _reuse;
        }

        public void setReuse(boolean reuse) {
            this._reuse = reuse;
        }
        
        public void addCfgParam(String key, Set vals)
        {
            if(_cfgParams == null)_cfgParams = new HashMap();
            _cfgParams.put(key, vals);
        }
        
        public Map getCfgParamMap()
        {
            return _cfgParams;
        }
        
        public int getCfgParamSize()
        {
            return _cfgParams == null ? 0 : _cfgParams.size();
        }
        
        public void addResult(String result){
            if(_results == null)_results = new HashSet();
            _results.add(result);
        }
        
        public void setResults(HashSet results){
            _results = results;
        }
        
        public Set getResults(){
            return _results;
        }
        
        public boolean hasResult(String s){
            //if no results specified then any result is valid
            return _results == null ? true : _results.contains(s);
        } 
                
        public StepList getSteps() {
            if(_steps == null)_steps = new StepList();
            return _steps;
        }   
        
        public Step addStep() {
            return getSteps().addStep();
        }        

        public boolean isLocalFunction() {
            return localFunction;
        }

        public void setLocalFunction(boolean localFunction) {
            this.localFunction = localFunction;
        }
    }
    
    public static class Step{
        private boolean _isLoop = false;
        private Loop _loop;
        private FunctionCall _func;
        private boolean _isBreak = false;
        private boolean _isEnd = false;
        private boolean _isReturn = false;
        private String _returnValue;
        private boolean _isLabel = false;
        private String _labelName;
        
        Step(){
        }
        
        public void setIsLoop(boolean val){_isLoop = val;}
        public boolean isLoop(){return _isLoop;}

        public Loop getLoop() {
            return _loop;
        }

        public void setLoop(Loop loop) {
            this._loop = loop;
        }

        public FunctionCall getFunc() {
            return _func;
        }

        public void setFunc(FunctionCall func) {
            this._func = func;
        }

        public boolean isBreak() {
            return _isBreak;
        }

        public void setIsBreak(boolean isBreak) {
            this._isBreak = isBreak;
        }

        public boolean isEnd() {
            return _isEnd;
        }

        public void setIsEnd(boolean isEnd) {
            this._isEnd = isEnd;
        }

        public boolean isReturn() {
            return _isReturn;
        }

        public void setIsReturn(boolean isReturn) {
            this._isReturn = isReturn;
        }

        public String getReturnValue() {
            return _returnValue;
        }

        public void setReturnValue(String returnValue) {
            this._returnValue = returnValue;
        }

        public boolean isLabel() {
            return _isLabel;
        }

        public void setIsLabel(boolean isLabel) {
            this._isLabel = isLabel;
        }

        public String getLabelName() {
            return _labelName;
        }

        public void setLabelName(String labelName) {
            this._labelName = labelName;
        }
    }
    
    public static class StepList
    {
        private ArrayList _steps  = new ArrayList();
        private HashSet _labelNames = new HashSet();
        private boolean _isMainBlock = false;
        
        StepList() {}
        
        StepList(boolean isMainBlock){
            _isMainBlock = isMainBlock;
        }

        public List getSteps() {
            return _steps;
        }

        public Set getLabelNames() {
            return _labelNames;
        }
        
        public Step addStep() {
            Step step = new Step();
            _steps.add(step);
            return step;
        }
        
        public boolean addLabelName(String labelName) {
            return _labelNames.add(labelName);
        }

        public boolean isMainBlock() {
            return _isMainBlock;
        }

        public void setIsMainBlock(boolean isMainBlock) {
            this._isMainBlock = isMainBlock;
        }
        
        public int size() {
            return _steps.size();
        }
        
        public Iterator iterator(){
            return _steps.iterator();
        }
        
        public boolean isEmpty(){
            return size() == 0;
        }
    }
    
    public static class Loop{
        private String _loopVar;
        private StepList _steps  = new StepList();
        
        Loop() {}
        
        
        public StepList getSteps() {
            return _steps;
        }
        
        public Step addStep() {
            return _steps.addStep();
        }
        
        public String getLoopVar() {
            return _loopVar;
        }

        public void setLoopVar(String loopVar) {
            this._loopVar = loopVar;
        }
    }
    
    public static class FunctionCall{
        private String _name;
        private List _args; //can be null
        private HashMap _resultMap;
        
        FunctionCall() {}
        
        public void addResult(Result res) {
            if(_resultMap == null)_resultMap = new HashMap();
            _resultMap.put(res.getValue(), res);
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            this._name = name;
        }
        
        public void setArgs(List args) {
            _args = args;
        }
        
        public List getArgs() {
            return _args;
        }        
        
        public int getResultSize() {
            return _resultMap == null ? 0 : _resultMap.size();
        }
        
        public Map getResultMap() {
            return _resultMap;
        }        
    }
    
    public static class Result{
        private String _value;
        private StepList _steps = new StepList();
        
        Result() {}
        
        public void setValue(String val) {
            _value = val;
        }
        
        public String getValue() {
            return _value;
        }

        public StepList getSteps() {
            return _steps;
        }
        
        public Step addStep() {
            return _steps.addStep();
        }
    }    
}
