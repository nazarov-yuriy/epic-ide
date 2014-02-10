package org.epic.core.model;

import org.epic.core.parser.PerlToken;

/**
 * An ISourceElement representing a "use constant CONSTANT => VALUE" statement.
 * 
 * @author nazarov
 */
public class Constant implements IPackageElement
{
    private final Package parent;
    private final int index;
    private final PerlToken name;
    
    public Constant(Package parent, int index, PerlToken name)
    {
        this.parent = parent;
        this.index = index;
        this.name = name;
    }
    
    public int getIndex()
    {
        return index;
    }

    public int getLength()
    {
        return name.getLength();
    }

    public String getName()
    {
        return name.getText();
    }
    
    public PerlToken getNameToken()
    {
        return name;
    }

    public int getOffset()
    {
        return name.getOffset();
    }
    
    public Package getParent()
    {
        return parent;
    }
    
    public String toString()
    {
        return "constant #" + index + " " + getName() + " @" + getOffset(); 
    }
}
