package org.epic.core.model;

import java.util.*;

import org.eclipse.jface.text.BadLocationException;
import org.epic.core.parser.CurlyToken;
import org.epic.core.parser.PerlToken;

/**
 * IMultilineElement representing a "package" declaration in a source file,
 * or the implicit "main" package. Other source file elements defined within
 * the scope of the package are maintained as subelements within the Package
 * object.
 * <p>
 * The identity of the Package object only reflects its relative position in
 * the ordered list of "package" keywords present in the source file.
 * This is to support incremental updates while reparsing the file and
 * to optimise performance of {@link TreeViewer#refresh}, which disfavours
 * models that change contained objects' identities. Consequently, reordering
 * package keywords within a source file is likely to suffer from low
 * performance (this is supposed to be a rarely occuring case).
 * <p>
 * The Package element logically spans multiple lines, so that all
 * elements belonging to the package are fully contained between its first
 * and last line. The first line is the one containing the "package" keyword.
 * The last line is the one containing the terminating } of the enclosing block,
 * or the line containing the last token before the next "package" keyword
 * in the same block, or the last line in file, whichever comes first.
 * 
 * @author jploski
 */
public class Package implements IMultilineElement
{
    private final int index;
    private final int blockLevel;
    private final PerlToken packageKeyword;
    private final PerlToken name;
    private final List subs;
    private final List uses;
    private final List parents;
    private final HashMap<String, Constant> constants;
    private PerlToken lastToken;
    
    /**
     * Creates the default ("main") package.
     */
    public Package()
    {
        this.index = 0;
        this.blockLevel = 0;
        this.packageKeyword = null;
        this.name = null;
        this.subs = new ArrayList();
        this.uses = new ArrayList();
        this.parents = new ArrayList();
        this.constants = new HashMap<String, Constant>();
    }
    
    /**
     * Creates a named package.
     */
    public Package(
        int index,
        int blockLevel,
        PerlToken packageKeyword,
        PerlToken name)
    {
        this.index = index;
        this.blockLevel = blockLevel;
        this.packageKeyword = packageKeyword;
        this.name = name;
        this.subs = new ArrayList();
        this.uses = new ArrayList();
        this.parents = new ArrayList();
        this.constants = new HashMap<String, Constant>();
    }
    
    public Subroutine addSub(
        PerlToken subKeyword,
        PerlToken name,
        CurlyToken openCurly)
    {
        Subroutine ret = new Subroutine(
            this, subs.size(), subKeyword, name, openCurly);
        subs.add(ret);
        return ret;
    }
    
    public ModuleUse addUse(PerlToken keyword, PerlToken name)
        throws BadLocationException
    {
        ModuleUse ret = new ModuleUse(this, uses.size(), keyword, name);
        uses.add(ret);
        return ret;
    }

    public ModuleUse addParent(PerlToken keyword, PerlToken name)
        throws BadLocationException
    {
        ModuleUse ret = new ModuleUse(this, uses.size(), keyword, name);
        parents.add(ret);
        return ret;
    }
    
    public void addConstant(PerlToken name)
            throws BadLocationException
    {
        Constant constant = new Constant(this, constants.size(), name);
        constants.put(name.getText(), constant);
    }

    public boolean equals(Object obj)
    {
        return obj instanceof Package &&
               index == ((Package) obj).index;
    }
    
    public int getBlockLevel()
    {
        return blockLevel;
    }

    public int getEndLine()
    {
        return lastToken.getLine()-1;
    }

    public int getStartLine()
    {
        return packageKeyword != null ? packageKeyword.getLine()-1 : 0;
    }
    
    public int getIndex()
    {
        return index;
    }

    public int getLength()
    {
        return name != null ? name.getLength() : "main".length();
    }

    public String getName()
    {
        return name != null ? name.getText() : "main";
    }

    public int getOffset()
    {
        return name != null ? name.getOffset() : -1;
    }
    
    public List getSubs()
    {
        return Collections.unmodifiableList(subs);
    }
    
    public Constant getConstant(String constantName)
    {
    	if(constants.containsKey(constantName)){
    		return constants.get(constantName);
    	}else{
    		return null;
    	}
    }
    
    public List getUses()
    {
        return Collections.unmodifiableList(uses);
    }

    public List getParents()
    {
        return Collections.unmodifiableList(parents);
    }

    public int hashCode()
    {
        return index;
    }
    
    public void setLastToken(PerlToken lastToken)
    {
        this.lastToken = lastToken;
    }
}
