package org.epic.perleditor.actions;

import java.util.Iterator;

import org.eclipse.jface.text.*;
import org.epic.core.model.Package;
import org.epic.core.model.SourceFile;
import org.epic.core.model.Constant;
import org.epic.perleditor.editors.PartitionTypes;

/**
 * 
 * @author nazarov
 */
class OpenConstantDeclaration extends AbstractOpenDeclaration
{
    //~ Constructors

    public OpenConstantDeclaration(OpenDeclarationAction action)
    {
        super(action);
    }
    
    //~ Methods
    
    protected IRegion findDeclaration(SourceFile sourceFile, String constantName)
    {
        for (Iterator<Package> i = sourceFile.getPackages().iterator(); i.hasNext();)
        {
        	Package sub = i.next();
        	Constant constant = sub.getConstant(constantName);
            if (constant != null)
                return new Region(constant.getOffset(), constant.getLength());
        }
        return null;
    }
    
    protected String getLocalSearchString(String searchString)
    {
        int lastSepIndex = searchString.lastIndexOf("::");
        return lastSepIndex != -1
            ? searchString.substring(lastSepIndex + 2) : null;
    }
    
    protected String getSearchString(ITextSelection selection)
    {
        return getSelectedModuleName(selection);
    }
    
    protected String getTargetModule(String moduleName)
    {
        if (moduleName.indexOf("::") != -1)
        {
            int lastSepIndex = moduleName.lastIndexOf("::");
            return moduleName.substring(0, lastSepIndex);
        }
        else return null;
    }
    
    /**
     * Returns the currently selected module name.
     * <p>
     * If the supplied selection's length is 0, the offset is treated as the
     * caret position and the enclosing partition is returned as the module
     * name.
     * 
     * @return selected module name or null if none is selected
     */
    private String getSelectedModuleName(ITextSelection selection)
    {
        // Note that we rely heavily on the correct partitioning delivered
        // by PerlPartitioner. When in doubt, fix PerlPartitioner instead of
        // adding workarounds here.

        IDocument doc = getSourceDocument();

        try
        {
            ITypedRegion partition = PartitionTypes.getPerlPartition(doc, selection.getOffset());
            if (!partition.getType().equals(PartitionTypes.DEFAULT)) return null;
            else
            {
                String moduleName =
                    doc.get(partition.getOffset(), partition.getLength());
                return moduleName;
            }
        }
        catch (BadLocationException e)
        {
            return null; // should never happen
        }
    }
}
