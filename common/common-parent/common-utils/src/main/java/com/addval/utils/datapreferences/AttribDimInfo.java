/*
 * AttribDimInfo.java
 *
 * Created on May 2, 2007, 11:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

/**
 *
 * @author ravi
 */
public class AttribDimInfo implements java.io.Serializable
{
    protected String _dim;
    protected String _wildCard;
    protected String _attribCodeCalcImplClass;
    protected IAttribCodeCalculator _attribCodeCalculator;
    
    /**
     * Creates a new instance of AttribDimInfo
     */
    public AttribDimInfo() {
    }

    public String getDim() {
        return _dim;
    }

    public void setDim(String dim) {
        this._dim = dim;
    }

    public String getWildCard() {
        return _wildCard;
    }

    public void setWildCard(String wildCard) {
        this._wildCard = wildCard;
    }

    public String getAttribCodeCalcImplClass() {
        return _attribCodeCalcImplClass;
    }

    public void setAttribCodeCalcImplClass(String attribCodeCalcImplClass) {
        this._attribCodeCalcImplClass = attribCodeCalcImplClass;
    }
    
    public String toString()
    {
        return "AttribDimInfo[dim=" + _dim +
            "; wildCard=" + _wildCard +
            "; implClass=" + _attribCodeCalcImplClass + "]";
    }

    public IAttribCodeCalculator getAttribCodeCalculator() {
        return _attribCodeCalculator;
    }

    public void setAttribCodeCalculator(IAttribCodeCalculator attribCodeCalculator) {
        this._attribCodeCalculator = attribCodeCalculator;
    }
    
}
